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
package com.mnxfst.stream;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import akka.actor.ActorRef;
import akka.actor.UntypedActor;

import com.mnxfst.stream.message.StreamEventMessage;

/**
 * Common base class to all script evaluating components, eg. modifiers or analyzers. It provides
 * implementations to common methods and a configuration abstraction 
 * @author mnxfst
 * @since 03.02.2014
 */
public abstract class AbstractStreamEventScriptEvaluator extends UntypedActor {

	/** script engine variable that holds the event message content */ 
	public static final String SCRIPT_INPUT = "eventContent";
	/** script engine variable that must hold the result at the end of computation */
	public static final String SCRIPT_RESULT = "result";

	/** analyzer name or identifier */
	private final String identifier;
	/** javascript used for analyzing inbound stream events */
	private final String script;
	/** scripting engine */
	private final ScriptEngine scriptEngine;
	/** error handler */
	private final ActorRef errorHandler;

	/**
	 * Initializes the evaluator using the provided input
	 * @param identifier
	 * @param script
	 */
	public AbstractStreamEventScriptEvaluator(final String identifier, final String script, final ActorRef errorHandler) {
		this.identifier = identifier;
		this.script = script;
		this.errorHandler = errorHandler;

		ScriptEngineManager scriptEngineManager = new ScriptEngineManager();
		this.scriptEngine = scriptEngineManager.getEngineByName("JavaScript");
		if(this.scriptEngine == null)
			throw new RuntimeException("Failed to initializes script engine");
	}
	
	/**
	 * Applies the given script on the received {@link StreamEventMessage stream event}
	 * @param streamEventMessage
	 * @return holds the script response
	 * @throws ScriptException 
	 */
	protected String evaluareScript(final StreamEventMessage streamEventMessage) throws ScriptException {
		this.scriptEngine.put(SCRIPT_INPUT, streamEventMessage.getContent());
		this.scriptEngine.eval(this.script);
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
}
