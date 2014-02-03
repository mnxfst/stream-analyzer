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
package com.mnxfst.stream.analyzer;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.script.ScriptException;

import org.apache.commons.lang3.StringUtils;

import akka.actor.ActorRef;

import com.mnxfst.stream.AbstractStreamEventScriptEvaluator;
import com.mnxfst.stream.message.StreamEventMessage;


/**
 * Provides a framework for analyzing inbound {@link StreamEventMessage stream events} and
 * forwarding them to configured destinations. It receives a script, a field to fetch the 
 * result from and a map holding different result types along with message destinations.
 * @author mnxfst
 * @since 31.01.2014
 */
public class StreamEventAnalyzer extends AbstractStreamEventScriptEvaluator {
	
	/** forwarding rules */
	private final Map<String, List<ActorRef>> forwardingRules = new HashMap<>();
	
	/**
	 * Initializes the stream event analyzer using the provided input
	 * @param identifier
	 * @param script
	 * @param resultAttribute
	 * @param forwardingRules
	 * @param errorHandler
	 */
	public StreamEventAnalyzer(final String identifier, final String script, final Map<String, List<ActorRef>> forwardingRules, final ActorRef errorHandler) {		
		super(identifier, script, errorHandler);		
		this.forwardingRules.putAll(forwardingRules);		
	}
	
	/**
	 * @see akka.actor.UntypedActor#onReceive(java.lang.Object)
	 */
	public void onReceive(Object message) throws Exception {
		
		if(message instanceof StreamEventMessage) {
			StreamEventMessage msg = (StreamEventMessage)message;
			
			// analyze message with configured script
			String response = null;
			try {
				response = evaluareScript(msg);
			} catch(ScriptException e) {
				reportError(msg, "stream.analyzer.script.execution.failed", "analyzerStreamEvent", "Failed to execute script. Error: " + e.getMessage());
				return;
			} catch(Exception e) {
				reportError(msg, "stream.analyzer.script.execution.general", "analyzerStreamEvent", e.getMessage());
				return;
			}

			// forward message to configured receivers
			try {
				forwardStreamEvent(msg, response);
			} catch(Exception e) {
				reportError(msg,"stream.analyzer.script.forwarding.general", "forwardStreamEvent", e.getMessage());
				return;
			}

		}		
	}

	/**
	 * Forwards the {@link StreamEventMessage stream event} according to the provided script response
	 * @param streamEventMessage
	 * @param scriptResponse
	 */
	protected void forwardStreamEvent(final StreamEventMessage streamEventMessage, final String scriptResponse) {

		// ensure that the script response is not blank as it is otherwise forwarded to the error handler
		if(StringUtils.isBlank(scriptResponse)) {
			reportError(streamEventMessage, "stream.analyzer.script.forwarding.noResponse", "forwardStreamEvent", "no response found");
			return;
		}
			
		// fetch the receivers configured for the script response, otherwise forward the message to the error handler
		List<ActorRef> forwards = this.forwardingRules.get(scriptResponse);
		if(forwards == null || forwards.isEmpty()) {
			reportError(streamEventMessage, "stream.analyzer.script.forwarding.noRules", "forwardStreamEvent", "no forwarding rule found for response");
			return;
		}
		
		// forward event to configured receivers
		for(final ActorRef ref : forwards) {
			if(ref != null)
				ref.tell(streamEventMessage, getSender());
		}
	}
	
	
}
