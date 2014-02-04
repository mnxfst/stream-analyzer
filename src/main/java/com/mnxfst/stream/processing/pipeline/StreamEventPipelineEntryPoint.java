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
package com.mnxfst.stream.processing.pipeline;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.UntypedActor;
import akka.routing.RoundRobinRouter;

import com.mnxfst.stream.processing.StreamEventProcessingNodeConfiguration;
import com.mnxfst.stream.processing.evaluator.StreamEventScriptEvaluator;
import com.mnxfst.stream.processing.message.PipelineNodeReferencesMessage;
import com.mnxfst.stream.processing.message.StreamEventMessage;

/**
 * Represents the entry point into a {@link StreamEventMessage stream event} processing pipeline. 
 * It holds a list of {@link StreamEventScriptEvaluator script evaluators} which are processed in 
 * order as provided. During pipeline initialization each component receives its very own configuration 
 * data required for a proper setup. The entry point is the root to all pipeline bound elements but 
 * is allowed to reference external components as well, eg. database writer.
 * @author mnxfst
 * @since Jan 30, 2014
 * 
 * TODO add error handler
 * TODO testing 
 */
public class StreamEventPipelineEntryPoint extends UntypedActor {

	/** complete pipeline configuration */
	private final StreamEventPipelineConfiguration configuration;
	/** extracted entry point identifier to ease lookups for the first node receiving inbound messages */
	private final String pipelineEntryPointId;
	/** map of all elements contained inside stream event pipeline */
	private final Map<String, ActorRef> pipelineNodeRefs = new HashMap<>();
		
	/**
	 * Initializes the pipeline using the provided input 
	 * @param configuration
	 */
	public StreamEventPipelineEntryPoint(final StreamEventPipelineConfiguration configuration) {
		
		// empty configurations are prohibited
		if(configuration == null) 
			throw new RuntimeException("Required configuration missing");
		
		// the pipeline identifier is required 
		if(StringUtils.isBlank(configuration.getIdentifier()))
			throw new RuntimeException("Required pipeline identifier is missing");
		
		// first pipeline element to pass an inbound message to is required as the pipeline work otherwise
		if(StringUtils.isBlank(configuration.getEntryPointId()))
			throw new RuntimeException("Required pipeline entry point identifier is missing");
		
		// the pipeline must have any nodes
		if(configuration.getPipelineNodes() == null || configuration.getPipelineNodes().isEmpty()) 
			throw new RuntimeException("Required evaluator configurations missing");
		
		
		this.configuration = configuration;
		this.pipelineEntryPointId = configuration.getEntryPointId();
	}
		
	/**
	 * @see akka.actor.UntypedActor#preStart()
	 */
	public void preStart() throws Exception {
		super.preStart();
		
		// TODO add error handler
		
		// prepare broadcast message notifying all pipeline elements about each other ... TODO a real akka broadcast would be much cooler 
		PipelineNodeReferencesMessage nodeReferencesMessage = new PipelineNodeReferencesMessage(configuration.getIdentifier());

		// step through nodes and initialize each one separately		
		for(final StreamEventProcessingNodeConfiguration nodeCfg : configuration.getPipelineNodes()) {

			// if the current configuration element is null: write an error log element
			if(nodeCfg == null) {
				context().system().log().error("Found 'null' configuration"); // TODO more logging
				continue;
			}
			
			// if the node class is empty/unknown: avoid further processing this configuration as the class is required for actor instantiation 
			if(StringUtils.isBlank(nodeCfg.getProcessingNodeClass())) {
				context().system().log().error("[pipeline="+configuration.getIdentifier()+", node="+nodeCfg.getIdentifier()+", error=processing_class_missing"); // TODO more logging and reporting toward error ndoes
				continue;
			}
			
			// instantiate the actor, fetch its reference and register it at the internal mapping as well as with the broadcast message 
			ActorRef nodeRef = null;
			
			if(nodeCfg.getNumOfNodeInstances() > 0) 
				nodeRef = context().actorOf(Props.create(Class.forName(nodeCfg.getProcessingNodeClass()), nodeCfg).withRouter(new RoundRobinRouter(nodeCfg.getNumOfNodeInstances())), nodeCfg.getIdentifier()); // TODO what about round robin routers?
			else
				nodeRef = context().actorOf(Props.create(Class.forName(nodeCfg.getProcessingNodeClass()), nodeCfg), nodeCfg.getIdentifier()); // TODO what about round robin routers?

			if(nodeRef != null) {
				this.pipelineNodeRefs.put(nodeCfg.getIdentifier(), nodeRef);
				nodeReferencesMessage.addNodeReference(nodeCfg.getIdentifier(), nodeRef);
			} else {
				context().system().log().error("Failed to initialize node " + nodeCfg.getIdentifier()); // TODO more logging
			}			
		}
		
		// if the pipeline misses the configured entry point, this is an invalid state as inbound message cannot be handed over to the pipeline
		if(!this.pipelineNodeRefs.containsKey(pipelineEntryPointId)) {
			throw new RuntimeException("Failed to initialize pipeline '"+configuration.getIdentifier()+"' as entry point is missing");
		}
		
		// fill error handler set
		for(String errorHandlerId : this.configuration.getErrorHandlingNodes()) {
			if(StringUtils.isNotBlank(errorHandlerId)) {
				final ActorRef errorHandlerRef = this.pipelineNodeRefs.get(errorHandlerId);
				nodeReferencesMessage.addErrorHandlerReference(errorHandlerId, errorHandlerRef);
			} else {
				context().system().log().error("Failed to find error handler '"+errorHandlerId+"' in pipeline node set");
			}
		}
		
		// tell all sub-nodes about the others
		for(String refId : this.pipelineNodeRefs.keySet()) {
			final ActorRef nodeRef = this.pipelineNodeRefs.get(refId);			
			nodeRef.tell(nodeReferencesMessage, getSelf());
		}
			
		System.out.println(pipelineNodeRefs.size() + " nodes notified");
		
	}
	
	/**
	 * @see akka.actor.UntypedActor#onReceive(java.lang.Object)
	 */
	public void onReceive(Object message) throws Exception {
		
		// fetch inbound message, check its type and forward it to the first pipeline node 
		if(message instanceof StreamEventMessage) {			
			final ActorRef entryPointRef = this.pipelineNodeRefs.get(pipelineEntryPointId);
			entryPointRef.tell(message, getSelf());
		}		
	}
}
