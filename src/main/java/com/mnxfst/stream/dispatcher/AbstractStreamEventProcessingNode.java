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
package com.mnxfst.stream.dispatcher;

import com.mnxfst.stream.evaluator.StreamEventScriptEvaluator;
import com.mnxfst.stream.message.StreamEventMessage;
import com.mnxfst.stream.persistence.StreamEventESWriter;
import com.mnxfst.stream.pipeline.StreamEventPipelineEntryPoint;

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

	/** analyzer name or identifier */
	private final String identifier;
	/** error handler */
	private final ActorRef errorHandler;

	/**
	 * Initializes the node using the provided input
	 * @param identifier
	 * @param errorHandler
	 */
	public AbstractStreamEventProcessingNode(final String identifier, final ActorRef errorHandler) {
		this.identifier = identifier;
		this.errorHandler = errorHandler;
	}

}
