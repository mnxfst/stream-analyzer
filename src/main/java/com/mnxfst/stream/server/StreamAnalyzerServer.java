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
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.PosixParser;

import akka.actor.ActorSystem;
import akka.actor.Props;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mnxfst.stream.processing.dispatcher.StreamEventDispatcher;
import com.mnxfst.stream.processing.dispatcher.StreamEventDispatcherConfiguration;
import com.mnxfst.stream.webtrends.WebTrendsStreamAPIReader;

/**
 * Main class starting things up
 * @author mnxfst
 * @since 05.02.2014
 */
public class StreamAnalyzerServer {
	
	/** central actor system */
	private final ActorSystem actorSystem = ActorSystem.create("streamAnalyzer"); 
	/** executor service required for running the web socket reader*/
	private ExecutorService executorService = null;
	/** web socket reader */
	private WebTrendsStreamAPIReader streamAPIReader = null;
		
	/**
	 * Initializes and ramps up the stream analyzer server component
	 * @param configurationFile
	 * @throws Exception
	 */
	protected void run(final String configurationFile) throws Exception {
		
		// handler to gracefully shutdown the actor system 
		Runtime.getRuntime().addShutdownHook(new Thread() {
		    public void run() {		        
		        actorSystem.shutdown();
		    }
		});
		
		// fetch the configuration from file
		ObjectMapper configFileMapper = new ObjectMapper();
		StreamAnalyzerServerConfiguration cfg = configFileMapper.readValue(new File(configurationFile), StreamAnalyzerServerConfiguration.class);
		if(cfg == null)
			throw new RuntimeException("Failed to read configuration from " + configurationFile);
		
		actorSystem.actorOf(Props.create(StreamEventDispatcher));
		
		this.executorService = Executors.newFixedThreadPool(1);
		this.streamAPIReader = new WebTrendsStreamAPIReader(cfg.getClientId(), cfg.getClientSecret(), 
				cfg.getStreamType(), cfg.getStreamVersion(), cfg.getStreamSchemaVersion(), cfg.getStreamQuery(), dispatcherRef);
		this.executorService.execute(streamAPIReader);
		
		
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
		
		(new StreamAnalyzerServer()).run(cmdLine.getOptionValue("f"));
	}
}
