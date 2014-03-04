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
 * Notifies the node that tries to register itself with the {@link ComponentRegistry component registry}
 * about the result of the attempt
 * @author mnxfst
 * @since 03.03.2014
 *
 */
@JsonRootName ( value = "componentRegistrationFailedMessage" )
public class ComponentRegistrationResponseMessage implements Serializable {

	private static final long serialVersionUID = -5788230880020866248L;
	
	public static final int REGISTRATION_OK = 0;
	public static final int REGISTRATION_FAILED_EMPTY_MESSAGE = 1;
	public static final int REGISTRATION_FAILED_MISSING_ID = 2;
	public static final int REGISTRATION_FAILED_MISSING_TYPE = 3;
	public static final int REGISTRATION_FAILED_MISSING_REFERENCE = 4;
	public static final int REGISTRATION_FAILED_NON_UNIQUE_ID = 5;
	
	/** unique component identifier */
	@JsonProperty ( value = "id", required = true )	
	private String id;
	
	/** component type */
	@JsonProperty ( value = "type", required = true )
	private ComponentType type;
	
	/** reason for registration failure */
	@JsonProperty ( value = "state", required = true )
	private int state = 0;
	
	/**
	 * Default constructor
	 */
	public ComponentRegistrationResponseMessage() {		
	}
	
	/**
	 * Initializes the message using the provided input
	 * @param id
	 * @param type
	 * @param state
	 */
	public ComponentRegistrationResponseMessage(final String id, final ComponentType type, final int state) {
		this.id = id;
		this.type = type;
		this.state = state;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public ComponentType getType() {
		return type;
	}

	public void setType(ComponentType type) {
		this.type = type;
	}

	public int getState() {
		return state;
	}

	public void setState(int state) {
		this.state = state;
	}

	/**
	 * @see java.lang.Object#hashCode()
	 */
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + state;
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
		ComponentRegistrationResponseMessage other = (ComponentRegistrationResponseMessage) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (state != other.state)
			return false;
		if (type != other.type)
			return false;
		return true;
	}

	
	
	
}
