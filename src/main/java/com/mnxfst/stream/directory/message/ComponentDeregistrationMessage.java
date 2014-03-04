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

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;
import com.mnxfst.stream.directory.ComponentRegistry;
import com.mnxfst.stream.directory.ComponentType;

/**
 * Tells the {@link ComponentRegistry component registry} to deregister the referenced component
 * @author mnxfst
 * @since 04.03.2014
 *
 */
@JsonRootName ( value = "componentDeregistrationMessage" )
public class ComponentDeregistrationMessage implements Serializable {

	private static final long serialVersionUID = -7324259130742114559L;

	/** unique component identifier */
	@JsonProperty ( value = "id", required = true )	
	private String id;
	
	/** component type */
	@JsonProperty ( value = "type", required = true )
	private ComponentType type;
	
	/**
	 * Default constructor
	 */
	public ComponentDeregistrationMessage() {		
	}
	
	/**
	 * Initializes the instance using the provided input
	 * @param identifier
	 * @param type
	 */
	public ComponentDeregistrationMessage(final String identifier, final ComponentType type) {
		this.id = identifier;
		this.type = type;
	}

	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * @return the type
	 */
	public ComponentType getType() {
		return type;
	}

	/**
	 * @param type the type to set
	 */
	public void setType(ComponentType type) {
		this.type = type;
	}

		
}
