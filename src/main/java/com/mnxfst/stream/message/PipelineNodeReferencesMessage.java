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
package com.mnxfst.stream.message;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import akka.actor.ActorRef;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;
import com.mnxfst.stream.pipeline.StreamEventPipelineEntryPoint;

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
	
	/**
	 * Default constructor
	 */
	public PipelineNodeReferencesMessage() {		
	}
	
	/**
	 * Initializes the message using the provided input
	 * @param pipelineId
	 * @param nodeReferences
	 */
	public PipelineNodeReferencesMessage(final String pipelineId, final Map<String, ActorRef> nodeReferences) {
		this.pipelineId = pipelineId;
		this.nodeReferences.putAll(nodeReferences);
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
	
	
}
