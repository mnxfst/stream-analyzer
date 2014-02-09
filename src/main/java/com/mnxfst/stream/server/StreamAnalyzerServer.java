/**
 * Copyright 2014 Christian Kreutzfeldt
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package com.mnxfst.stream.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.PosixParser;
import org.apache.log4j.Logger;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mnxfst.stream.listener.StreamEventListener;
import com.mnxfst.stream.listener.StreamEventListenerConfiguration;
import com.mnxfst.stream.listener.webtrends.WebTrendsStreamListenerConfiguration;
import com.mnxfst.stream.processing.dispatcher.StreamEventDispatcher;
import com.mnxfst.stream.processing.dispatcher.StreamEventDispatcherConfiguration;
import com.mnxfst.stream.processing.pipeline.StreamEventPipelineConfiguration;
import com.mnxfst.stream.processing.pipeline.StreamEventPipelineEntryPoint;

/**
 * Main class starting things up
 * @author mnxfst
 * @since 05.02.2014
 */
public class StreamAnalyzerServer {
	
	private static final Logger logger = Logger.getLogger(StreamAnalyzerServer.class);
	
	/** central actor system */
	private final ActorSystem actorSystem = ActorSystem.create("streamAnalyzer"); 
	/** executor service required for running the web socket reader*/
	private ExecutorService executorService = null;
	/** pipelines receiving traffic from dispatchers */
	private Map<String, ActorRef> pipelines;
	/** dispatchers receiving inbound traffic from listeners */
	private Map<String, ActorRef> dispatchers;
	/** listeners receiving traffic from external source */
	private Map<String, StreamEventListener> listeners;
		
	/**
	 * Initializes and ramps up the stream analyzer server component
	 * @param cfg
	 * @throws Exception
	 */
	protected void run(final StreamAnalyzerServerConfiguration cfg) throws Exception {
				
		// initialize server and ramp it up
		EventLoopGroup bossGroup = new NioEventLoopGroup();
		EventLoopGroup workerGroup = new NioEventLoopGroup();
		try {
			ServerBootstrap b = new ServerBootstrap();
			b.option(ChannelOption.SO_BACKLOG, 1024);
			b.group(bossGroup, workerGroup)
				.channel(NioServerSocketChannel.class)
	            .childHandler(new StreamAnalyzerChannelInitializer());

			Channel ch = b.bind(cfg.getPort()).sync().channel();
			ch.closeFuture().sync();
		} finally {
	    	bossGroup.shutdownGracefully();
	        workerGroup.shutdownGracefully();
		}
	}
	
	protected void initialize(final StreamAnalyzerServerConfiguration configuration) throws ClassNotFoundException {

		// handler to gracefully shutdown the actor system 
		Runtime.getRuntime().addShutdownHook(new Thread() {
		    public void run() {
		    	
		    	for(final String listenerId : listeners.keySet()) {
		    		StreamEventListener listener = listeners.get(listenerId);
		    		if(listener != null)
		    			listener.shutdown();
		    		actorSystem.log().info("[listener="+listenerId+", state=shutdown]");
		    	}		    	
		        actorSystem.shutdown();
		    }
		});

		// init pipelines
		// init dispatchers
		// init listeners
		
		this.pipelines = initializePipelines(configuration.getPipelines());
		logger.info(this.pipelines.size() + " pipelines initalized");
		this.dispatchers = initializeDispatchers(configuration.getDispatchers(), pipelines);
		logger.info(this.dispatchers.size() + " dispatchers initialized");
		this.listeners = initializeListeners(configuration.getListeners(), dispatchers);
		logger.info(this.listeners.size() + " listeners initialized");

		actorSystem.log().info("[listeners="+this.listeners.size()+", dispatchers="+this.dispatchers.size()+", pipelines="+this.pipelines+"]");
		
	}
	
	/**
	 * Initializes the {@link StreamEventListener stream event listeners}, ramps them up and assigns
	 * them to the {@link ExecutorService executor} taking care of it
	 * @param listenerConfigurations
	 * @param dispatchers
	 * @return
	 * @throws ClassNotFoundException 
	 * @throws SecurityException 
	 * @throws NoSuchMethodException 
	 * @throws InvocationTargetException 
	 * @throws IllegalArgumentException 
	 * @throws IllegalAccessException 
	 * @throws InstantiationException 
	 */
	protected Map<String, StreamEventListener> initializeListeners(final List<StreamEventListenerConfiguration> listenerConfigurations,
			Map<String, ActorRef> dispatchers) {
		
		if(listenerConfigurations == null || listenerConfigurations.isEmpty())
			throw new RuntimeException("Missing required listener configuration");
		
		Map<String, StreamEventListener> listeners = new HashMap<>();
		
		for(final StreamEventListenerConfiguration cfg : listenerConfigurations) {
			if(cfg == null)
				continue;
			
			try {
				Class<?> dispatcherClass = Class.forName(cfg.getListenerClassName());
				Constructor<?> constructor = dispatcherClass.getConstructor(WebTrendsStreamListenerConfiguration.class);
				final StreamEventListener listener = (StreamEventListener)constructor.newInstance(cfg);
				
				for(String dispatcherId : cfg.getDispatchers())
					listener.addDispatcherReference(dispatchers.get(dispatcherId));
				listeners.put(cfg.getIdentifier(), listener);
			} catch(Exception e) {
				throw new RuntimeException("Failed to initialize listener '"+cfg.getIdentifier()+"'. Error: " + e.getMessage(), e);
			}
		}
		
		if(listeners.isEmpty())
			throw new RuntimeException("No valid listener configurations found");
		
		this.executorService = Executors.newFixedThreadPool(listeners.size());
		for(final String listenerId : listeners.keySet()) {
			this.executorService.execute(listeners.get(listenerId));
			actorSystem.log().info("[listener="+listenerId+", state=started]");
		}
		
		return listeners;		
	}
	
	/**
	 * Initializes the {@link StreamEventDispatcher dispatchers} according to the provided configurations. It returns a
	 * map holding the {@link StreamEventDispatcherConfiguration#getIdentifier() dispatcher identifier} associated with
	 * the {@link ActorRef dispatcher instance}.
	 * @param dispatcherConfigurations
	 * @param pipelines
	 * @return
	 * @throws ClassNotFoundException 
	 */
	protected Map<String, ActorRef> initializeDispatchers(final List<StreamEventDispatcherConfiguration> dispatcherConfigurations, 
			final Map<String, ActorRef> pipelines) throws ClassNotFoundException {
		
		// check input for any configs 
		if(dispatcherConfigurations == null || dispatcherConfigurations.isEmpty())
			throw new RuntimeException("Missing required dispatcher configuration");

		Map<String, ActorRef> dispatchers = new HashMap<>();		
		// step through configurations
		for(final StreamEventDispatcherConfiguration cfg : dispatcherConfigurations) {
			if(cfg == null)
				continue;
			
			// fetch all 
			Set<String> allPipelinesIdentifiers = new HashSet<>();
			for(String destinationId : cfg.getDestinationPipelines().keySet()) {
				Set<String> pipelineIds = cfg.getDestinationPipelines().get(destinationId);
				if(pipelineIds != null && !pipelineIds.isEmpty())
					allPipelinesIdentifiers.addAll(pipelineIds);
				else
					throw new RuntimeException("Missing pipeline configuration for event source '" + destinationId + "'");
			}
			
			// step through all pipelines and retrieve references towards their entry points
			for(String pipelineId : allPipelinesIdentifiers) {
				if(pipelines.containsKey(pipelineId))
					cfg.addPipeline(pipelineId, pipelines.get(pipelineId));
				else
					throw new RuntimeException("Missing pipeline instance for '"+pipelineId+"'");
			}
				
			final ActorRef dispatcherRef = actorSystem.actorOf(Props.create(Class.forName(cfg.getDispatcherClass()), cfg), cfg.getIdentifier());
			if(dispatcherRef == null)
				throw new RuntimeException("Failed to initialize dispatcher '"+cfg.getIdentifier()+"'");
			dispatchers.put(cfg.getIdentifier(), dispatcherRef);
		}
		
		if(dispatchers.isEmpty())
			throw new RuntimeException("No valid dispatcher configuration found");
		
		return dispatchers;
		
	}
	
	/**
	 * Initialize processing pipelines and return map holding mapping from 
	 * {@link StreamEventPipelineConfiguration#getIdentifier() pipeline identifier} towards
	 * the {@link StreamEventPipelineEntryPoint entry point}  
	 * @param pipelineConfigurations
	 * @return
	 */
	protected Map<String, ActorRef> initializePipelines(final List<StreamEventPipelineConfiguration> pipelineConfigurations) {
		
		if(pipelineConfigurations == null || pipelineConfigurations.isEmpty())
			throw new RuntimeException("Missing required pipeline configurations");
		
		// step through configurations and initialize pipelines
		Map<String, ActorRef> pipelineEntryPoints = new HashMap<>();
		for(final StreamEventPipelineConfiguration cfg : pipelineConfigurations) {
			if(cfg == null)
				continue;
			
			// initialize pipeline entry point
			final ActorRef pipelineEntryPointRef = actorSystem.actorOf(Props.create(StreamEventPipelineEntryPoint.class, cfg), cfg.getIdentifier());
			if(pipelineEntryPointRef != null)
				pipelineEntryPoints.put(cfg.getIdentifier(), pipelineEntryPointRef);
		}
		
		if(pipelineConfigurations.isEmpty())
			throw new RuntimeException("No valid pipeline configuration found");
		
		actorSystem.log().info("[pipelines="+pipelineConfigurations.size()+"]");
		
		return pipelineEntryPoints;		
	}
	
	protected static Options getOptions() {
		Options options = new Options();
		options.addOption("f", true, "Configuration file");
		return options;
	}
	public static void main(String[] args) throws Exception {
		
		CommandLineParser parser = new PosixParser();
		CommandLine cmdLine = parser.parse(getOptions(), args);
		
		if(!cmdLine.hasOption("f")) {
			HelpFormatter formatter = new HelpFormatter();
			formatter.printHelp("java " + StreamAnalyzerServer.class.getName(), getOptions());
			return;
		}

		String configurationFile = cmdLine.getOptionValue("f");
		
		// fetch the configuration from file
		ObjectMapper configFileMapper = new ObjectMapper();
		StreamAnalyzerServerConfiguration cfg = configFileMapper.readValue(new File(configurationFile), StreamAnalyzerServerConfiguration.class);
		if(cfg == null)
			throw new RuntimeException("Failed to read configuration from " + configurationFile);
		
		StreamAnalyzerServer server = new StreamAnalyzerServer();
		server.initialize(cfg);
		server.run(cfg);		
	}
	/*

	public static void main(String[] args) throws Exception {
			StreamAnalyzerServerConfiguration cfg = new StreamAnalyzerServerConfiguration(9090);
		WebTrendsStreamListenerConfiguration wtCfg = new WebTrendsStreamListenerConfiguration("wt-id", "disp-1", WebTrendsStreamAPIListener.class.getName(), 
				"clientId", "clientSecret", "return_all", "select *", "2.0", "2.2");
		cfg.addStreamEventListenerConfiguration(wtCfg);
		
		Set<String> forwards = new HashSet<>();
		forwards.add("es-writer-1");
		StreamEventScriptEvaluatorConfiguration scriptEvalConfig = new StreamEventScriptEvaluatorConfiguration(StreamEventScriptEvaluator.class.getName(), "script-1", "script evaluator", 2, "var result = 'true'", "JavaScript");
		scriptEvalConfig.addForwardingRule("errorsFound", forwards);
				
		StreamEventPipelineConfiguration pipelineCfg = new StreamEventPipelineConfiguration("pipe1","pipeline-1-description", "eval-1");
		pipelineCfg.addErrorHandlingNode("script-1");
		pipelineCfg.addPipelineNode(scriptEvalConfig);
		cfg.addStreamEventPipelineConfiguration(pipelineCfg);
		
		StreamEventDispatcherConfiguration dispatcherCfg = new StreamEventDispatcherConfiguration("disp-1", "dispatcher-1");
		dispatcherCfg.addEventSourcePipeline(WebTrendsStreamSocket.EVENT_SOURCE_ID, "pipe1");
		cfg.addStreamEventDispatcherConfiguration(dispatcherCfg);
		
		ObjectMapper mapper = new ObjectMapper();
		System.out.println(mapper.writeValueAsString(cfg));
		}
		*/
}
