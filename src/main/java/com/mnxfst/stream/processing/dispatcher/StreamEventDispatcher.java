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
public class StreamEventDispatcher extends UntypedActor {
	
	/** dispatcher configuration */
	private final StreamEventDispatcherConfiguration configuration;
	
	/** mapping from pipeline identifiers towards the initial node references */
	private Map<String, ActorRef> pipelines = new HashMap<>();
	/** mapping of event source towards a set pipelines that receive inbound messages of that source */
	private Map<String, Set<String>> eventSourcePipelines = new HashMap<>();
	
	/**
	 * Initializes the dispatcher using the provided input
	 * @param configuration
	 */
	public StreamEventDispatcher(final StreamEventDispatcherConfiguration configuration) {		
		if(configuration == null)
			throw new RuntimeException("Missing required configuration");
		if(configuration.getPipelines() == null || configuration.getPipelines().isEmpty())
			throw new RuntimeException("Missing required pipeline configurations");
		if(configuration.getEventSourcePipelines() == null || configuration.getEventSourcePipelines().isEmpty())
			throw new RuntimeException("Missing required event source-to-pipelines mappings");
		this.configuration = configuration;
	}
	
	/**
	 * @see akka.actor.UntypedActor#preStart()
	 */
	public void preStart() throws Exception {
		super.preStart();
		this.pipelines = this.configuration.getPipelines();
		this.eventSourcePipelines = this.configuration.getEventSourcePipelines();
	}

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
	 * Dispatches the received {@link StreamEventMessage event message} to configured
	 * pipelines. 
	 * @param msg
	 */
	protected void dispatchMessage(final StreamEventMessage msg) {
		
		// message must not be null ... obviously
		if(msg == null) {
			// TODO do we need some reporting here?
			return;
		}
		
		// event source identifier must be available to properly identify pipeline  
		if(StringUtils.isBlank(msg.getEventSourceId())) {
			// TODO do we need some reporting here
			return;
		}
		
		// event source identifier must point to a processing pipeline
		if(!this.eventSourcePipelines.containsKey(msg.getEventSourceId())) {
			// TODO  any more logging required?
			context().system().log().error("No pipeline found for event source identifier '"+msg.getEventSourceId()+"'. Ignoring message."); 
			return;
		}
		
		Set<String> pipelineIdentifiers = this.eventSourcePipelines.get(msg.getEventSourceId());
		if(pipelineIdentifiers != null && !pipelineIdentifiers.isEmpty()) {
			for(String pipelineId : pipelineIdentifiers) {
				ActorRef pipelineEntryPointRef = this.pipelines.get(pipelineId);
				pipelineEntryPointRef.tell(msg, getSelf());
			}
		} else {
			context().system().log().error("No pipeline found for event source identifier '"+msg.getEventSourceId()+"'. Ignoring message.");
		}
	}
}
