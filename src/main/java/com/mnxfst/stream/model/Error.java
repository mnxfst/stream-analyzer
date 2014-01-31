/**
 * Copyright 2014 Christian Kreutzfeldt
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package com.mnxfst.stream.model;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.mnxfst.stream.message.StreamEventMessage;

/**
 * Error element which is added to the {@link StreamEventMessage stream event} on demand
 * @author mnxfst
 * @since 31.01.2014
 */
public class Error implements Serializable {

	private static final long serialVersionUID = -7316869687195331432L;

	/** error identifier */
	@JsonProperty ( value = "key", required = true )
	private String key = null;	
	/** component that reported the error */
	@JsonProperty ( value = "componentId", required = true )
	private String componentId = null;
	/** error location */
	@JsonProperty ( value = "location", required = true )
	private String location = null;
	/** message */
	@JsonProperty ( value = "message", required = true )
	private String message = null;
	
	/**
	 * Default constructor
	 */
	public Error() {		
	}
	
	/**
	 * Initializes the error using the provided input
	 * @param key
	 * @param componentId
	 * @param location
	 * @param message
	 */
	public Error(final String key, final String componentId, final String location, final String message) {
		this.key = key;
		this.componentId = componentId;
		this.location = location;
		this.message = message;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getComponentId() {
		return componentId;
	}

	public void setComponentId(String componentId) {
		this.componentId = componentId;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
	
	
}
