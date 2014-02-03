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
package com.mnxfst.stream.pipeline;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import akka.actor.ActorRef;
import akka.actor.UntypedActor;

import com.mnxfst.stream.evaluator.StreamEventScriptEvaluatorConfiguration;
import com.mnxfst.stream.message.StreamEventMessage;

/**
 * Represents the entry point into a {@link StreamEventMessage stream event} processing pipeline. 
 * It holds a list of {@link StreamEventAnalyzer analyzers} and {@link StreamEventModifier modifiers} 
 * which are processed in order as provided. During pipeline initialization each component receives
 * its very own configuration data required for a proper setup.
 * 
 * TODO must initialize all nodes before!
 * @author mnxfst
 * @since Jan 30, 2014
 */
public class StreamEventPipelineEntryPoint extends UntypedActor {

	private final String pipelineId;
	private final ActorRef pipelineEntryPointRef;
	private final Map<String, StreamEventScriptEvaluatorConfiguration> streamEventScriptEvaluatorConfigurations = new HashMap<>();
	
	
	public StreamEventPipelineEntryPoint(final String pipelineId, final String pipelineEntryPointId, final Map<String, StreamEventScriptEvaluatorConfiguration> streamEventScriptEvaluatorConfigurations) {
		
		// the pipeline identifier is required 
		if(StringUtils.isBlank(pipelineId))
			throw new RuntimeException("Required pipeline identifier is missing");
		
		// first pipeline element to pass an inbound message to is required as the pipeline work otherwise
		if(StringUtils.isBlank(pipelineEntryPointId))
			throw new RuntimeException("Required pipeline entry point identifier is missing");
		
		if(streamEventScriptEvaluatorConfigurations.isEmpty()) 
			throw new RuntimeException("Required evaluator configurations missing");
		
		for(String evaluatorId : streamEventScriptEvaluatorConfigurations.keySet()) {
			
		}
		
//		this.pipelineEntryPointRef = null;
//		this.pipelineEntryPointRef = context().system().actorSelection(pipelineEntryPointId).resolveOne(timeout)
		
	}
	
	/**
	 * @see akka.actor.UntypedActor#onReceive(java.lang.Object)
	 */
	public void onReceive(Object message) throws Exception {
		
	}

}
