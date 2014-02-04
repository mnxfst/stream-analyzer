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
package com.mnxfst.stream.processing.message;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import akka.actor.ActorRef;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;
import com.mnxfst.stream.processing.pipeline.StreamEventPipelineEntryPoint;

/**
 * Contain {@link ActorRef references} towards all nodes that belong to a pipeline. Each reference comes with
 * the identifier used by the {@link StreamEventPipelineEntryPoint pipeline} to access the node.
 * @author mnxfst
 * @since Feb 4, 2014
 */
@JsonRootName ( value = "nodeReferences" )
public class PipelineNodeReferencesMessage implements Serializable {

	private static final long serialVersionUID = -5701417247616918756L;

	/** pipeline identifier */
	@JsonProperty ( value = "pipelineId", required = true )
	private String pipelineId = null;
	/** map of node references towards nodes (actors) */
	@JsonProperty ( value = "nodeReferences", required = true )
	private Map<String, ActorRef> nodeReferences = new HashMap<>();
	/** map of node references which serve as error handlers (actors) */
	@JsonProperty ( value = "errorHandlerReferences", required = true )
	private Map<String, ActorRef> errorHandlerReferences = new HashMap<>();
	
	/**
	 * Default constructor
	 */
	public PipelineNodeReferencesMessage() {		
	}
	
	/**
	 * Initializes the message using the provided input
	 * @param pipelineId
	 */
	public PipelineNodeReferencesMessage(final String pipelineId) {
		this.pipelineId = pipelineId;
	}
	
	/**
	 * Adds the node actor referenced by the identifier to the internal mapping 
	 * @param nodeRefId
	 * @param nodeActorReference
	 */
	public void addNodeReference(final String nodeRefId, final ActorRef nodeActorReference) {
		this.nodeReferences.put(nodeRefId, nodeActorReference);
	}
	
	/**
	 * Adds the error handler referenced by the identifier to the internal mapping
	 * @param errorHandlerRefId
	 * @param errorHandlerReference
	 */
	public void addErrorHandlerReference(final String errorHandlerRefId, final ActorRef errorHandlerReference) {
		this.errorHandlerReferences.put(errorHandlerRefId, errorHandlerReference);
	}

	public String getPipelineId() {
		return pipelineId;
	}

	public void setPipelineId(String pipelineId) {
		this.pipelineId = pipelineId;
	}

	public Map<String, ActorRef> getNodeReferences() {
		return nodeReferences;
	}

	public void setNodeReferences(Map<String, ActorRef> nodeReferences) {
		this.nodeReferences = nodeReferences;
	}

	public Map<String, ActorRef> getErrorHandlerReferences() {
		return errorHandlerReferences;
	}

	public void setErrorHandlerReferences(
			Map<String, ActorRef> errorHandlerReferences) {
		this.errorHandlerReferences = errorHandlerReferences;
	}
		
}