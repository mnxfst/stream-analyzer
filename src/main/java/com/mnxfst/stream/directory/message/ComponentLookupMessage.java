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
package com.mnxfst.stream.directory.message;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;
import com.mnxfst.stream.directory.ComponentRegistry;
import com.mnxfst.stream.directory.ComponentType;

import akka.actor.ActorRef;

/**
 * Requests the lookup of {@link ActorRef references} for a given set of component
 * identifiers. The message must be directed to the {@link ComponentRegistry component registry}
 * and will be responded to by a {@link ComponentLookupResponseMessage}. The message also
 * states the {@link ComponentType type} expected.
 * @author mnxfst
 * @since 03.03.2014
 *
 */
@JsonRootName ( value = "componentLookupMessage" )
public class ComponentLookupMessage implements Serializable {

	private static final long serialVersionUID = -6267266581266074887L;
	
	/** list of components to retrieve references for */
	@JsonProperty ( value = "componentIds", required = true )
	private Set<String> componentIds = new HashSet<>();

	/** expected type */
	@JsonProperty ( value = "type", required = true )
	private ComponentType type;
	
	/**
	 * Default constructor
	 */
	public ComponentLookupMessage() {		
	}
	
	/**
	 * Initializes the message using the provided input
	 * @param type
	 */
	public ComponentLookupMessage(final ComponentType type) {
		this.type = type;
	}
	
	/**
	 * Adds a component identifier to lookup the {@link ActorRef reference} for
	 * @param componentId
	 */
	public void addComponentId(final String componentId) {
		this.componentIds.add(componentId);
	}

	public Set<String> getComponentIds() {
		return componentIds;
	}

	public void setComponentIds(Set<String> componentIds) {
		this.componentIds = componentIds;
	}

	public ComponentType getType() {
		return type;
	}

	public void setType(ComponentType type) {
		this.type = type;
	}

	
}
