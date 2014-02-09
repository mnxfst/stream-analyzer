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
package com.mnxfst.stream.processing;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.mnxfst.stream.processing.evaluator.StreamEventScriptEvaluatorConfiguration;
import com.mnxfst.stream.processing.persistence.StreamEventESWriterConfiguration;

/**
 * Common interface to all settings used for setting up {@link StreamEventProcessingNode stream event processing nodes}
 * @author mnxfst
 * @since 03.02.2014
 */
@JsonRootName ( value = "nodeConfiguration" )
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
@JsonSubTypes ({ @JsonSubTypes.Type(StreamEventScriptEvaluatorConfiguration.class), @JsonSubTypes.Type( StreamEventESWriterConfiguration.class) })
public abstract class StreamEventProcessingNodeConfiguration implements Serializable {

	private static final long serialVersionUID = -2927260388305518940L;
	
	/** processing node class - required for dynamic node instantiation */
	@JsonProperty ( value = "processingNodeClass", required = true )
	private String processingNodeClass = null;
	/** analyzer id used for referencing the component from within other pipeline elements */ 
	@JsonProperty( value = "identifier", required = true )
	private String identifier = null;
	/** description */
	@JsonProperty ( value = "description", required = false )
	private String description = null;
	/** number of node instances - value of less than 1 avoids the instantiation of any router */
	@JsonProperty ( value = "numOfNodeInstances", required = true )
	private int numOfNodeInstances = 0; // value o

	/**
	 * Default constructor
	 */
	public StreamEventProcessingNodeConfiguration() {		
	}
	
	/**
	 * Initializes the configuration using the provided input
	 * @param processingNodeClass
	 * @param identifier
	 * @param description
	 * @param numOfNodeInstances
	 */
	public StreamEventProcessingNodeConfiguration(final String processingNodeClass, final String identifier, final String description, final int numOfNodeInstances) {
		this.processingNodeClass = processingNodeClass;
		this.identifier = identifier;
		this.description = description;
		this.numOfNodeInstances = numOfNodeInstances;
	}

	public String getProcessingNodeClass() {
		return processingNodeClass;
	}

	public void setProcessingNodeClass(String processingNodeClass) {
		this.processingNodeClass = processingNodeClass;
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

	public int getNumOfNodeInstances() {
		return numOfNodeInstances;
	}

	public void setNumOfNodeInstances(int numOfNodeInstances) {
		this.numOfNodeInstances = numOfNodeInstances;
	}
	
	
}

