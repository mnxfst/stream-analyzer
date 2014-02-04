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
package com.mnxfst.stream.processing;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;

import com.mnxfst.stream.message.PipelineNodeReferencesMessage;
import com.mnxfst.stream.message.StreamEventMessage;
import com.mnxfst.stream.processing.evaluator.StreamEventScriptEvaluator;
import com.mnxfst.stream.processing.persistence.StreamEventESWriter;
import com.mnxfst.stream.processing.pipeline.StreamEventPipelineEntryPoint;

import akka.actor.ActorRef;
import akka.actor.UntypedActor;

/**
 * Common base class to all {@link StreamEventMessage stream event} processing nodes, like
 * the {@link StreamEventScriptEvaluator script evaluator} or any {@link StreamEventESWriter database writer}.
 * These nodes can be merged to form a processing {@link StreamEventPipelineEntryPoint pipeline}.
 * @author mnxfst
 * @since 03.02.2014
 */
public abstract class AbstractStreamEventProcessingNode extends UntypedActor {

	/** reference to default error handler(s) */
	public static final String DEFAULT_ERROR_HANDLER = "default";

	/** analyzer name or identifier */
	private final String identifier;
	/** error handler references */
	private final Map<String, Set<String>> errorHandlerConfig = new HashMap<>();
	/** error handlers - 1..n per error type (default is required) */
	private final Map<String, Set<ActorRef>> errorHandlers = new HashMap<>();

	/**
	 * Initializes the node using the provided input
	 * @param identifier
	 * @param errorHandler
	 */
	public AbstractStreamEventProcessingNode(final StreamEventProcessingNodeConfiguration configuration) {
		
		if(configuration == null)
			throw new RuntimeException("Required configuration missing");		
		if(StringUtils.isBlank(configuration.getIdentifier()))
			throw new RuntimeException("Required node identifier missing");
		
		//configuration.getErrorHandlers()
		
		this.identifier = configuration.getIdentifier();
		this.errorHandlerConfig.putAll(configuration.getErrorHandlers());

		continue with error handlers
		// TODO lookup error handlers
		
		if(!this.errorHandlerConfig.containsKey(DEFAULT_ERROR_HANDLER)) 
			throw new RuntimeException("Missing default error handler");
	}
	
	/**
	 * Reports an error to the configured {@link ActorRef error handler}
	 * @param streamEventMessage
	 * @param key
	 * @param location
	 * @param message
	 */
	protected void reportError(final StreamEventMessage streamEventMessage, final String key, final String location, final String message) {
		
		// assign error
		streamEventMessage.addError(key, identifier, location, message);

		// lookup error handler(s)
		Set<ActorRef> keySpecificErrorHandlers = this.errorHandlers.get(key);
		if(keySpecificErrorHandlers == null || keySpecificErrorHandlers.isEmpty())
			keySpecificErrorHandlers = this.errorHandlers.get(DEFAULT_ERROR_HANDLER);
		
		
		if(keySpecificErrorHandlers != null) {
			// forward message
			for(ActorRef ar : keySpecificErrorHandlers)
				ar.tell(streamEventMessage, getSelf());
		} else {
			context().system().log().error("Error handler configuration invalid. Failed to report: " + key + "/"+location+"/"+message);
		}
	}
	
	/**
	 * Register {@link PipelineNodeReferencesMessage#getErrorHandlerReferences() error handlers} 
	 * @param refMessage
	 */
	protected void registerErrorHandlers(final PipelineNodeReferencesMessage refMessage) {
		
		// inbound message must neither be null nor must its node references map be empty
		if(refMessage == null)
			return;
		if(refMessage.getErrorHandlerReferences() == null || refMessage.getErrorHandlerReferences().isEmpty())
			return; // TODO valid

		
	}
	
	// TODO error handler registration
}
