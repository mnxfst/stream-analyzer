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
package com.mnxfst.stream.pipeline.config;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;

/**
 * Pipeline root configuration providing an identifier used for accessing the master
 * as well as configuration options for pipeline elements
 * @author mnxfst
 * @since 03.03.2014
 *
 */
@JsonRootName ( value = "pipelineMasterConfiguration" )
public class PipelineRootConfiguration implements Serializable {

	private static final long serialVersionUID = -2785753273278475169L;

	/** unique identifier which is reported to the component registry and is used for accessing the pipeline */
	@JsonProperty ( value = "id", required = true )
	private String pipelineId;
	
	/** description of pipeline behavior */
	@JsonProperty ( value = "description", required = true )
	private String description;
	
	/** identifier of initial message receiving element */
	@JsonProperty ( value = "initialReceiverId", required = true )
	private String initialReceiverId;
	
	/** pipeline element configurations required for setting up the pipeline */
	@JsonProperty ( value = "elements", required = true )
	private Set<PipelineElementConfiguration> elements = new HashSet<>();
	
	/**
	 * Default constructor
	 */
	public PipelineRootConfiguration() {		
	}
	
	/**
	 * Initializes the pipeline master configuration using the provided input
	 * @param pipelineId
	 * @param description
	 */
	public PipelineRootConfiguration(final String pipelineId, final String description, final String initialReceiverId) {
		this.pipelineId = pipelineId;
		this.description = description;		
		this.initialReceiverId = initialReceiverId;
	}
	
	/**
	 * Adds another element configuration
	 * @param elementConfiguration
	 */
	public void addElementConfiguration(final PipelineElementConfiguration elementConfiguration) {
		this.elements.add(elementConfiguration);
	}

	public String getPipelineId() {
		return pipelineId;
	}

	public void setPipelineId(String pipelineId) {
		this.pipelineId = pipelineId;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Set<PipelineElementConfiguration> getElements() {
		return elements;
	}

	public void setElements(Set<PipelineElementConfiguration> elements) {
		this.elements = elements;
	}

	public String getInitialReceiverId() {
		return initialReceiverId;
	}

	public void setInitialReceiverId(String initialReceiverId) {
		this.initialReceiverId = initialReceiverId;
	}
	
	
}
