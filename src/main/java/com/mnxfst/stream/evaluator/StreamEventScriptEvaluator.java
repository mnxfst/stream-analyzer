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
package com.mnxfst.stream.evaluator;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import org.apache.commons.lang3.StringUtils;

import akka.actor.ActorRef;
import akka.actor.UntypedActor;

import com.mnxfst.stream.message.StreamEventMessage;

/**
 * Pipeline element which receives a script during initialization and executes that against an
 * inbound {@link StreamEventMessage message}. Depending on the result the (modified (by script)) 
 * message is forwarded to a number of configured components. 
 * @author mnxfst
 * @since 03.02.2014
 */
public class StreamEventScriptEvaluator extends UntypedActor {

	/** script engine variable that holds the event message content - if the evaluator changes anything it must be written back to this variable */ 
	public static final String SCRIPT_EVENT_CONTENT = "eventContent";	
	/** script engine variable that must hold the result indicator at the end of computation - aka the value which determine the forward to use */
	public static final String SCRIPT_RESULT = "result";

	/** analyzer name or identifier */
	private final String identifier;
	/** javascript used for analyzing inbound stream events */
	private final String script;
	/** scripting engine */
	private final ScriptEngine scriptEngine;
	/** forwarding rules */
	private final Map<String, List<ActorRef>> forwardingRules = new HashMap<>();

	/**
	 * Initializes the evaluator using the provided input
	 * @param identifier
	 * @param script
	 */
	public StreamEventScriptEvaluator(final String identifier, final String script, final Map<String, List<ActorRef>> forwardingRules, final ActorRef errorHandler) {
		this.identifier = identifier;
		this.script = script;
		this.errorHandler = errorHandler;
		this.forwardingRules.putAll(forwardingRules);

		ScriptEngineManager scriptEngineManager = new ScriptEngineManager();
		this.scriptEngine = scriptEngineManager.getEngineByName("JavaScript");
		if(this.scriptEngine == null)
			throw new RuntimeException("Failed to initializes script engine");
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
	 * Applies the given script on the received {@link StreamEventMessage stream event}
	 * @param streamEventMessage
	 * @return holds the script response
	 * @throws ScriptException 
	 */
	protected String evaluareScript(final StreamEventMessage streamEventMessage) throws ScriptException {
		this.scriptEngine.put(SCRIPT_EVENT_CONTENT, streamEventMessage.getContent());
		this.scriptEngine.eval(this.script);
		String messageContent = (String)this.scriptEngine.get(SCRIPT_EVENT_CONTENT);
		streamEventMessage.setContent(messageContent);
		return (String)this.scriptEngine.get(SCRIPT_RESULT);
	}
	
	/**
	 * Reports an error to the configured {@link ActorRef error handler}
	 * @param streamEventMessage
	 * @param key
	 * @param location
	 * @param message
	 */
	protected void reportError(final StreamEventMessage streamEventMessage, final String key, final String location, final String message) {
		streamEventMessage.addError(key, identifier, location, message);
		this.errorHandler.tell(streamEventMessage, getSelf());
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
