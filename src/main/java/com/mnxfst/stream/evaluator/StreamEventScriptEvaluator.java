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

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.jetty.util.ConcurrentHashSet;

import scala.concurrent.Future;
import scala.concurrent.duration.FiniteDuration;
import akka.actor.ActorRef;
import akka.dispatch.OnComplete;

import com.mnxfst.stream.message.StreamEventMessage;
import com.mnxfst.stream.processing.AbstractStreamEventProcessingNode;

/**
 * Pipeline element which receives a script during initialization and executes that against an
 * inbound {@link StreamEventMessage message}. Depending on the result the (modified (by script)) 
 * message is forwarded to a number of configured components. 
 * @author mnxfst
 * @since 03.02.2014
 */
public class StreamEventScriptEvaluator extends AbstractStreamEventProcessingNode {

	/** timeout (in seconds) applied for actor selection */
	public static final long ACTOR_SELECTION_TIMEOUT = 2;
	/** script engine variable that holds the event message content - if the evaluator changes anything it must be written back to this variable */ 
	public static final String SCRIPT_EVENT_CONTENT = "eventContent";	
	/** script engine variable that must hold the result indicator at the end of computation - aka the value which determine the forward to use */
	public static final String SCRIPT_RESULT = "result";

	/** evaluator configuration */
	private final StreamEventScriptEvaluatorConfiguration configuration;
	/** scripting engine */
	private ScriptEngine scriptEngine;
	/** forwarding rules */
	private final Map<String, ConcurrentHashSet<ActorRef>> forwardingRules = new ConcurrentHashMap<>();
	
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

		// initialize the map of forwarding rules by adding an empty destination list for each script result 
		// to render map entry initialization later on unnecessary as it may lead to concurrency issues since 
		// the entries added to the lists 
		// TODO test this thoroughly!!!
		if(configuration.getForwardingRules() != null && !configuration.getForwardingRules().isEmpty()) {
			for(final String scriptResult : configuration.getForwardingRules().keySet()) {
				this.forwardingRules.put(scriptResult, new ConcurrentHashSet<ActorRef>());
			}

			for(final String scriptResult : configuration.getForwardingRules().keySet()) {		
				Set<String> forwards = configuration.getForwardingRules().get(scriptResult);				
				if(forwards != null && !forwards.isEmpty()) {
					for(final String fwdRefPath : forwards) {
	
						Future<ActorRef> fwdRef = context().system().actorSelection(fwdRefPath).resolveOne(FiniteDuration.apply(ACTOR_SELECTION_TIMEOUT, TimeUnit.SECONDS));
						fwdRef.onComplete(new OnComplete<ActorRef>() {
							
							/**
							 * @see akka.dispatch.OnComplete#onComplete(java.lang.Throwable, java.lang.Object)
							 */
							public void onComplete(Throwable error, ActorRef actorRef) throws Throwable {
								
								if(error == null) {
									ConcurrentHashSet<ActorRef> forwards = forwardingRules.get(scriptResult);									
									if(forwards == null)
										forwards = new ConcurrentHashSet<ActorRef>();				
									if(!forwards.contains(actorRef)) {
										forwards.add(actorRef);
										context().system().log().debug("Successfully registered " + actorRef + " as forward destination for " + scriptResult);
									} else {				
										context().system().log().debug("An identical forward destination already exists for " + scriptResult);
									}
								} else {
									throw new RuntimeException("Failed to fetch the actor reference for " + fwdRefPath + ". Reason: " + error.getMessage(), error);
								}
							}
									
						}, context().dispatcher());

					}					
				}
			}
		}	

		
		
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
		this.scriptEngine.eval(this.configuration.getScript());
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
			
		// fetch the receivers configured for the script response, otherwise forward the message to the error handler
		ConcurrentHashSet<ActorRef> forwards = this.forwardingRules.get(scriptResponse);
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
