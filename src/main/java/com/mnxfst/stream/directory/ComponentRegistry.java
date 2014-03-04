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
package com.mnxfst.stream.directory;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import akka.actor.ActorRef;
import akka.actor.UntypedActor;

import com.mnxfst.stream.directory.message.ComponentDeregistrationMessage;
import com.mnxfst.stream.directory.message.ComponentDeregistrationResponseMessage;
import com.mnxfst.stream.directory.message.ComponentLookupMessage;
import com.mnxfst.stream.directory.message.ComponentLookupResponseMessage;
import com.mnxfst.stream.directory.message.ComponentRegistrationMessage;
import com.mnxfst.stream.directory.message.ComponentRegistrationResponseMessage;

/**
 * Central component registration. Each component must notify an instance of this node about
 * its existence. The reference along with an unique identifier will be written to a central
 * directory, eg. Zookeeper. 
 * @author mnxfst
 * @since 03.03.2014
 *
 */
public class ComponentRegistry extends UntypedActor {

	/**
	 * map of already registered compontents - a reference that does not exist might be 
	 * available in a remote store thus this is only a cache  
	 */
	private Map<String, ComponentRegistryEntry> registeredComponents = new HashMap<>();
	
	/**
	 * @see akka.actor.UntypedActor#onReceive(java.lang.Object)
	 */
	public void onReceive(Object message) throws Exception {

		if(message instanceof ComponentLookupMessage) {
			getSender().tell(lookupComponentReferences((ComponentLookupMessage)message), getSelf());
		} else if(message instanceof ComponentRegistrationMessage) {
			getSender().tell(registerComponent((ComponentRegistrationMessage)message), getSelf());
		} else if(message instanceof ComponentDeregistrationMessage) {
			getSender().tell(deregisterComponent((ComponentDeregistrationMessage)message), getSelf());
		}
		
	}
	
	/**
	 * Looks up the {@link ActorRef references} towards the named components
	 * @param lookupMessage 
	 * @return
	 */
	protected ComponentLookupResponseMessage lookupComponentReferences(final ComponentLookupMessage lookupMessage) {
		
		// prepare response message and step through retrieved component identifiers (if any exists and the type is not null)
		ComponentLookupResponseMessage response = new ComponentLookupResponseMessage(lookupMessage.getType());
		if(lookupMessage != null && lookupMessage.getType() != null && lookupMessage.getComponentIds() != null && !lookupMessage.getComponentIds().isEmpty()) {
			for(String cid : lookupMessage.getComponentIds()) {
				// TODO cache lookup, zookeeper and null response
				ComponentRegistryEntry registryEntry = registeredComponents.get(lookupMessage.getType()+"#"+cid);
				if(registryEntry != null) {
					response.addComponentReference(registryEntry.getComponentId(), registryEntry.getReference());
				}
			}
		}
		return response;
	}
	
	/**
	 * Registers the {@link ComponentRegistrationMessage#getId() referenced} component with the registry and
	 * assembles a {@link ComponentRegistrationResponseMessage response} to be forwarded to the sender
	 * @param msg
	 * @return
	 */
	protected ComponentRegistrationResponseMessage registerComponent(final ComponentRegistrationMessage msg) { 
		
		if(msg  != null) {
			
			if(StringUtils.isBlank(msg.getId()))
				return new ComponentRegistrationResponseMessage(msg.getId(), msg.getType(), ComponentRegistrationResponseMessage.REGISTRATION_FAILED_MISSING_ID);
			if(msg.getType() == null)
				return new ComponentRegistrationResponseMessage(msg.getId(), msg.getType(), ComponentRegistrationResponseMessage.REGISTRATION_FAILED_MISSING_TYPE);
			if(msg.getReference() == null)
				return new ComponentRegistrationResponseMessage(msg.getId(), msg.getType(), ComponentRegistrationResponseMessage.REGISTRATION_FAILED_MISSING_REFERENCE);
			
			// build component identifier from type followed by hash tag and component identifier
			String componentIdentifier = msg.getType() + "#" + msg.getId();
			if(this.registeredComponents.containsKey(componentIdentifier))
				return new ComponentRegistrationResponseMessage(msg.getId(), msg.getType(), ComponentRegistrationResponseMessage.REGISTRATION_FAILED_NON_UNIQUE_ID);
			
			// register new component using the retrieved information 
			this.registeredComponents.put(componentIdentifier, new ComponentRegistryEntry(msg.getId(), msg.getType(), msg.getReference()));
			return new ComponentRegistrationResponseMessage(msg.getId(), msg.getType(), ComponentRegistrationResponseMessage.REGISTRATION_OK);
		}

		// no content found, return error 
		return new ComponentRegistrationResponseMessage(null, null, ComponentRegistrationResponseMessage.REGISTRATION_FAILED_EMPTY_MESSAGE);
	}
	
	/**
	 * Deregisters the {@link ComponentDeregistrationMessage#getId() referenced} component from the registry and
	 * assembles a {@link ComponentDeregistrationResponseMessage response} to be forwarded to the sender
	 * @param msg
	 * @return
	 */
	protected ComponentDeregistrationResponseMessage deregisterComponent(final ComponentDeregistrationMessage msg) {
		
		if(msg != null) {
			
			if(StringUtils.isBlank(msg.getId()))
				return new ComponentDeregistrationResponseMessage(msg.getId(), msg.getType(), ComponentDeregistrationResponseMessage.DEREGISTRATION_FAILED_MISSING_ID);
			if(msg.getType() == null)
				return new ComponentDeregistrationResponseMessage(msg.getId(), msg.getType(), ComponentDeregistrationResponseMessage.DEREGISTRATION_FAILED_MISSING_TYPE);
			
			// build component identifier from type followed by hash tag and component identifier
			String componentIdentifier = msg.getType() + "#" + msg.getId();
			
			// deregister component if available
			if(this.registeredComponents.containsKey(componentIdentifier))
				this.registeredComponents.remove(componentIdentifier);
			
			// response message telling about successful deregistration
			return new ComponentDeregistrationResponseMessage(msg.getId(), msg.getType(), ComponentDeregistrationResponseMessage.DEREGISTRATION_OK);
		}
		
		// no content found - should not occur 
		return new ComponentDeregistrationResponseMessage(null, null, ComponentDeregistrationResponseMessage.DEREGISTRATION_FAILED_EMPTY_MESSAGE);
		
	}

}
