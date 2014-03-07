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

import java.io.File;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.log4j.Logger;

import akka.actor.ActorSystem;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mnxfst.stream.config.StreamAnalyzerConfiguration;
import com.mnxfst.stream.listener.StreamEventListener;
import com.mnxfst.stream.listener.StreamEventListenerConfiguration;
import com.mnxfst.stream.server.config.StreamAnalyzerServerConfiguration;
import com.yammer.dropwizard.Service;
import com.yammer.dropwizard.config.Bootstrap;
import com.yammer.dropwizard.config.Environment;

/**
 * Initializes the stream analyzer server and starts it up
 * @author mnxfst
 * @since 06.03.2014
 *
 */
public class StreamAnalyzerServer extends Service<StreamAnalyzerServerConfiguration> {
	
	private static final Logger logger = Logger.getLogger(StreamAnalyzerServer.class.getName());
	
	private ActorSystem rootActorSystem;
	private ExecutorService listenerExecutorService;

	/**
	 * @see com.yammer.dropwizard.Service#initialize(com.yammer.dropwizard.config.Bootstrap)
	 */
	public void initialize(Bootstrap<StreamAnalyzerServerConfiguration> bootstrap) {
	}

	/**
	 * @see com.yammer.dropwizard.Service#run(com.yammer.dropwizard.config.Configuration, com.yammer.dropwizard.config.Environment)
	 */
	public void run(StreamAnalyzerServerConfiguration configuration, Environment environment) throws Exception {
		
		String pipelineConfigurationFilename = configuration.getPipelineConfigurationFile();
		ObjectMapper mapper = new ObjectMapper();
		StreamAnalyzerConfiguration streamAnalyzerConfiguration = mapper.readValue(new File(pipelineConfigurationFilename), StreamAnalyzerConfiguration.class);

		/////////////////////////////////////////////////////////////
		// set up listeners
		// TODO dynamic listners via REST 
		this.listenerExecutorService = Executors.newCachedThreadPool();		
		/////////////////////////////////////////////////////////////
		
		this.rootActorSystem = ActorSystem.create(configuration.getActorSystemId());
		
	}
	
	/**
	 * Initializes the {@link StreamEventListener stream event listeners} contained in the provided configuration
	 * @param listenerConfigurations
	 */
	protected void listenerInitialization(final List<StreamEventListenerConfiguration> listenerConfigurations) {

		// step through configurations
		for(final StreamEventListenerConfiguration listenerCfg : listenerConfigurations) {
			if(listenerCfg != null) {
				
				logger.info("listener [id="+listenerCfg.getId()+", name="+listenerCfg.getName()+", class="+listenerCfg.getListenerClass()+", version=" + listenerCfg.getVersion()+"]");
				
				
//				listenerCfg.getDescription()
//				listenerCfg.getDispatchers()
//				listenerCfg.getId()
//				listenerCfg.getListenerClass()
//				listenerCfg.getName()
//				listenerCfg.getSettings()
//				listenerCfg.getVersion()
			}
		}
		
	}
	
	
	/**
	 * Ramps up the server
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
		(new StreamAnalyzerServer()).run(args);
	}
	

}
