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
import com.fasterxml.jackson.databind.node.ObjectNode;

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
	
	/** initial message content */
	@JsonProperty ( value = "content", required = true )
	private String content = null;
	
	/** message represented as json node which is modified throughout the pipeline */
	private ObjectNode json = null;
	
	public StreamEventMessage() {		
	}
	
	/**
	 * Initializes the message using the provided input
	 * @param eventSourceId
	 * @param eventCollectorId
	 * @param timestamp
	 * @param content
	 * @param json
	 */
	public StreamEventMessage(final String eventSourceId, final String eventCollectorId, final long timestamp, final String content, final ObjectNode json) {
		this.eventCollectorId = eventCollectorId;
		this.eventSourceId = eventSourceId;
		this.timestamp = timestamp;
		this.content = content;
		this.json = json;
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

	public ObjectNode getJson() {
		return json;
	}

	public void setJson(ObjectNode json) {
		this.json = json;
	}
	
	
}
