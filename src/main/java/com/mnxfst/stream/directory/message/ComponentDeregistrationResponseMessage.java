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
import com.mnxfst.stream.directory.ComponentType;

/**
 * Response to {@link ComponentDeregistrationResponseMessage deregistration} request
 * @author mnxfst
 * @since 04.03.2014
 *
 */
public class ComponentDeregistrationResponseMessage implements Serializable {

	private static final long serialVersionUID = 4225041216374820327L;
	
	public static final int DEREGISTRATION_OK = 0;
	public static final int DEREGISTRATION_FAILED_MISSING_ID = 1;
	public static final int DEREGISTRATION_FAILED_MISSING_TYPE = 2;
	public static final int DEREGISTRATION_FAILED_EMPTY_MESSAGE = 3;
	
	/** unique component identifier */
	@JsonProperty ( value = "id", required = true )	
	private String id;
	
	/** component type */
	@JsonProperty ( value = "type", required = true )
	private ComponentType type;
	
	/** state */
	@JsonProperty ( value = "state", required = true )
	private int state = DEREGISTRATION_OK;
	
	/**
	 * Default constructor
	 */
	public ComponentDeregistrationResponseMessage() {		
	}
	
	/**
	 * Initializes the instance using the provided input
	 * @param identifier
	 * @param type
	 * @param state
	 */
	public ComponentDeregistrationResponseMessage(final String identifier, final ComponentType type, final int state) {
		this.id = identifier;
		this.type = type;
		this.state = state;
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

	public int getState() {
		return state;
	}

	public void setState(int state) {
		this.state = state;
	}	

}
