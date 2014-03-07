/**
 *  Copyright 2014 Christian Kreutzfeldt
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
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
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

import java.io.File;
import java.util.List;
import java.util.Map;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.PosixParser;
import org.apache.log4j.Logger;
import org.codehaus.jackson.map.ObjectMapper;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;

import com.mnxfst.stream.config.StreamAnalyzerConfiguration;
import com.mnxfst.stream.directory.ComponentRegistry;
import com.mnxfst.stream.dispatcher.config.StreamEventMessageDispatcherConfiguration;
import com.mnxfst.stream.listener.StreamEventListenerConfiguration;

/**
 * Initializes the stream analyzer server and starts it up
 * @author mnxfst
 * @since 06.03.2014
 *
 */
public class StreamAnalyzerServer  {
	
	private static final Logger logger = Logger.getLogger(StreamAnalyzerServer.class.getName());
	
	private ActorSystem rootActorSystem;

	public void run(final String configurationFilename, final int port) throws Exception {

		// set up  the actor runtime environment
		this.rootActorSystem = ActorSystem.create("streamanalyzer");
		

		ObjectMapper mapper = new ObjectMapper();
		StreamAnalyzerConfiguration streamAnalyzerConfiguration = mapper.readValue(new File(configurationFilename), StreamAnalyzerConfiguration.class);

		/////////////////////////////////////////////////////////////
		// set up listeners
		// TODO dynamic listners via REST 
		
		/////////////////////////////////////////////////////////////
		
		
		EventLoopGroup bossGroup = new NioEventLoopGroup(); // (1)
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap b = new ServerBootstrap(); // (2)
            b.group(bossGroup, workerGroup)
             .channel(NioServerSocketChannel.class) // (3)
             .childHandler(new ChannelInitializer<SocketChannel>() { // (4)
                 @Override
                 public void initChannel(SocketChannel ch) throws Exception {
                     ch.pipeline().addLast(new StreamAnalyzerStatsHandler());
                 }
             })
             .option(ChannelOption.SO_BACKLOG, 128)          // (5)
             .childOption(ChannelOption.SO_KEEPALIVE, true); // (6)

            // Bind and start to accept incoming connections.
            ChannelFuture f = b.bind(port).sync(); // (7)

            // Wait until the server socket is closed.
            // In this example, this does not happen, but you can do that to gracefully
            // shut down your server.
            f.channel().closeFuture().sync();
        } finally {
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }		
	
	}
	
	/**
	 * Initializes the component registry
	 * @return
	 */
	protected ActorRef componentRegistryInitialization() {		
		return this.rootActorSystem.actorOf(Props.create(ComponentRegistry.class), "componentRegistry");		
	}
	
	/**
	 * Initializes the {@link StreamEventListener stream event listeners} contained in the provided configuration
	 * @param listenerConfigurations
	 */
	protected void listenerInitialization(final List<StreamEventListenerConfiguration> listenerConfigurations, final ActorRef componentRegistryRef) throws Exception {

		// step through configurations
		for(final StreamEventListenerConfiguration listenerCfg : listenerConfigurations) {
			if(listenerCfg != null) {
				logger.info("listener [id="+listenerCfg.getId()+", name="+listenerCfg.getName()+", class="+listenerCfg.getListenerClass()+", version=" + listenerCfg.getVersion()+"]");
				this.rootActorSystem.actorOf(Props.create(Class.forName(listenerCfg.getListenerClass()), listenerCfg, componentRegistryRef), listenerCfg.getId());
			}
		}		
	}
	
	protected void dispatcherInitialization(final List<StreamEventMessageDispatcherConfiguration> dispatcherConfigurations, final ActorRef componentRegistryRef) throws Exception {
		
		// step through configurations
		for(final StreamEventMessageDispatcherConfiguration dispCfg : dispatcherConfigurations) {
			
		}
		
	}
	
	
	/**
	 * Ramps up the server
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
		
		CommandLineParser parser = new PosixParser();
		CommandLine  cl = parser.parse(getOptions(), args);
		if(!cl.hasOption("cfg") || !cl.hasOption("port")) {
			HelpFormatter hf = new HelpFormatter();
			hf.printHelp("java " + StreamAnalyzerServer.class.getName(), getOptions());
			return;
		}

		(new StreamAnalyzerServer()).run(cl.getOptionValue("cfg"), Integer.parseInt(cl.getOptionValue("port")));
	}
	
	/**
	 * Return command-line options
	 * @return
	 */
	protected static Options getOptions() {		
		Options opts = new Options();
		opts.addOption("cfg", true, "Stream analyzer configuration for listeners, dispatchers, pipelines, ...");
		opts.addOption("port", true, "Server port");
		return opts;		
	}
	

}
