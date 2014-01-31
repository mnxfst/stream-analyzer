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
 * Notifies the receivers about binding an {@link StreamEventMessage#getEventSourceId() event source}
 * with a processing pipeline. The message provides the name of the event source and holds a 
 * reference towards the pipeline entry point.
 * @author mnxfst
 * @since Jan 30, 2014
 */
@JsonRootName ( value = "bindEventSource" )
public class BindEventSourcePipelineMessage implements Serializable {

	private static final long serialVersionUID = -7607952214303140241L;

	/** references the event source that must be processed by the referenced pipeline endpoint */
	@JsonProperty ( value = "eventSourceId", required = true )
	private String eventSourceId = null;
	
	/** references the pipeline (entry point) that must receive events originating from the given source */
	@JsonProperty ( value = "pipelineEntryPointRef", required = true )
	private String pipelineEntryPointRef = null;
	
	/** message timestamp */
	@JsonProperty ( value = "timestamp", required = true )
	private long timestamp = System.currentTimeMillis();

	/**
	 * Default constructor
	 */
	public BindEventSourcePipelineMessage() {		
	}
	
	/**
	 * Initializes the message using the provided input
	 * @param eventSourceId
	 * @param pipelineEntryPointRef
	 */
	public BindEventSourcePipelineMessage(final String eventSourceId, final String pipelineEntryPointRef) {
		this.eventSourceId = eventSourceId;
		this.pipelineEntryPointRef = pipelineEntryPointRef;
	}

	public String getEventSourceId() {
		return eventSourceId;
	}

	public void setEventSourceId(String eventSourceId) {
		this.eventSourceId = eventSourceId;
	}

	public String getPipelineEntryPointRef() {
		return pipelineEntryPointRef;
	}

	public void setPipelineEntryPointRef(String pipelineEntryPointRef) {
		this.pipelineEntryPointRef = pipelineEntryPointRef;
	}

	public long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}
	
}
