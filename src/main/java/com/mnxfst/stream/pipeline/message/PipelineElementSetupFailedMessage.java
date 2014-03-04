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
package com.mnxfst.stream.pipeline.message;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;
import com.mnxfst.stream.pipeline.PipelineElement;

/**
 * Indicates that the initialization of a {@link PipelineElement pipeline element} failed for some reason
 * @author mnxfst
 * @since 04.03.2014
 *
 */
@JsonRootName ( value = "pipelineElementSetupFailedMessage" )
public class PipelineElementSetupFailedMessage implements Serializable {

	private static final long serialVersionUID = 9163817849616828660L;
	
	public static final int GENERAL = 1;
	public static final int CLASS_NOT_FOUND = 2;
	public static final int NON_UNIQUE_ELEMENT_ID = 3;
	
	/** reference towards pipeline the error belongs to */
	@JsonProperty ( value = "pipelineId", required = true )
	private String pipelineId;

	/** identifier of failed element */
	@JsonProperty ( value = "elementId", required = true )
	private String elementId;
	
	/** error code */
	@JsonProperty ( value = "error", required = true )
	private int error = 0;
	
	/** error message */
	@JsonProperty ( value = "message", required = true )
	private String message;
	
	/**
	 * Default constructor
	 */
	public PipelineElementSetupFailedMessage() {		
	}
	
	/**
	 * Initializes the message using the provided input
	 * @param pipelineId
	 * @param elementId
	 * @param error
	 * @param message
	 */
	public PipelineElementSetupFailedMessage(final String pipelineId, final String elementId, final int error, final String message) {
		this.pipelineId = pipelineId;
		this.elementId = elementId;
		this.error = error;
		this.message = message;
	}

	public String getPipelineId() {
		return pipelineId;
	}

	public void setPipelineId(String pipelineId) {
		this.pipelineId = pipelineId;
	}

	public String getElementId() {
		return elementId;
	}

	public void setElementId(String elementId) {
		this.elementId = elementId;
	}

	public int getError() {
		return error;
	}

	public void setError(int error) {
		this.error = error;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
	
	
}
