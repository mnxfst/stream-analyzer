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
import java.util.HashMap;
import java.util.Map;

import akka.actor.ActorRef;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;
import com.mnxfst.stream.directory.ComponentType;

/**
 * Response to {@link ComponentLookupMessage component lookup message}
 * @author mnxfst
 * @since 03.03.2014
 *
 */
@JsonRootName ( value = "componentLookupResponseMessage" )
public class ComponentLookupResponseMessage implements Serializable {

	private static final long serialVersionUID = -5877159755964080376L;
	
	/** holds a mapping from a requested component id towards the actor reference. missing entry or null value == no reference found */
	@JsonProperty ( value = "componentRefs", required = true )
	private Map<String, ActorRef> componentReferences = new HashMap<>();
	/** component type */
	@JsonProperty ( value = "type", required = true )
	private ComponentType type;
	
	/**
	 * Default constructor
	 */
	public ComponentLookupResponseMessage() {		
	}
	
	/**
	 * Initializes the message using the provided input
	 * @param type
	 */
	public ComponentLookupResponseMessage(final ComponentType type) {
		this.type = type;
	}
	
	/**
	 * Adds the {@link ActorRef reference} for a given component identifier
	 * @param componentId
	 * @param componentReference
	 */
	public void addComponentReference(final String componentId, final ActorRef componentReference) {
		this.componentReferences.put(componentId, componentReference);
	}

	public Map<String, ActorRef> getComponentReferences() {
		return componentReferences;
	}

	public void setComponentReferences(Map<String, ActorRef> componentReferences) {
		this.componentReferences = componentReferences;
	}

	public ComponentType getType() {
		return type;
	}

	public void setType(ComponentType type) {
		this.type = type;
	}

	/**
	 * @see java.lang.Object#hashCode()
	 */
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime
				* result
				+ ((componentReferences == null) ? 0 : componentReferences
						.hashCode());
		result = prime * result + ((type == null) ? 0 : type.hashCode());
		return result;
	}

	/**
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ComponentLookupResponseMessage other = (ComponentLookupResponseMessage) obj;
		if (componentReferences == null) {
			if (other.componentReferences != null)
				return false;
		} else if (!componentReferences.equals(other.componentReferences))
			return false;
		if (type != other.type)
			return false;
		return true;
	}
	
}
