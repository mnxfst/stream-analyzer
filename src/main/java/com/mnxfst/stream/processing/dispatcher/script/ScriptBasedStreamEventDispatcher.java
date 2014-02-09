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
package com.mnxfst.stream.processing.dispatcher.script;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import org.apache.commons.lang3.StringUtils;

import com.mnxfst.stream.processing.dispatcher.StreamEventDispatcher;
import com.mnxfst.stream.processing.message.StreamEventMessage;

/**
 * Dispatches inbound {@link StreamEventMessage stream event messages} according to their
 * evaluation against a previously configured {@link ScriptBasedStreamEventDispatcherConfiguration#getScript() script}
 * @author mnxfst
 * @since Feb 9, 2014
 */
public class ScriptBasedStreamEventDispatcher extends StreamEventDispatcher {

	/** script engine variable that holds the event message content - if the evaluator changes anything it must be written back to this variable */ 
	public static final String SCRIPT_EVENT_CONTENT = "eventContent";	
	/** script engine variable that must hold the result indicator at the end of computation - aka the value which determine the forward to use */
	public static final String SCRIPT_RESULT = "result";
	/** special forward rule that tells the dispatcher to use the event source identifier as dispatching rule */
	public static final String USE_EVENT_SOURCE_IDENTIFIER = "useEventSource";

	/** script engine name */
	private final String scriptEngineName;
	/** script */
	private final String script;

	/** script engine */
	private ScriptEngine scriptEngine = null;

	/**
	 * Initializes the dispatcher using the provided input
	 * @param configuration
	 */
	public ScriptBasedStreamEventDispatcher(final ScriptBasedStreamEventDispatcherConfiguration configuration) {
		super(configuration);
		
		if(StringUtils.isBlank(configuration.getScript()))
			throw new RuntimeException("Missing required script");
		if(StringUtils.isBlank(configuration.getScriptEngineName()))
			throw new RuntimeException("Missing required script engine");
		
		this.script = configuration.getScript();
		this.scriptEngineName = configuration.getScriptEngineName();
	}
	
	/**
	 * @see com.mnxfst.stream.processing.dispatcher.StreamEventDispatcher#preStart()
	 */
	public void preStart() throws Exception {
		super.preStart();
		
		ScriptEngineManager scriptEngineManager = new ScriptEngineManager();
		this.scriptEngine = scriptEngineManager.getEngineByName(scriptEngineName);
		if(this.scriptEngine == null)
			throw new RuntimeException("Failed to initializes script engine '"+scriptEngineName+"'");
		// TODO load all prerequisites, eg. init scripts		
	}

	/**
	 * @see com.mnxfst.stream.processing.dispatcher.StreamEventDispatcher#dispatchMessage(com.mnxfst.stream.processing.message.StreamEventMessage)
	 */
	protected void dispatchMessage(StreamEventMessage streamEventMessage) throws Exception {

		// message must not be null ... obviously - should not occur as the parent class onReceive ensures this ... but ... :-)
		if(streamEventMessage == null) 
			return;
		
		// ignore everything that shows no content
		if(StringUtils.isBlank(streamEventMessage.getContent())) {
			context().system().log().debug("Received message with empty content ... ignoring");
			return;
		}

		forwardMessage(streamEventMessage, evaluateScript(streamEventMessage));
	}

	/**
	 * Applies the given script on the received {@link StreamEventMessage stream event}
	 * @param streamEventMessage
	 * @return holds the script response
	 * @throws ScriptException 
	 */
	protected String evaluateScript(final StreamEventMessage streamEventMessage) throws ScriptException {
		this.scriptEngine.put(SCRIPT_EVENT_CONTENT, streamEventMessage.getContent());
		this.scriptEngine.eval(this.script);
		String messageContent = (String)this.scriptEngine.get(SCRIPT_EVENT_CONTENT);
		streamEventMessage.setContent(messageContent);
		return (String)this.scriptEngine.get(SCRIPT_RESULT);
	}

	
}
