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

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;
import com.mnxfst.stream.processing.message.StreamEventMessage;

/**
 * Holds configuration required to initialize {@link StreamEventDispatcher stream event dispatcher}
 * @author mnxfst
 * @since Feb 4, 2014
 */
@JsonRootName ( value = "dispatcherConfiguration" )
public class StreamEventDispatcherConfiguration implements Serializable {

	private static final long serialVersionUID = -8557324052827634194L;

	/** unique dispatcher identifier */
	@JsonProperty ( value = "identifier", required = true )
	private String identifier = null;
	/** pipeline description */
	@JsonProperty ( value = "description", required = true )
	private String description = null;
	/** mapping from pipeline identifiers towards the initial node references */
	private final Map<String, ActorRef> pipelines = new HashMap<>();
	/** mapping of event source towards a set pipelines that receive inbound messages of that source */
	@JsonProperty ( value = "eventSourcePipelines", required = true )
	private final Map<String, Set<String>> eventSourcePipelines = new HashMap<>();
	
	/**
	 * Default constructor
	 */
	public StreamEventDispatcherConfiguration() {		
	}
	
	/**
	 * Initializes the configuration using the provided input
	 * @param identifier
	 * @param description
	 */
	public StreamEventDispatcherConfiguration(final String identifier, final String description) {
		this.identifier = identifier;
		this.description = description;
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
	 * Adds the referenced pipeline as receiver of {@link StreamEventMessage stream events} that 
	 * originate from the named event source 
	 * @param eventSourceId
	 * @param pipelineId
	 */
	public void addEventSourcePipeline(final String eventSourceId, final String pipelineId) {		
		Set<String> pipelineIdentifiers = this.eventSourcePipelines.get(eventSourceId);
		if(pipelineIdentifiers == null)
			pipelineIdentifiers = new HashSet<>();
		pipelineIdentifiers.add(pipelineId);
		this.eventSourcePipelines.put(eventSourceId, pipelineIdentifiers);		
	}
	
	/**
	 * Adds the referenced pipelines as receivers of {@link StreamEventMessage stream events} that
	 * originate from the name event source
	 * @param eventSourceId
	 * @param pipelineRefs
	 */
	public void addEventSourcePipeline(final String eventSourceId, final Set<String> pipelineIds) {
		Set<String> pipeIds = this.eventSourcePipelines.get(eventSourceId);
		if(pipeIds == null)
			pipeIds = new HashSet<>();
			pipeIds.addAll(pipelineIds);
		this.eventSourcePipelines.put(eventSourceId, pipeIds);		
	}

	/**
	 * @return the identifier
	 */
	public String getIdentifier() {
		return identifier;
	}

	/**
	 * @param identifier the identifier to set
	 */
	public void setIdentifier(String identifier) {
		this.identifier = identifier;
	}

	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * @param description the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * @return the pipelines
	 */
	public Map<String, ActorRef> getPipelines() {
		return pipelines;
	}

	/**
	 * @return the eventSourcePipelines
	 */
	public Map<String, Set<String>> getEventSourcePipelines() {
		return eventSourcePipelines;
	}
	
	
}
