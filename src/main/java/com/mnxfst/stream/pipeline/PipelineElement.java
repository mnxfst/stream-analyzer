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

import com.mnxfst.stream.message.StreamEventMessage;
import com.mnxfst.stream.pipeline.config.PipelineElementConfiguration;
import com.mnxfst.stream.pipeline.message.PipelineElementReferenceUnknownMessage;
import com.mnxfst.stream.pipeline.message.PipelineElementReferenceUpdateMessage;

/**
 * Common parent to all pipeline element
 * @author mnxfst
 * @since 03.03.2014
 * TODO implement retention policy
 */
public abstract class PipelineElement extends UntypedActor {

	/** configuration provided to the pipeline element */
	private final PipelineElementConfiguration pipelineElementConfiguration;
	/** destinations accessible by this element */
	private final Map<String, ActorRef> messageDestinations = new HashMap<>();

	/**
	 * Processes an inbound message and must be implemented by all elements
	 * @param message
	 * @throws Exception
	 */
	protected abstract void processEvent(StreamEventMessage message) throws Exception;
	
	/**
	 * Initializes the pipeline element using the provided input
	 * @param pipelineElementConfiguration
	 */
	public PipelineElement(final PipelineElementConfiguration pipelineElementConfiguration) {
		this.pipelineElementConfiguration = pipelineElementConfiguration;
	}

	/**
	 * @see akka.actor.UntypedActor#onReceive(java.lang.Object)
	 */
	public void onReceive(Object message) throws Exception {
		
		if(message instanceof StreamEventMessage) {
			processEvent((StreamEventMessage)message);
		} else if(message instanceof PipelineElementReferenceUpdateMessage) {
			registerMessageDestinations((PipelineElementReferenceUpdateMessage)message);
			processRetainedMessages();
		} else {
			unhandled(message);
		}
		
	}

	
	/**
	 * Reports the given error along with an error code
	 * @param errorCode
	 * @param errorMessage
	 */
	protected void reportError(final int errorCode, final String errorMessage) {
		// TODO implement error handling
		context().system().log().error("error [pipeline="+pipelineElementConfiguration.getPipelineId()+", element="+pipelineElementConfiguration.getElementId()+", code="+errorCode+", message="+errorMessage+"]");
	}
	
	/**
	 * Forwards the provided message towards the referenced {@link PipelineElement element}. If the pipeline element
	 * does not exist, a request for an reference update will be issued and the message is retained if requested
	 * @param message
	 * @param elementId
	 * @param retainOnError
	 */
	protected void forwardMessage(final StreamEventMessage message, final String elementId, boolean retainOnError) {

		if(message != null && StringUtils.isNotBlank(elementId) && this.messageDestinations.containsKey(elementId)) {			
			final ActorRef destinationRef = this.messageDestinations.get(elementId);
			if(destinationRef != null)
				destinationRef.tell(message, getSelf());
//			else
				// destination has been queried before and is unknown .. what to do?
		} else {
			context().parent().tell(new PipelineElementReferenceUnknownMessage(pipelineElementConfiguration.getPipelineId(), pipelineElementConfiguration.getElementId(), elementId), getSelf());
			// TODO retain message
		}		
	}	
	
	/**
	 * Register possible message destinations
	 * @param msg
	 */
	protected void registerMessageDestinations(final PipelineElementReferenceUpdateMessage msg) {
		
		if(msg != null && msg.getElementReferences() != null && !msg.getElementReferences().isEmpty()) {
			for(String elementId : msg.getElementReferences().keySet()) {
				
				// avoid self-references
				if(!StringUtils.equalsIgnoreCase(elementId, pipelineElementConfiguration.getElementId())) {
					ActorRef elementRef = msg.getElementReferences().get(elementId);
					if(elementRef != null) {
						this.messageDestinations.put(elementId, elementRef);
						context().system().log().info("Element '"+elementId+"' registered as destination for '"+pipelineElementConfiguration.getPipelineId()+"#"+pipelineElementConfiguration.getElementId()+"'");
					}
				}
			}
		}		
	}
	
	/**
	 * Attempts to send retained messages
	 */
	protected void processRetainedMessages() {
		
		// TODO implement
		
	}
	
	//////////////////////// CONFIGURATION ACCESS ////////////////////////
	
	/**
	 * @return the pipelineElementConfiguration
	 */
	protected PipelineElementConfiguration getPipelineElementConfiguration() {
		return pipelineElementConfiguration;
	}
	
	/**
	 * Retrieves a {@link String string} property from the {@link PipelineElementConfiguration#getSettings() element settings}
	 * @param propertyName
	 * @return
	 */
	protected String getStringProperty(final String propertyName) {
		if(pipelineElementConfiguration.getSettings().containsKey(propertyName))
			return pipelineElementConfiguration.getSettings().get(propertyName);
		return null;
	}
	
	/**
	 * Evaluates the referenced property to a boolean value
	 * @param propertyName
	 * @param defaultValue
	 * @return
	 */
	protected boolean getBooleanProperty(final String propertyName, final boolean defaultValue) {
		String value = pipelineElementConfiguration.getSettings().get(propertyName);
		if(StringUtils.isNotBlank(value)) {
			if(StringUtils.equalsIgnoreCase("true", value))
				return true;
			return false;			
		}
		return defaultValue;
	}	
	
	/**
	 * Parses the content of the referenced property into its integer representation
	 * @param propertyName
	 * @param defaultValue
	 * @return
	 */
	protected int getIntProperty(final String propertyName, final int defaultValue) {
		String value = pipelineElementConfiguration.getSettings().get(propertyName);
		if(StringUtils.isNotBlank(value)) {
			try {
				return Integer.parseInt(value);
			} catch(Exception e) {
				context().system().log().error("Failed to parse setting to integer. [pipeline="+pipelineElementConfiguration.getPipelineId()+", element="+pipelineElementConfiguration.getElementId()+", setting="+propertyName+"]");
			}
		}
		return defaultValue;			
	}
}
