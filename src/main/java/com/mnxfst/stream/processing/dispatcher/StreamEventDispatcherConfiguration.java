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
package com.mnxfst.stream.processing.dispatcher;

import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import akka.actor.ActorRef;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.mnxfst.stream.processing.dispatcher.script.ScriptBasedStreamEventDispatcherConfiguration;
import com.mnxfst.stream.processing.message.StreamEventMessage;

/**
 * Holds configuration required to initialize {@link StreamEventDispatcher stream event dispatcher}
 * @author mnxfst
 * @since Feb 4, 2014
 */
@JsonRootName ( value = "dispatcherConfiguration" )
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
@JsonSubTypes ({ @JsonSubTypes.Type(ScriptBasedStreamEventDispatcherConfiguration.class) })
public abstract class StreamEventDispatcherConfiguration implements Serializable {

	private static final long serialVersionUID = -8557324052827634194L;

	/** unique dispatcher identifier */
	@JsonProperty ( value = "identifier", required = true )
	private String identifier = null;
	/** pipeline description */
	@JsonProperty ( value = "description", required = true )
	private String description = null;
	/** mapping from pipeline identifiers towards the initial node references */
	@JsonIgnore
	private final Map<String, ActorRef> pipelines = new HashMap<>();
	/** mapping of destination identifiers towards a set pipelines that receive inbound messages of that destination */
	@JsonProperty ( value = "destinationPipelines", required = true )
	private Map<String, Set<String>> destinationPipelines = new HashMap<>();
	/** name of dispatcher implementation */
	@JsonProperty ( value = "dispatcherClass", required = true )
	private String dispatcherClass = null;
	
	/**
	 * Default constructor
	 */
	public StreamEventDispatcherConfiguration() {		
	}
	
	/**
	 * Initializes the configuration using the provided input
	 * @param identifier
	 * @param description
	 * @param dispatcherClass
	 */
	public StreamEventDispatcherConfiguration(final String identifier, final String description, final String dispatcherClass) {
		this.identifier = identifier;
		this.description = description;
		this.dispatcherClass = dispatcherClass;
	}

	/**
	 * Adds the given pipeline entry point referenced by its identifier to the map
	 * @param pipelineId
	 * @param pipelineEntryPointRef
	 */
	public void addPipeline(final String pipelineId, final ActorRef pipelineEntryPointRef) {
		this.pipelines.put(pipelineId, pipelineEntryPointRef);
	}
	
	/**
	 * Adds the referenced pipeline as receiver of {@link StreamEventMessage stream events} 
	 * @param destinationId
	 * @param pipelineId
	 */
	public void addDestinationPipeline(final String destinationId, final String pipelineId) {		
		Set<String> pipelineIdentifiers = this.destinationPipelines.get(destinationId);
		if(pipelineIdentifiers == null)
			pipelineIdentifiers = new HashSet<>();
		pipelineIdentifiers.add(pipelineId);
		this.destinationPipelines.put(destinationId, pipelineIdentifiers);		
	}
	
	/**
	 * Adds the referenced pipelines as receivers of {@link StreamEventMessage stream events} 
	 * @param destinationId
	 * @param pipelineRefs
	 */
	public void addDestinationPipeline(final String destinationId, final Set<String> pipelineIds) {
		Set<String> pipeIds = this.destinationPipelines.get(destinationId);
		if(pipeIds == null)
			pipeIds = new HashSet<>();
			pipeIds.addAll(pipelineIds);
		this.destinationPipelines.put(destinationId, pipeIds);		
	}

	public String getIdentifier() {
		return identifier;
	}

	public void setIdentifier(String identifier) {
		this.identifier = identifier;
	}

	/**
	 * @return the dispatcherClass
	 */
	public String getDispatcherClass() {
		return dispatcherClass;
	}

	/**
	 * @param dispatcherClass the dispatcherClass to set
	 */
	public void setDispatcherClass(String dispatcherClass) {
		this.dispatcherClass = dispatcherClass;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Map<String, Set<String>> getDestinationPipelines() {
		return destinationPipelines;
	}

	public void setDestinationPipelines(
			Map<String, Set<String>> destinationPipelines) {
		this.destinationPipelines = destinationPipelines;
	}

	public Map<String, ActorRef> getPipelines() {
		return pipelines;
	}
	
}
