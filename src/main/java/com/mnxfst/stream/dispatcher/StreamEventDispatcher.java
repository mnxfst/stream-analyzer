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
package com.mnxfst.stream.dispatcher;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.StringUtils;

import scala.concurrent.Future;
import akka.actor.ActorRef;
import akka.actor.UntypedActor;
import akka.dispatch.OnComplete;
import akka.util.Timeout;

import com.mnxfst.stream.message.BindEventSourcePipelineFailedMessage;
import com.mnxfst.stream.message.StreamEventMessage;

/**
 * Receives all inbound {@link StreamEventMessage events}, analyzes the contained 
 * {@link StreamEventMessage#getEventSourceId() source identifier} and dispatches 
 * the message to the configured pipelines. 
 * @author mnxfst
 * @since Jan 30, 2014
 */
public class StreamEventDispatcher extends UntypedActor {
	
	private final int ACTOR_SELECTION_TIMEOUT = 2; // seconds
	
	/** mapping from event source identifier towards the entry point of an processing pipeline */
	private final Map<String, List<ActorRef>> pipelines = new HashMap<>();
	
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
			// TODO error handling
			return;
		}
		
		// event source identifier must be available to properly identify pipeline  
		if(StringUtils.isBlank(msg.getEventSourceId())) {
			// TODO error handling 
			return;
		}
		
		// event source identifier must point to a processing pipeline
		if(!this.pipelines.containsKey(msg.getEventSourceId())) {
			// TODO error handling
			return;
		}
		
		// fetch the pipeline entry points and ensure that the list holds some references
		List<ActorRef> pipelineEntryPoints = this.pipelines.get(msg.getEventSourceId());
		if(pipelineEntryPoints == null || pipelineEntryPoints.isEmpty()) {
			// TODO error handling
			return;
		}
	
		// step through endpoint references and forward message to each one found
		for(ActorRef entryPoint : pipelineEntryPoints) {
			if(entryPoint != null) {
				entryPoint.tell(msg, getSelf());
			}
		}
		context().system().log().debug("Forwarded inbound message from " + msg.getEventSourceId() + 
				" to " + pipelineEntryPoints.size() + " pipelines");		
	}
	
	/**
	 * Looks up the {@link ActorRef pipeline endpoint} referenced and binds the given event source
	 * identifier to it.
	 * @param eventSourceId
	 * @param pipelineEndpointPath
	 */
	protected void bindEventSourcePipeline(final String eventSourceId, final String pipelineEndpointPath) {
		
		// fetch the actor referenced by the pipeline endpoint path
		// if the (async!) lookup fails a notification is send towards the
		// origin sender otherwise the pipeline endpoint is referenced by
		// the event source as receiver of inbound events
		Future<ActorRef> actorRef = context().system().actorSelection(pipelineEndpointPath).resolveOne(
				Timeout.apply(ACTOR_SELECTION_TIMEOUT, TimeUnit.SECONDS));		
		actorRef.onComplete(new OnComplete<ActorRef>() {

			/**
			 * @see akka.dispatch.OnComplete#onComplete(java.lang.Throwable, java.lang.Object)
			 */
			public void onComplete(Throwable error, ActorRef actorRef) throws Throwable {
			
				// TODO this could lead to problems on initialization as the list might be added twice ... due to concurrency
				if(error == null) {
					List<ActorRef> pipelineEndpoints = pipelines.get(eventSourceId);
					if(pipelineEndpoints == null)
						pipelineEndpoints = new ArrayList<>();				
					if(!pipelineEndpoints.contains(actorRef)) {
						pipelineEndpoints.add(actorRef);
						context().system().log().debug("Successfully registered " + actorRef + " as receiver of inbound message from " + eventSourceId);
					} else {				
						context().system().log().debug("A receiver already exists for " + eventSourceId);
					}
				} else {
					getSender().tell(new BindEventSourcePipelineFailedMessage(eventSourceId, pipelineEndpointPath, error.getMessage()), getSelf());
				}
			}
			
		}, context().dispatcher());
		
	}
}
