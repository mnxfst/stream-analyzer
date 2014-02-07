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
package com.mnxfst.stream.processing.evaluator;

import java.io.FileInputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import org.apache.commons.lang3.StringUtils;

import akka.actor.ActorRef;

import com.mnxfst.stream.processing.AbstractStreamEventProcessingNode;
import com.mnxfst.stream.processing.message.PipelineNodeReferencesMessage;
import com.mnxfst.stream.processing.message.StreamEventMessage;

/**
 * Pipeline element which receives a script during initialization and executes that against an
 * inbound {@link StreamEventMessage message}. Depending on the result the (modified (by script)) 
 * message is forwarded to a number of configured components. 
 * @author mnxfst
 * @since 03.02.2014
 * TODO testing
 */
public class StreamEventScriptEvaluator extends AbstractStreamEventProcessingNode {

	/** timeout (in seconds) applied for actor selection */
	public static final long ACTOR_SELECTION_TIMEOUT = 2;
	/** script engine variable that holds the event message content - if the evaluator changes anything it must be written back to this variable */ 
	public static final String SCRIPT_EVENT_CONTENT = "eventContent";	
	/** script engine variable that must hold the result indicator at the end of computation - aka the value which determine the forward to use */
	public static final String SCRIPT_RESULT = "result";
	/** special forward rule that tells the forwarder to ignore the message as it is considered irrelevant - avoid traffic if it would be send to a dead letter box simply to ignore it */
	public static final String FORWARD_IGNORE_MESSAGE = "ignoreMessage";

	/** spahql script */
	private final String spaqhlScript;
	/** reference towards script */
	private final String script;
	/** evaluator configuration */
	private final StreamEventScriptEvaluatorConfiguration configuration;
	/** scripting engine */
	private ScriptEngine scriptEngine;
	/** forwarding rules */
	private final Map<String, Set<ActorRef>> forwardingRules = new HashMap<>();
	/** event counter */
	private long eventCount = 0;
	
	/**
	 * Initializes the evaluator using the provided input
	 * @param configuration
	 */
	public StreamEventScriptEvaluator(final StreamEventScriptEvaluatorConfiguration configuration) {		
		super(configuration);
		this.configuration = configuration;
		
		if(StringUtils.isBlank(configuration.getScript()))
			throw new RuntimeException("Missing required script");
		if(StringUtils.isBlank(configuration.getScriptEngineName()))
			throw new RuntimeException("Missing required script engine name");
		
		try {
			FileInputStream fin = new FileInputStream(configuration.getScript());
			int c = 0;
			StringBuffer buf = new StringBuffer();
			while((c = fin.read()) != -1) {
				buf.append((char)c);
			}
			fin.close();
			this.script = buf.toString();
			fin = new FileInputStream("/home/mnxfst/git/stream-analyzer/src/main/resources/spahql.js");
			c = 0;
			StringBuffer spahqlScript = new StringBuffer();
			while((c = fin.read()) != -1) {
				spahqlScript.append((char)c);
			}
			fin.close();
			this.spaqhlScript = spahqlScript.toString();
		} catch(Exception e) {
			throw new RuntimeException("Failed to read script from " + configuration.getScript() + ". Error: " + e.getMessage());
		}
		
		
	}
	
	/**
	 * @see akka.actor.UntypedActor#preStart()
	 */
	public void preStart() throws Exception {
		super.preStart();

		ScriptEngineManager scriptEngineManager = new ScriptEngineManager();
		this.scriptEngine = scriptEngineManager.getEngineByName(configuration.getScriptEngineName());
		if(this.scriptEngine == null)
			throw new RuntimeException("Failed to initializes script engine '"+configuration.getScriptEngineName()+"'");
		this.scriptEngine.eval(this.spaqhlScript);

	}

	/**
	 * @see com.mnxfst.stream.processing.AbstractStreamEventProcessingNode#processEvent(java.lang.Object)
	 */
	protected void processEvent(Object message) throws Exception {
		
		if(message instanceof StreamEventMessage) {
			
			eventCount++;
			StreamEventMessage msg = (StreamEventMessage)message;
			
			// analyze message with configured script
			String response = null;
			try {
				response = evaluateScript(msg);
			} catch(ScriptException e) {
				reportError(msg, "stream.script.evaluator.execution.failed", "evaluateScript", "Failed to execute script. Error: " + e.getMessage());
				return;
			} catch(Exception e) {
				reportError(msg, "stream.script.evaluator.execution.general", "evaluateScript", e.getMessage());
				return;
			}

			// forward message to configured receivers
			try {
				forwardStreamEvent(msg, response);
			} catch(Exception e) {
				reportError(msg,"stream.script.evaluator.forwarding.general", "forwardStreamEvent", e.getMessage());
				return;
			}
			
		} else if(message instanceof PipelineNodeReferencesMessage) {
			registerForwards((PipelineNodeReferencesMessage)message);
		}
	}	
	
	/**
	 * Applies the given script on the received {@link StreamEventMessage stream event}
	 * @param streamEventMessage
	 * @return holds the script response
	 * @throws ScriptException 
	 */
	protected String evaluateScript(final StreamEventMessage streamEventMessage) throws ScriptException {
		this.scriptEngine.put(SCRIPT_EVENT_CONTENT, streamEventMessage.getContent());
//		this.scriptEngine.eval(this.configuration.getScript());
		this.scriptEngine.eval(this.script);
		String messageContent = (String)this.scriptEngine.get(SCRIPT_EVENT_CONTENT);
		streamEventMessage.setContent(messageContent);
		return (String)this.scriptEngine.get(SCRIPT_RESULT);
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
		
		if(!StringUtils.equalsIgnoreCase(FORWARD_IGNORE_MESSAGE, scriptResponse)) {
			
			// fetch the receivers configured for the script response, otherwise forward the message to the error handler
			Set<ActorRef> forwards = this.forwardingRules.get(scriptResponse);
			if(forwards == null || forwards.isEmpty()) {
				reportError(streamEventMessage, "stream.analyzer.script.forwarding.noRules", "forwardStreamEvent", "no forwarding rule found for response '"+scriptResponse+"'");
				return;
			}
			
			// forward event to configured receivers
			for(final ActorRef ref : forwards) {
				if(ref != null)
					ref.tell(streamEventMessage, getSender());
			}
		}
	}
	
	/**
	 * Register {@link AbstractStreamEventProcessingNode forwards} according to the configuration by taking
	 * their {@link ActorRef references} from the inbound {@link PipelineNodeReferencesMessage message}
	 * @param refMessage
	 */
	protected void registerForwards(final PipelineNodeReferencesMessage refMessage) {
		
		// inbound message must neither be null nor must its node references map be empty
		if(refMessage == null)
			return;
		if(refMessage.getNodeReferences() == null || refMessage.getNodeReferences().isEmpty())
			return;
		
		// step through expected script results and fetch the configured forwards
		for(String expectedScriptResult : this.configuration.getForwardingRules().keySet()) {
			
			Set<ActorRef> procNodeReferences = new HashSet<>();
			
			// read out all process node identifiers that are configured for the expected script result
			Set<String> procNodeIdentifiers = this.configuration.getForwardingRules().get(expectedScriptResult);
			if(procNodeIdentifiers == null || procNodeIdentifiers.isEmpty())
				continue;
			
			// step through process node identifiers, check its correctness (non empty) and fetch the node reference from
			// the inbound message
			for(String procNodeRefId : procNodeIdentifiers) {
				if(StringUtils.isNotBlank(procNodeRefId)) {
					final ActorRef procNodeRef = refMessage.getNodeReferences().get(procNodeRefId);
					if(procNodeRef != null) {
						procNodeReferences.add(procNodeRef);
					}
				}
			}
			
			this.forwardingRules.put(expectedScriptResult, procNodeReferences);
		}		
		
	}
}
