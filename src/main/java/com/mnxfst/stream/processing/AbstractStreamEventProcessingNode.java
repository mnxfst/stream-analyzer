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
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import akka.actor.ActorRef;
import akka.actor.UntypedActor;

import com.mnxfst.stream.processing.evaluator.StreamEventScriptEvaluator;
import com.mnxfst.stream.processing.message.PipelineNodeReferencesMessage;
import com.mnxfst.stream.processing.message.StreamEventMessage;
import com.mnxfst.stream.processing.persistence.StreamEventESWriter;
import com.mnxfst.stream.processing.pipeline.StreamEventPipelineEntryPoint;

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
	/** error handlers - one per error type (default is required) */
	private final Map<String, ActorRef> errorHandlers = new HashMap<>();

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

		this.identifier = configuration.getIdentifier();
	}
	
	/**
	 * Processes the inbound event
	 * @param message
	 * @throws Exception
	 */
	protected abstract void processEvent(Object message) throws Exception;
	
	/**
	 * @see akka.actor.UntypedActor#onReceive(java.lang.Object)
	 */
	public void onReceive(Object message) throws Exception {
		
		if(message instanceof PipelineNodeReferencesMessage) {
			registerErrorHandlers((PipelineNodeReferencesMessage)message);
		}
		
		processEvent(message);
		
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
		ActorRef keySpecificErrorHandler = this.errorHandlers.get(key);
		if(keySpecificErrorHandler == null)
			keySpecificErrorHandler = this.errorHandlers.get(DEFAULT_ERROR_HANDLER);
		
		if(keySpecificErrorHandler != null) {
			keySpecificErrorHandler.tell(streamEventMessage, getSelf());
		} else {
			context().system().log().error("Invalid error handler configuration found. Failed to report: " + key + "/"+location+"/"+message);
		}
	}
	
	/**
	 * Register {@link PipelineNodeReferencesMessage#getErrorHandlerReferences() error handlers} 
	 * @param refMessage
	 */
	private void registerErrorHandlers(final PipelineNodeReferencesMessage refMessage) {
		
		// inbound message must neither be null nor must its node references map be empty
		if(refMessage == null)
			return;
		if(refMessage.getErrorHandlerReferences() == null || refMessage.getErrorHandlerReferences().isEmpty())
			return; 

		// copy error handlers
		for(final String errorKey : refMessage.getErrorHandlerReferences().keySet()) {
			this.errorHandlers.put(errorKey, refMessage.getErrorHandlerReferences().get(errorKey));
		}

		// ensure that the error handlers contain at least the default handler
		if(!this.errorHandlers.containsKey(DEFAULT_ERROR_HANDLER)) 
			throw new RuntimeException("Missing default error handler");
	}
	
}
