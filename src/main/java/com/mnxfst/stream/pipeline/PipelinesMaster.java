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
import akka.actor.Props;
import akka.actor.UntypedActor;

import com.mnxfst.stream.directory.ComponentRegistry;
import com.mnxfst.stream.directory.ComponentType;
import com.mnxfst.stream.directory.message.ComponentDeregistrationMessage;
import com.mnxfst.stream.directory.message.ComponentRegistrationMessage;
import com.mnxfst.stream.directory.message.ComponentRegistrationResponseMessage;
import com.mnxfst.stream.pipeline.config.PipelineElementConfiguration;
import com.mnxfst.stream.pipeline.message.PipelineElementSetupFailedMessage;
import com.mnxfst.stream.pipeline.message.PipelineRootInitializedMessage;
import com.mnxfst.stream.pipeline.message.PipelineSetupMessage;
import com.mnxfst.stream.pipeline.message.PipelineSetupResponseMessage;
import com.mnxfst.stream.pipeline.message.PipelineShutdownMessage;
import com.mnxfst.stream.pipeline.message.PipelineShutdownResponseMessage;

/**
 * Root to all pipelines configured within the analyzer. It receives all configuration, initializes them and notifies the
 * {@link ComponentRegistry registry} about their existence 
 * @author mnxfst
 * @since 28.02.2014
 *
 */
public class PipelinesMaster extends UntypedActor {

	private final Map<String, ActorRef> pipelines = new HashMap<>();
	private ActorRef componentRegistryRef = null;
	
	/**
	 * Initializes the pipeline master using the provided input
	 * @param componentRegistryRef
	 */
	public PipelinesMaster(final ActorRef componentRegistryRef) {
		this.componentRegistryRef = componentRegistryRef;
	}
	
	/**
	 * @see akka.actor.UntypedActor#onReceive(java.lang.Object)
	 */
	public void onReceive(Object message) throws Exception {

		// TODO TESTING
		if(message instanceof PipelineSetupMessage) {
			PipelineSetupResponseMessage setupResponse = instantiatePipeline((PipelineSetupMessage)message);
			if(setupResponse != null)
				getSender().tell(setupResponse, getSelf());
		} else if(message instanceof ComponentRegistrationResponseMessage) {
			processComponentRegistrationResponse((ComponentRegistrationResponseMessage)message);
		} else if(message instanceof PipelineElementSetupFailedMessage) {
			PipelineElementSetupFailedMessage msg = (PipelineElementSetupFailedMessage)message;
			if(StringUtils.isNotBlank(msg.getPipelineId()))
				getSender().tell(new PipelineShutdownMessage(msg.getPipelineId()), getSelf());
		} else if(message instanceof PipelineShutdownResponseMessage) {
			PipelineShutdownResponseMessage msg = (PipelineShutdownResponseMessage)message;
			context().system().log().info("Pipeline '"+msg.getPipelineId()+"' successfully shut down. Shutting down pipeline root");
			context().stop(getSender());
		} else if(message instanceof PipelineShutdownMessage) {
			PipelineShutdownMessage msg = (PipelineShutdownMessage)message;
			shutdownPipeline(msg.getPipelineId(), true);
		} else if(message instanceof PipelineRootInitializedMessage) {
			PipelineRootInitializedMessage msg = (PipelineRootInitializedMessage)message;
			this.componentRegistryRef.tell(new ComponentRegistrationMessage(msg.getPipelineId(), ComponentType.PIPELINE_ROOT, getSender()), getSelf());
		}
		
		// TODO implement fail-over handling
	}
	
	/**
	 * Initializes a new pipeline using the provided settings 
	 * @param setupMessage
	 * @return 
	 */
	protected PipelineSetupResponseMessage instantiatePipeline(final PipelineSetupMessage setupMessage) {

		// ensure that the configuration is not null
		if(setupMessage.getConfiguration() == null)
			return new PipelineSetupResponseMessage(null, PipelineSetupResponseMessage.SETUP_RESPONSE_MISSING_PIPELINE_ELEMENT_CONFIGURATION);

		// pipeline identifier is required
		if(StringUtils.isBlank(setupMessage.getConfiguration().getPipelineId()))
			return new PipelineSetupResponseMessage(null, PipelineSetupResponseMessage.SETUP_RESPONSE_MISSING_PIPELINE_ID);

		// ensure that there are any element configurations
		if(setupMessage.getConfiguration().getElements() == null || setupMessage.getConfiguration().getElements().isEmpty()) 
			return new PipelineSetupResponseMessage(setupMessage.getConfiguration().getPipelineId(), PipelineSetupResponseMessage.SETUP_RESPONSE_MISSING_PIPELINE_ELEMENT_CONFIGURATION);
		
		String pipelineId = setupMessage.getConfiguration().getPipelineId();

		// ensure that no such pipeline exists
		if(this.pipelines.containsKey(pipelineId))
			return new PipelineSetupResponseMessage(setupMessage.getConfiguration().getPipelineId(), PipelineSetupResponseMessage.SETUP_RESPONSE_PIPELINE_ALREADY_EXISTS);
		
		// step through element configurations and validate settings
		for(final PipelineElementConfiguration cfg : setupMessage.getConfiguration().getElements()) {

			if(StringUtils.isBlank(cfg.getElementId()))
				return new PipelineSetupResponseMessage(pipelineId, PipelineSetupResponseMessage.SETUP_RESPONSE_MISSING_PIPELINE_ELEMENT_ID);
			if(StringUtils.isBlank(cfg.getElementClass()))
				return new PipelineSetupResponseMessage(pipelineId, PipelineSetupResponseMessage.SETUP_RESPONSE_MISSING_PIPELINE_ELEMENT_CLASS);
			
		}
		
		// instantiate pipeline root and register it locally and with component registry
		final ActorRef pipelineRootRef = context().actorOf(Props.create(PipelineRoot.class, setupMessage.getConfiguration()), setupMessage.getConfiguration().getPipelineId());
		this.pipelines.put(setupMessage.getConfiguration().getPipelineId(), pipelineRootRef);
		
		// final response
//		return new PipelineSetupResponseMessage(pipelineId, PipelineSetupResponseMessage.SETUP_RESPONSE_OK);
		return null;
	}
	
	/**
	 * Handles {@link ComponentRegistrationResponseMessage registration response} messages, especially in case of errors
	 * @param msg
	 */
	protected void processComponentRegistrationResponse(final ComponentRegistrationResponseMessage msg) {
		
		if(msg != null) {
			switch(msg.getState()) {
				case ComponentRegistrationResponseMessage.REGISTRATION_FAILED_EMPTY_MESSAGE: {
					// this case should not occur as the message has been reported to be empty ... but ...
					shutdownPipeline(msg.getId(), false);
					break;
				}
				case ComponentRegistrationResponseMessage.REGISTRATION_FAILED_MISSING_ID: {
					// same goes here :-)
					shutdownPipeline(msg.getId(), false);
					break;
				}
				case ComponentRegistrationResponseMessage.REGISTRATION_FAILED_MISSING_REFERENCE: {
					shutdownPipeline(msg.getId(), false);
					break;
				}
				case ComponentRegistrationResponseMessage.REGISTRATION_FAILED_MISSING_TYPE: {
					shutdownPipeline(msg.getId(), false);
					break;
				}
				case ComponentRegistrationResponseMessage.REGISTRATION_FAILED_NON_UNIQUE_ID: {
					shutdownPipeline(msg.getId(), false);
					break;
				}
			}
		}		
	}
	
	/**
	 * Initiates the shutdown of the referenced {@link PipelineRoot pipeline}
	 * @param pipelineId
	 * @param deregistrationRequired
	 */
	protected void shutdownPipeline(final String pipelineId, final boolean deregistrationRequired) {

		if(StringUtils.isNotBlank(pipelineId)) {
			final ActorRef pipelineRoot = this.pipelines.get(pipelineId);
			if(pipelineRoot != null) {
				pipelineRoot.tell(new PipelineShutdownMessage(pipelineId), getSelf());
				if(deregistrationRequired)
					this.componentRegistryRef.tell(new ComponentDeregistrationMessage(pipelineId, ComponentType.PIPELINE_ROOT), getSelf());
			}			
		}
	}
}
