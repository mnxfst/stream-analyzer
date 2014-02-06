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
	
	// TODO refactor this to isolated config options
	/** web socket reader configuration */
	@JsonProperty ( value = "clientId", required = true )
	private String clientId = null;
	@JsonProperty ( value = "clientSecret", required = true )
	private String clientSecret = null;
	@JsonProperty ( value = "streamType", required = true )
	private String streamType = null;
	@JsonProperty ( value = "streamVersion", required = true )
	private String streamVersion = null;
	@JsonProperty ( value = "streamSchemaVersion", required = true )
	private String streamSchemaVersion = null;
	@JsonProperty ( value = "streamQuery", required = true )
	private String streamQuery = null;
	
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

	public String getClientId() {
		return clientId;
	}

	public void setClientId(String clientId) {
		this.clientId = clientId;
	}

	public String getClientSecret() {
		return clientSecret;
	}

	public void setClientSecret(String clientSecret) {
		this.clientSecret = clientSecret;
	}

	public String getStreamType() {
		return streamType;
	}

	public void setStreamType(String streamType) {
		this.streamType = streamType;
	}

	public String getStreamVersion() {
		return streamVersion;
	}

	public void setStreamVersion(String streamVersion) {
		this.streamVersion = streamVersion;
	}

	public String getStreamSchemaVersion() {
		return streamSchemaVersion;
	}

	public void setStreamSchemaVersion(String streamSchemaVersion) {
		this.streamSchemaVersion = streamSchemaVersion;
	}

	public String getStreamQuery() {
		return streamQuery;
	}

	public void setStreamQuery(String streamQuery) {
		this.streamQuery = streamQuery;
	}	
	
}
