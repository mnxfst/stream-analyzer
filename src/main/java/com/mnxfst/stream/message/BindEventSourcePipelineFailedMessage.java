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
 * Indicates that binding an {@link StreamEventMessage#getEventSourceId() event source identifier}
 * to pipeline failed for some reason.
 * @author mnxfst
 * @since Jan 30, 2014
 */
@JsonRootName ( "bindEventSourceFailed")
public class BindEventSourcePipelineFailedMessage implements Serializable {

	private static final long serialVersionUID = 4008315256229394362L;

	/** references the event source that must be processed by the referenced pipeline endpoint */
	@JsonProperty ( value = "eventSourceId", required = true )
	private String eventSourceId = null;
	
	/** references the pipeline (entry point) that must receive events originating from the given source */
	@JsonProperty ( value = "pipelineEntryPointRef", required = true )
	private String pipelineEntryPointRef = null;
	
	/** error message */
	@JsonProperty ( value = "errorMessage", required = true )
	private String errorMessage = null;
	
	/** message timestamp */
	@JsonProperty ( value = "timestamp", required = true )
	private long timestamp = System.currentTimeMillis();

	/**
	 * Default constructor
	 */
	public BindEventSourcePipelineFailedMessage() {		
	}
	
	/**
	 * Initializes the message using the provided input
	 * @param eventSourceId
	 * @param pipelineEntryPointRef
	 */
	public BindEventSourcePipelineFailedMessage(final String eventSourceId, final String pipelineEntryPointRef, final String errorMessage) {
		this.eventSourceId = eventSourceId;
		this.pipelineEntryPointRef = pipelineEntryPointRef;
		this.errorMessage = errorMessage;
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
