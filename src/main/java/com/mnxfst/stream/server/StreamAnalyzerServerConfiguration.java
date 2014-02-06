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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;
import com.mnxfst.stream.listener.StreamEventListenerConfiguration;
import com.mnxfst.stream.processing.dispatcher.StreamEventDispatcherConfiguration;
import com.mnxfst.stream.processing.pipeline.StreamEventPipelineConfiguration;

/**
 * Holds configuration for {@link StreamAnalyzerServer}
 * @author mnxfst
 * @since 05.02.2014
 */
@JsonRootName ( value = "serverConfigration" )
public class StreamAnalyzerServerConfiguration implements Serializable {

	private static final long serialVersionUID = 2229639751524073732L;

	/** port to reach the server */
	@JsonProperty ( value = "port", required = true )
	private int port = 9090;
	/** pipeline configurations */
	@JsonProperty ( value = "pipelines", required = true )
	private List<StreamEventPipelineConfiguration> pipelines = new ArrayList<>();
	/** dispatcher configurations */
	@JsonProperty ( value = "dispatchers", required = true )
	private List<StreamEventDispatcherConfiguration> dispatchers = new ArrayList<>();
	/** listener configurations */
	@JsonProperty ( value = "listeners", required = true )
	private List<StreamEventListenerConfiguration> listeners = new ArrayList<>();
	
	/**
	 * Default constructor
	 */
	public StreamAnalyzerServerConfiguration() {		
	}
	
	/**
	 * Initializes the instance using the provided input
	 * @param port
	 */
	public StreamAnalyzerServerConfiguration(final int port) {
		this.port = port;
	}
	
	/**
	 * Adds a new {@link StreamEventPipelineConfiguration pipeline configuration}
	 * @param cfg
	 */
	public void addStreamEventPipelineConfiguration(final StreamEventPipelineConfiguration cfg) {
		this.pipelines.add(cfg);
	}
	
	/**
	 * Adds a new {@link StreamEventDispatcherConfiguration dispatcher configuration}
	 * @param streamEventDispatcherConfiguration
	 */
	public void addStreamEventDispatcherConfiguration(final StreamEventDispatcherConfiguration streamEventDispatcherConfiguration) {
		this.dispatchers.add(streamEventDispatcherConfiguration);
	}
	
	/**
	 * Adds a new {@link StreamEventListenerConfiguration listener configuration}
	 * @param streamEventListenerConfiguration
	 */
	public void addStreamEventListenerConfiguration(final StreamEventListenerConfiguration streamEventListenerConfiguration) {
		this.listeners.add(streamEventListenerConfiguration);
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public List<StreamEventPipelineConfiguration> getPipelines() {
		return pipelines;
	}

	public void setPipelines(List<StreamEventPipelineConfiguration> pipelines) {
		this.pipelines = pipelines;
	}

	public List<StreamEventDispatcherConfiguration> getDispatchers() {
		return dispatchers;
	}

	public void setDispatchers(List<StreamEventDispatcherConfiguration> dispatchers) {
		this.dispatchers = dispatchers;
	}

	public List<StreamEventListenerConfiguration> getListeners() {
		return listeners;
	}

	public void setListeners(List<StreamEventListenerConfiguration> listeners) {
		this.listeners = listeners;
	}

	
}
