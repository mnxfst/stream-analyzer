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
package com.mnxfst.stream.processing.message;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;
import com.mnxfst.stream.processing.model.Error;

/**
 * Event message as expected by all analyzer and modifier components 
 * @author mnxfst
 * @since Jan 30, 2014
 */
@JsonRootName ( value = "event" )
public class StreamEventMessage implements Serializable {

	private static final long serialVersionUID = -5771490648930073652L;

	/** references the event source the message originates from */
	@JsonProperty ( value = "eventSourceId", required = true )
	private String eventSourceId = null;

	/** references the collector the message was received by */
	@JsonProperty ( value = "eventCollectorId", required = true )
	private String eventCollectorId = null;

	/** message timestamp */
	@JsonProperty ( value = "timestamp", required = true )
	private long timestamp = 0;
	
	/** message content */
	@JsonProperty ( value = "content", required = true )
	private String content = null;
	
	/** error stack */
	@JsonProperty ( value = "errorStack", required = true )
	private List<Error> errorStack = new ArrayList<>();
	
	public StreamEventMessage() {		
	}
	
	/**
	 * Initializes the message using the provided input
	 * @param eventSourceId
	 * @param eventCollectorId
	 * @param timestamp
	 * @param content
	 */
	public StreamEventMessage(final String eventSourceId, final String eventCollectorId, final long timestamp, final String content) {
		this.eventCollectorId = eventCollectorId;
		this.eventSourceId = eventSourceId;
		this.timestamp = timestamp;
		this.content = content;
	}
	
	/**
	 * Adds a new error message to the stack
	 * @param error
	 */
	public void addError(final Error error) {
		this.errorStack.add(error);
	}
	
	/**
	 * Adds a new error message to the stack
	 * @param key
	 * @param componentId
	 * @param location
	 * @param message
	 */
	public void addError(final String key, final String componentId, final String location, final String message) {
		this.errorStack.add(new Error(key, componentId, location, message));
	}

	public String getEventSourceId() {
		return eventSourceId;
	}

	public void setEventSourceId(String eventSourceId) {
		this.eventSourceId = eventSourceId;
	}

	public String getEventCollectorId() {
		return eventCollectorId;
	}

	public void setEventCollectorId(String eventCollectorId) {
		this.eventCollectorId = eventCollectorId;
	}

	public long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}
	
}
