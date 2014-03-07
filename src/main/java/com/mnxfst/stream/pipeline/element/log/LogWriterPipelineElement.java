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
package com.mnxfst.stream.pipeline.element.log;

import akka.actor.ActorSystem;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mnxfst.stream.message.StreamEventMessage;
import com.mnxfst.stream.pipeline.PipelineElement;
import com.mnxfst.stream.pipeline.config.PipelineElementConfiguration;

/**
 * Simple pipeline element which sends all inbound messages to {@link ActorSystem#log() log} 
 * @author mnxfst
 * @since Mar 7, 2014
 *
 */
public class LogWriterPipelineElement extends PipelineElement {

	// messages will be serialized to json before writing to log
	private final ObjectMapper mapper = new ObjectMapper();
	
	/**
	 * Initializes the element using the provided input
	 * @param pipelineElementConfiguration
	 */
	public LogWriterPipelineElement(PipelineElementConfiguration pipelineElementConfiguration) {
		super(pipelineElementConfiguration);
	}

	/**
	 * @see com.mnxfst.stream.pipeline.PipelineElement#processEvent(com.mnxfst.stream.message.StreamEventMessage)
	 */
	protected void processEvent(StreamEventMessage message) throws Exception {
		if(message != null)
			context().system().log().info(mapper.writeValueAsString(message));
	}

}
