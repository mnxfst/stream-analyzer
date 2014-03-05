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
package com.mnxfst.stream.pipeline.message;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;

import akka.actor.ActorRef;

/**
 * Reports an update of element {@link ActorRef references}
 * @author mnxfst
 * @since 05.03.2014
 *
 */
@JsonRootName ( value = "pipelineElementReferenceUpdateMessage" )
public class PipelineElementReferenceUpdateMessage implements Serializable {

	private static final long serialVersionUID = 2822558124362700694L;

	/** identifier of pipeline the referenced elements belong to */
	@JsonProperty ( value = "pipelineId", required = true )
	private String pipelineId;
	
	/** element identifiers along with their node references */
	@JsonProperty ( value = "elementReferences", required = true )
	private Map<String, ActorRef> elementReferences = new HashMap<>();
	
	/**
	 * Default constructor
	 */
	public PipelineElementReferenceUpdateMessage() {		
	}
	
	/**
	 * Initializes the message using the provided input
	 * @param pipelineId
	 */
	public PipelineElementReferenceUpdateMessage(final String pipelineId) {
		this.pipelineId = pipelineId;
	}
	
	/**
	 * Adds a new element reference
	 * @param elementId
	 * @param elementReference
	 */
	public void addElementReference(final String elementId, final ActorRef elementReference) {
		this.elementReferences.put(elementId, elementReference);
	}

	public String getPipelineId() {
		return pipelineId;
	}

	public void setPipelineId(String pipelineId) {
		this.pipelineId = pipelineId;
	}

	public Map<String, ActorRef> getElementReferences() {
		return elementReferences;
	}

	public void setElementReferences(Map<String, ActorRef> elementReferences) {
		this.elementReferences = elementReferences;
	}
	
}
