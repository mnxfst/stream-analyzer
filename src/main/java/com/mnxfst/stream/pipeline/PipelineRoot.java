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

import akka.actor.ActorContext;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.actor.UntypedActor;

import com.mnxfst.stream.message.StreamEventMessage;
import com.mnxfst.stream.pipeline.config.PipelineElementConfiguration;
import com.mnxfst.stream.pipeline.config.PipelineRootConfiguration;
import com.mnxfst.stream.pipeline.message.PipelineElementSetupFailedMessage;
import com.mnxfst.stream.pipeline.message.PipelineRootInitializedMessage;
import com.mnxfst.stream.pipeline.message.PipelineShutdownMessage;

/**
 * Pipeline root node
 * @author mnxfst
 * @since 03.03.2014
 *
 */
public class PipelineRoot extends UntypedActor {

	private final PipelineRootConfiguration pipelineConfiguration;
	private final Map<String, ActorRef> pipelineElements = new HashMap<>();
	
	/**
	 * Initializes the root using the provided input
	 * @param pipelineConfiguration
	 */
	public PipelineRoot(final PipelineRootConfiguration pipelineConfiguration) {
		if(pipelineConfiguration == null)
			throw new RuntimeException("Missing required root configuration");
		
		if(StringUtils.isBlank(pipelineConfiguration.getPipelineId()))
			throw new RuntimeException("Missing required pipeline identifier");
		if(pipelineConfiguration.getElements() == null || pipelineConfiguration.getElements().isEmpty())
			throw new RuntimeException("Missing required pipeline element configurations");				

		// step through element configurations and validate settings to avoid errors during start
		for(final PipelineElementConfiguration cfg : pipelineConfiguration.getElements()) {
			if(StringUtils.isBlank(cfg.getElementId()))
				throw new RuntimeException("Missing required pipeline element id. Pipeline: " + pipelineConfiguration.getPipelineId());
			if(StringUtils.isBlank(cfg.getElementClass()))
				throw new RuntimeException("Missing required pipeline element class. Pipeline: " + pipelineConfiguration.getPipelineId());
		}
		this.pipelineConfiguration= pipelineConfiguration;
	}
	
	/**
	 * @see akka.actor.UntypedActor#preStart()
	 */
	public void preStart() throws Exception {	
		context().system().log().info("init start [pipeline="+pipelineConfiguration.getPipelineId()+"]");
		
		boolean failed = false;
		String pipelineId = pipelineConfiguration.getPipelineId();
		// iterate through pipeline element configurations and instantiate a new node for each
		for(final PipelineElementConfiguration cfg : pipelineConfiguration.getElements()) {			

			String elementId = cfg.getElementId();
			String description = cfg.getDescription();
			String elementClassName = cfg.getElementClass();
			
			//////////////////////////////////////////////////////////////////////////////////////
			// ensure that the element does not exist
			if(this.pipelineElements.containsKey(elementId)) {
				reportInitError(pipelineId, elementId, elementClassName, PipelineElementSetupFailedMessage.NON_UNIQUE_ELEMENT_ID, "Element id '"+elementId+"' already in use");
				failed = true;
				break;
			}
			// 
			//////////////////////////////////////////////////////////////////////////////////////

			//////////////////////////////////////////////////////////////////////////////////////
			// initialize element 
			context().system().log().info("init start [pipeline="+pipelineId+", element="+elementId+", description="+description+", class="+elementClassName+"]");
			try {
				this.pipelineElements.put(elementId, context().actorOf(Props.create(Class.forName(elementClassName), cfg), elementId));
			} catch(ClassNotFoundException e) {
				reportInitError(pipelineId, elementId, elementClassName, PipelineElementSetupFailedMessage.CLASS_NOT_FOUND, "Class not found");
				failed = true;
				break;
			} catch(Exception e) {
				reportInitError(pipelineId, elementId, elementClassName, PipelineElementSetupFailedMessage.GENERAL, e.getMessage());
				failed = true;
				break;
			}
			context().system().log().info("init done  [pipeline="+pipelineId+", element="+elementId+", description="+description+", class="+elementClassName+"]");
		}				
		if(failed)
			context().system().log().info("init failed [pipeline="+pipelineId+"]");
		else {
			context().system().log().info("init done  [pipeline="+pipelineId+", elementCount="+this.pipelineElements.size()+"]");
			context().parent().tell(new PipelineRootInitializedMessage(pipelineId), getSelf());
		}
	}



	/**
	 * @see akka.actor.UntypedActor#onReceive(java.lang.Object)
	 */
	public void onReceive(Object message) throws Exception {
		
		if(message instanceof StreamEventMessage) {
			// TODO
		} else if(message instanceof PipelineShutdownMessage) {
			shutdown((PipelineShutdownMessage)message);
			getSender().tell(new PipelineShutdownMessage(this.pipelineConfiguration.getPipelineId()), getSelf());
		} 

	}
	
	/**
	 * Shuts down the pipeline if the reference provided in the message is the same as
	 * the one carried by the pipeline root
	 * @param msg
	 */
	protected void shutdown(final PipelineShutdownMessage msg) {
		if(StringUtils.equalsIgnoreCase(msg.getPipelineId(), this.pipelineConfiguration.getPipelineId())) {
			for(final ActorRef elementRef : this.pipelineElements.values()) {
				context().stop(elementRef);
			}
		}
	}
	
	/**
	 * Report an initialization error by sending the information to the {@link ActorSystem#log() log} as well as the towards the {@link ActorContext#parent() parent}
	 * @param pipelineId
	 * @param elementId
	 * @param elementClass
	 * @param errorCode
	 * @param errorMessage
	 */
	protected void reportInitError(final String pipelineId, final String elementId, final String elementClass, final int errorCode, final String errorMessage) {
		context().system().log().error("Failed to instantiate pipeline element [pipeline="+pipelineId+", element="+elementId+", class="+elementClass+", error="+errorMessage+"]");
		context().parent().tell(new PipelineElementSetupFailedMessage(
				pipelineId, elementId, errorCode, "Element class '"+elementClass+"' not found"), getSelf());
	}

}
