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

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;

import akka.actor.ActorRef;
import akka.actor.UntypedActor;

import com.mnxfst.stream.processing.message.StreamEventMessage;

/**
 * Receives all inbound {@link StreamEventMessage events}, analyzes the contained 
 * {@link StreamEventMessage#getEventSourceId() source identifier} and dispatches 
 * the message to the configured pipelines.
 * 
 * @author mnxfst
 * @since Jan 30, 2014
 */
public abstract class StreamEventDispatcher extends UntypedActor {
	
	/** mapping from pipeline identifiers towards the initial node references */
	private Map<String, ActorRef> pipelines = new HashMap<>();
	/** mapping of destinations towards a set pipelines that receive inbound messages of that source */
	private Map<String, Set<String>> destinationPipelines = new HashMap<>();
	
	/**
	 * Initializes the dispatcher using the provided input
	 * @param configuration
	 */
	public StreamEventDispatcher(final StreamEventDispatcherConfiguration configuration) {		
		if(configuration == null)
			throw new RuntimeException("Missing required configuration");
		if(configuration.getPipelines() == null || configuration.getPipelines().isEmpty())
			throw new RuntimeException("Missing required pipeline configurations");
		if(configuration.getDestinationPipelines() == null || configuration.getDestinationPipelines().isEmpty())
			throw new RuntimeException("Missing required destination-to-pipelines mappings");
		this.pipelines = configuration.getPipelines();
		this.destinationPipelines = configuration.getDestinationPipelines();
	}
	
	/**
	 * Dispatches the inbound {@link StreamEventMessage stream event} according to the implementing
	 * dispatcher rules
	 * @param streamEventMessage
	 * @throws Exception
	 */
	protected abstract void dispatchMessage(final StreamEventMessage streamEventMessage) throws Exception; 

	/**
	 * @see akka.actor.UntypedActor#onReceive(java.lang.Object)
	 */
	public void onReceive(Object message) throws Exception {

		if(message instanceof StreamEventMessage) {
			dispatchMessage((StreamEventMessage)message);			
		} else {
			unhandled(message);
		}		
	}
	
	/**
	 * Forwards the given message towards the destination referenced
	 * @param streamEventMessage
	 * @param destinationId
	 */
	protected void forwardMessage(final StreamEventMessage streamEventMessage, final String destinationId) {
		
		// ensure that the destination identifier is not empty
		if(StringUtils.isBlank(destinationId)) {
			context().system().log().error("Missing required destination identifier. Ignoring message.");
			return;
		}
				
		// the destination identifier must point to an existing set of pipelines
		if(!this.destinationPipelines.containsKey(destinationId)) {
			context().system().log().error("Found no set of destination pipelines for '"+destinationId+"'. Ignoring message."); 
			return;
		}
		
		// fetch set of pipelines referenced by the destination identifier
		Set<String> destinationPipelineIdentifiers = this.destinationPipelines.get(destinationId);
		if(destinationPipelineIdentifiers == null || destinationPipelineIdentifiers.isEmpty()) {
			context().system().log().error("Set of destination pipelines referenced by '"+destinationId+"' is empty or null. Ignoring message."); 
			return;
		}
		
		int successCount = 0;
		int errorCount = 0;
		// step through identifiers, fetch pipelines from map and forward the message to each one
		for(String destPipelineId : destinationPipelineIdentifiers) {
			
			final ActorRef pipelineRef = this.pipelines.get(destPipelineId);
			if(pipelineRef != null) {
				pipelineRef.tell(streamEventMessage, getSelf());
				successCount++;
			} else {
				context().system().log().error("Referenced pipeline '"+destPipelineId + "' not found");
				errorCount++;
			}			
		}
		
		context().system().log().debug("Message forwarded to " + successCount + " of " + destinationPipelineIdentifiers.size() + " pipelines. Errors found: " + errorCount);
	}
	
}
