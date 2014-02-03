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
package com.mnxfst.stream.modifier;

import java.util.ArrayList;
import java.util.List;

import javax.script.ScriptException;

import akka.actor.ActorRef;

import com.mnxfst.stream.AbstractStreamEventScriptEvaluator;
import com.mnxfst.stream.message.StreamEventMessage;

/**
 * Provides a framework for modifying inbound {@link StreamEventMessage stream events} and
 * forwarding them to configured destinations. It receives a script and a list holding different 
 * message destinations.
 * @author mnxfst
 * @since 31.01.2014
 */
public class StreamEventModifier extends AbstractStreamEventScriptEvaluator {

	/** forwarding rules */
	private final List<ActorRef> destinations = new ArrayList<>();
	
	/**
	 * Initializes the stream event modifier using the provided input
	 * @param identifier
	 * @param script
	 * @param destinations
	 * @param errorHandler
	 */
	public StreamEventModifier(final String identifier, final String script, final List<ActorRef> destinations, final ActorRef errorHandler) {
		super(identifier, script, errorHandler);
		this.destinations.addAll(destinations);
	}
	
	/**
	 * @see akka.actor.UntypedActor#onReceive(java.lang.Object)
	 */
	public void onReceive(Object message) throws Exception {

		if(message instanceof StreamEventMessage) {
			StreamEventMessage msg = (StreamEventMessage)message;

			// apply configured script on stream event
			try {
				msg.setContent(evaluareScript(msg));
			} catch(ScriptException e) {
				reportError(msg, "stream.modifier.script.execution.failed", "modifyStreamEvent", "Failed to execute script. Error: " + e.getMessage());
				return;
			} catch(Exception e) {
				reportError(msg, "stream.analyzer.script.execution.general", "modifyStreamEvent", e.getMessage());
				return;
			}

			// forward message to configured receivers
			try {
				forwardStreamEvent(msg);
			} catch(Exception e) {
				reportError(msg, "stream.analyzer.script.forwarding.general", "forwardStreamEvent", e.getMessage());
				return;
			}

		}		
	}

	/**
	 * Forwards the {@link StreamEventMessage stream event} according to the provided script response
	 * @param streamEventMessage
	 * @param scriptResponse
	 */
	protected void forwardStreamEvent(final StreamEventMessage streamEventMessage) {

		// forward event to configured receivers
		for(final ActorRef ref : destinations) {
			if(ref != null)
				ref.tell(streamEventMessage, getSender());
		}
	}
}
