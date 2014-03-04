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
package com.mnxfst.stream.message;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;

/**
 * Defines the message floating around in the system. It holds a common set of information
 * required for every message plus a content area where arbitrary data can be transported 
 * in. The only restriction on that field is that it must hold {@link Serializable serializable)
 * data. Aside all nodes accessing the content MUST ensure that they can handle the contained
 * format
 * @author mnxfst
 * @since 28.02.2014
 *
 */
@JsonRootName ( value = "event" )
public class StreamEventMessage implements Serializable {

	private static final long serialVersionUID = -5771490648930073652L;
	
	/** message identifier */
	@JsonProperty ( value = "id", required = true )
	private String identifier;
	/** message origin a.k.a. the listener which received the event */
	@JsonProperty ( value = "origin", required = true )
	private String origin;
	/** time of event ingestion, required format: "yyyy-MM-dd'T'HH:mm:ss.SSSZ" */
	@JsonProperty ( value = "timestamp", required = true )
	private String timestamp;
	/** content */
	@JsonProperty ( value = "event", required = true )
	private Serializable event;
	
	/**
	 * Default constructor
	 */
	public StreamEventMessage() {		
	}
	
	/**
	 * Initializes the instance using the provided input
	 * @param identifier
	 * @param origin
	 * @param timestamp
	 * @param event
	 */
	public StreamEventMessage(final String identifier, final String origin, final String timestamp, final Serializable event) {
		this.identifier = identifier;
		this.origin = origin;
		this.timestamp = timestamp;
		this.event = event;
	}

	public String getIdentifier() {
		return identifier;
	}

	public void setIdentifier(String identifier) {
		this.identifier = identifier;
	}

	public String getOrigin() {
		return origin;
	}

	public void setOrigin(String origin) {
		this.origin = origin;
	}

	public String getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(String timestamp) {
		this.timestamp = timestamp;
	}

	public Serializable getEvent() {
		return event;
	}

	public void setEvent(Serializable event) {
		this.event = event;
	}

}
