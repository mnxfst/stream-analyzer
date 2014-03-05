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

import com.mnxfst.stream.message.StreamEventMessage;
import com.mnxfst.stream.pipeline.config.PipelineElementConfiguration;

/**
 * @author mnxfst
 * @since 04.03.2014
 *
 */
public class TestPipelineElement extends PipelineElement {

	private String logRefPath = null;
	
	public TestPipelineElement(PipelineElementConfiguration pipelineElementConfiguration) {
		super(pipelineElementConfiguration);
		logRefPath = pipelineElementConfiguration.getSettings().get("logRefPath");
	}

	/**
	 * @see akka.actor.UntypedActor#postStop()
	 */
	public void postStop() throws Exception {		
	}

	/**
	 * @see com.mnxfst.stream.pipeline.PipelineElement#onReceive(java.lang.Object)
	 */
	public void onReceive(Object message) throws Exception {
		System.out.println("Message: " + message + " to " + context().actorSelection(logRefPath));
		context().actorSelection(logRefPath).tell(message, getSelf());
	}

	/**
	 * @see com.mnxfst.stream.pipeline.PipelineElement#processEvent(com.mnxfst.stream.message.StreamEventMessage)
	 */
	protected void processEvent(StreamEventMessage message) throws Exception {
		//
	}

}
