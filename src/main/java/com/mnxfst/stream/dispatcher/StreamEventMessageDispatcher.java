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
package com.mnxfst.stream.dispatcher;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import akka.actor.ActorRef;
import akka.actor.UntypedActor;

import com.mnxfst.stream.directory.ComponentType;
import com.mnxfst.stream.directory.message.ComponentLookupMessage;
import com.mnxfst.stream.directory.message.ComponentLookupResponseMessage;
import com.mnxfst.stream.directory.message.ComponentRegistrationMessage;
import com.mnxfst.stream.directory.message.ComponentRegistrationResponseMessage;
import com.mnxfst.stream.dispatcher.config.DispatchPolicyConfiguration;
import com.mnxfst.stream.dispatcher.config.StreamEventMessageDispatcherConfiguration;
import com.mnxfst.stream.message.StreamEventMessage;

/**
 * Dispatches inbound messages according to a configured {@link DispatchPolicy policy}
 * @author mnxfst
 * @since 28.02.2014
 *
 */
public class StreamEventMessageDispatcher extends UntypedActor {

	/** dispatcher configuration */
	private final StreamEventMessageDispatcherConfiguration dispatcherConfiguration;
	/** dispatch policy */
	private DispatchPolicy dispatchPolicy;
	/** holds all destinations */
	private final Map<String, ActorRef> dispatchDestinations = new HashMap<>();
	/** reference towards component registry */
	private final ActorRef componentRegistryRef;

	/**
	 * Initializes the dispatcher using the provided input
	 * @param dispatcherConfiguration
	 * @param componentRegistryRef
	 */
	public StreamEventMessageDispatcher(final StreamEventMessageDispatcherConfiguration dispatcherConfiguration, final ActorRef componentRegistryRef) {
		this.dispatcherConfiguration = dispatcherConfiguration;
		this.componentRegistryRef = componentRegistryRef;
	}
	
	/**
	 * @see akka.actor.UntypedActor#preStart()
	 */
	public void preStart() throws Exception {
		super.preStart();
		this.dispatchPolicy = initDispatchPolicy(dispatcherConfiguration.getDispatchPolicy());
		
		// register the component with the registry
		this.componentRegistryRef.tell(new ComponentRegistrationMessage(this.dispatcherConfiguration.getId(), ComponentType.DISPATCHER, getSelf()), getSelf());
		
		// request references to dispatch destinations
		ComponentLookupMessage dispatchDestinationsLookupMessage = new ComponentLookupMessage(ComponentType.PIPELINE_ROOT);
		dispatchDestinationsLookupMessage.getComponentIds().addAll(dispatcherConfiguration.getDestinations());
		this.componentRegistryRef.tell(dispatchDestinationsLookupMessage, getSelf());
	}

	/**
	 * @see akka.actor.UntypedActor#onReceive(java.lang.Object)
	 */
	public void onReceive(Object message) throws Exception {
		
		if(message instanceof StreamEventMessage) {
		
			// handle messages of type StreamEventMessage by determining their destination and dispatching it to that instance
			dispatchMessage((StreamEventMessage)message);
		} else if(message instanceof ComponentRegistrationResponseMessage) {

			// 	handle registration response
			ComponentRegistrationResponseMessage componentRegistrationResponse = (ComponentRegistrationResponseMessage)message;
			if(componentRegistrationResponse.getState() == ComponentRegistrationResponseMessage.REGISTRATION_OK) {			
				context().system().log().info("Successfully registered dispatcher with component registry: [id="+componentRegistrationResponse.getId()+", type="+componentRegistrationResponse.getType()+", state="+componentRegistrationResponse.getState()+"]");
			} else {
				// TODO send log or report to another node?
				throw new RuntimeException("Failed to register dispatcher with component registry: [id="+componentRegistrationResponse.getId()+", type="+componentRegistrationResponse.getType()+", state="+componentRegistrationResponse.getState()+"]");
			}
		} else if(message instanceof ComponentLookupResponseMessage) {
			
			// handle component lookup response
			registerDispatchDestinations((ComponentLookupResponseMessage)message);
		}
			
	}
	
	/**
	 * Dispatches the provided message 
	 * @param destinations
	 */
	protected void dispatchMessage(StreamEventMessage message) {
		
		// ensure that only "non-null" messages are processed
		if(message != null) {
			// determine the destinations according to the configured dispatch policy
			Set<String> destIds = dispatchPolicy.determineDestinations(message);
			if(destIds != null && !destIds.isEmpty()) {
				// prepare a component lookup message that may be issued in case that not all destinations could be found
				ComponentLookupMessage componentLookupMessage = new ComponentLookupMessage(ComponentType.PIPELINE_ROOT);
				
				// step through destination identifiers, lookup the reference and forward the message
				for(String id : destIds) {
					
					final ActorRef destinationRef = this.dispatchDestinations.get(id);
					if(destinationRef != null)
						destinationRef.tell(message, getSelf());
					else
						componentLookupMessage.addComponentId(id);
					
				}
				
				// if the component lookup message holds any identifiers, issue the request towards the registry
				if(!componentLookupMessage.getComponentIds().isEmpty())
					this.componentRegistryRef.tell(componentLookupMessage, getSelf());				
			}
		}
	}
	
	/**
	 * Registers the dispatch destinations contained in the received {@link ComponentLookupMessage component lookup response}
	 * @param componentLookupResponse
	 */
	protected void registerDispatchDestinations(final ComponentLookupResponseMessage componentLookupResponse) {
		
		if(componentLookupResponse.getType() == null) {
			context().system().log().error("Component lookup response received missing the required component type");
		} else {

			// the component references must be analyzed only if they exists ... obviously, otherwise the message is ignored
			if(componentLookupResponse.getComponentReferences() != null && !componentLookupResponse.getComponentReferences().isEmpty()) {
				
				// step through component references and register valid ones
				for(String cid : componentLookupResponse.getComponentReferences().keySet()) {
					final ActorRef cref = componentLookupResponse.getComponentReferences().get(cid);
					if(cref != null) {
						dispatchDestinations.put(cid, cref);
					} else {
						context().system().log().info("Lookup for component [cid="+cid+", type="+componentLookupResponse.getType()+"] failed: no reference received");
					}
				}				
			}
		}
	}
	
	/**
	 * Instantiates the {@link DispatchPolicy dispatch policy} named in configuration
	 * @param cfg
	 * @return
	 * @throws ClassNotFoundException 
	 * @throws SecurityException 
	 * @throws NoSuchMethodException 
	 * @throws InvocationTargetException 
	 * @throws IllegalArgumentException 
	 * @throws IllegalAccessException 
	 * @throws InstantiationException 
	 */
	protected DispatchPolicy initDispatchPolicy(final DispatchPolicyConfiguration cfg) throws ClassNotFoundException, NoSuchMethodException, SecurityException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {		
		Class<?> policyClass = Class.forName(cfg.getPolicyClass());
		Constructor<?> constructor = policyClass.getConstructor(String.class);
		DispatchPolicy dispatchPolicyInstance = (DispatchPolicy)constructor.newInstance(cfg.getName());
		dispatchPolicyInstance.init(cfg.getSettings());
		return dispatchPolicyInstance;		
	}

	/**
	 * Returns the dispatcher
	 * @return
	 */
	protected DispatchPolicy getDispatchPolicy() {
		return this.dispatchPolicy;		
	}
	
}
