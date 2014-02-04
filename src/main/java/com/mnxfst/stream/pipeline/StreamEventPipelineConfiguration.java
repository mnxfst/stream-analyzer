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
package com.mnxfst.stream.pipeline;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;
import com.mnxfst.stream.processing.StreamEventProcessingNodeConfiguration;

/**
 * Holds the configuration required for setting up a 
 * {@link StreamEventPipelineEntryPoint event processing pipeline}.
 * @author mnxfst
 * @since Feb 3, 2014
 */
@JsonRootName ( value = "pipelineConfiguration")
public class StreamEventPipelineConfiguration implements Serializable {

	private static final long serialVersionUID = 330161314952732747L;
	
	/** unique identifier to be used for referencing the pipeline */
	@JsonProperty ( value = "identifier", required = true )
	private String identifier = null;
	
	/** optional pipeline description */
	@JsonProperty ( value = "description", required = false )
	private String description = null;
	
	/** reference to entry point/initial node */
	@JsonProperty ( value = "entryPointId", required = true )
	private String entryPointId = null;
	
	/** set of pipeline node configurations */
	@JsonProperty ( value = "pipelineNodes", required = true )
	private List<StreamEventProcessingNodeConfiguration> pipelineNodes = new ArrayList<>();
	
	/**
	 * Default constructor
	 */
	public StreamEventPipelineConfiguration() {		
	}
	
	/**
	 * Initializes the configuration using the provided input
	 * @param identifier
	 * @param description
	 * @param entryPointId
	 */
	public StreamEventPipelineConfiguration(final String identifier, final String description, final String entryPointId) {
		this.identifier = identifier;
		this.description = description;
		this.entryPointId = entryPointId;
	}
	
	/**
	 * Adds the provided node configuration to the pipeline config
	 * @param nodeConfiguration
	 */
	public void addPipelineNode(final StreamEventProcessingNodeConfiguration nodeConfiguration) {
		this.pipelineNodes.add(nodeConfiguration);
	}

	public String getIdentifier() {
		return identifier;
	}

	public void setIdentifier(String identifier) {
		this.identifier = identifier;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getEntryPointId() {
		return entryPointId;
	}

	public void setEntryPointId(String entryPointId) {
		this.entryPointId = entryPointId;
	}

	public List<StreamEventProcessingNodeConfiguration> getPipelineNodes() {
		return pipelineNodes;
	}

	public void setPipelineNodes(
			List<StreamEventProcessingNodeConfiguration> pipelineNodes) {
		this.pipelineNodes = pipelineNodes;
	}
	
	
}
