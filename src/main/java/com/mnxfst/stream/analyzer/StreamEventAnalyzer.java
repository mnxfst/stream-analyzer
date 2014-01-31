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

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import org.apache.commons.lang3.StringUtils;

import akka.actor.ActorRef;
import akka.actor.UntypedActor;

import com.mnxfst.stream.message.StreamEventMessage;


/**
 * Provides a framework for analyzing inbound {@link StreamEventMessage stream events} and
 * forwarding them to configured destinations. It receives a script, a field to fetch the 
 * result from and a map holding different result types along with message destinations.
 * @author mnxfst
 * @since 31.01.2014
 */
public class StreamEventAnalyzer extends UntypedActor {

	/** attribute which must contain the result of an analyzer script */
	private static final String STREAM_ANALYZER_SCRIPT_RESULT_ATTRIBUTE = "analyzerResponse";
	
	/** analyzer name or identifier */
	private final String identifier;
	/** javascript used for analyzing inbound stream events */
	private final String script;	
	/** field to fetch the result from */
	private final String resultAttribute;	
	/** forwarding rules */
	private final Map<String, List<ActorRef>> forwardingRules = new HashMap<>();
	/** scripting engine */
	private final ScriptEngine scriptEngine;
	/** error handler */
	private final ActorRef errorHandler;
	
	/**
	 * Initializes the stream event analyzer using the provided input
	 * @param identifier
	 * @param script
	 * @param resultAttribute
	 * @param forwardingRules
	 * @param errorHandler
	 */
	public StreamEventAnalyzer(final String identifier, final String script, final String resultAttribute, final Map<String, List<ActorRef>> forwardingRules, final ActorRef errorHandler) {
		this.identifier = identifier;
		this.script = script;
		this.resultAttribute = resultAttribute;
		this.forwardingRules.putAll(forwardingRules);
		this.errorHandler = errorHandler;
		
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
				response = analyzerStreamEvent(msg);
			} catch(ScriptException e) {
				msg.addError("stream.analyzer.script.execution.failed", identifier, "analyzerStreamEvent", "Failed to execute script. Error: " + e.getMessage());
				this.errorHandler.tell(msg, getSelf());
				return;
			} catch(Exception e) {
				msg.addError("stream.analyzer.script.execution.general", identifier, "analyzerStreamEvent", e.getMessage());
				this.errorHandler.tell(msg, getSelf());
				return;
			}

			// forward message to configured receivers
			try {
				forwardStreamEvent(msg, response);
			} catch(Exception e) {
				msg.addError("stream.analyzer.script.forwarding.general", identifier, "forwardStreamEvent", e.getMessage());
				this.errorHandler.tell(msg, getSelf());
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
			streamEventMessage.addError("stream.analyzer.script.forwarding.noResponse", identifier, "forwardStreamEvent", "no response found");
			this.errorHandler.tell(streamEventMessage, getSelf());
			return;
		}
			
		// fetch the receivers configured for the script response, otherwise forward the message to the error handler
		List<ActorRef> forwards = this.forwardingRules.get(scriptResponse);
		if(forwards == null || forwards.isEmpty()) {
			streamEventMessage.addError("stream.analyzer.script.forwarding.noRules", identifier, "forwardStreamEvent", "no forwarding rule found for response");
			this.errorHandler.tell(streamEventMessage, getSelf());
			return;
		}
		
		// forward event to configured receivers
		for(final ActorRef ref : forwards) {
			if(ref != null)
				ref.tell(streamEventMessage, getSender());
		}
	}
	
	/**
	 * Applies the given script on the received {@link StreamEventMessage stream event}
	 * @param streamEventMessage
	 * @return
	 * @throws ScriptException 
	 */
	protected String analyzerStreamEvent(final StreamEventMessage streamEventMessage) throws ScriptException {
		this.scriptEngine.put("event", streamEventMessage.getContent());
		this.scriptEngine.eval(this.script);
		return (String)this.scriptEngine.get(STREAM_ANALYZER_SCRIPT_RESULT_ATTRIBUTE);
	}
}
