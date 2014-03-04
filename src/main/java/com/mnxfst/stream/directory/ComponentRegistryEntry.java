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

import java.io.Serializable;

import akka.actor.ActorRef;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;

/**
 * Component registry entry holding all required information
 * @author mnxfst
 * @since 03.03.2014
 *
 */
@JsonRootName ( value = "componentRegistryEntry" )
public class ComponentRegistryEntry implements Serializable {

	private static final long serialVersionUID = -231456490729518604L;
	
	/** component identifier */
	@JsonProperty ( value = "id", required = true )
	private String componentId;
	
	/** component type */
	@JsonProperty ( value = "type", required = true )
	private ComponentType type;
	
	/** reference towards component node */
	@JsonProperty ( value = "reference", required = true )
	private ActorRef reference;
	
	/**
	 * Default constructor
	 */
	public ComponentRegistryEntry() {		
	}
	
	/**
	 * Initializes the entry using the provided input
	 * @param componentId
	 * @param type
	 * @param reference
	 */
	public ComponentRegistryEntry(final String componentId, final ComponentType type, final ActorRef reference) {
		this.componentId = componentId;
		this.type = type;
		this.reference = reference;
	}

	public String getComponentId() {
		return componentId;
	}

	public void setComponentId(String componentId) {
		this.componentId = componentId;
	}

	public ComponentType getType() {
		return type;
	}

	public void setType(ComponentType type) {
		this.type = type;
	}

	public ActorRef getReference() {
		return reference;
	}

	public void setReference(ActorRef reference) {
		this.reference = reference;
	}
	

}
