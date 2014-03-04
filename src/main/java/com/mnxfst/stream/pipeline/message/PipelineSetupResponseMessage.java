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

/**
 * Response towards {@link PipelineSetupMessage pipeline setup request}
 * @author mnxfst
 * @since 03.03.2014
 *
 */
@JsonRootName ( value = "pipelineSetupResponseMessage" )
public class PipelineSetupResponseMessage implements Serializable {

	private static final long serialVersionUID = 3561879505103168575L;
	
	public static final int SETUP_RESPONSE_OK = 0;
	public static final int SETUP_RESPONSE_MISSING_PIPELINE_ID = 1;
	public static final int SETUP_RESPONSE_MISSING_PIPELINE_ELEMENT_CONFIGURATION = 2;
	public static final int SETUP_RESPONSE_MISSING_PIPELINE_ELEMENT_ID = 3;
	public static final int SETUP_RESPONSE_MISSING_PIPELINE_ELEMENT_CLASS = 4;
	public static final int SETUP_RESPONSE_MISSING_PIPELINE_ELEMENT_SETTINGS = 5;
	public static final int SETUP_RESPONSE_PIPELINE_ALREADY_EXISTS = 6;
	
	/** pipeline identifier assigned to setup message */
	@JsonProperty ( value = "id", required = true )
	private String pipelineId;
	
	/** response code */
	@JsonProperty ( value = "responseCode", required = true )
	private int responseCode = SETUP_RESPONSE_OK;
	
	/** optional response message */
	@JsonProperty ( value = "responeMessage", required = false )
	private String responseMessage;
	
	/**
	 * Default constructor
	 */
	public PipelineSetupResponseMessage() {		
	}
	
	/**
	 * Initializes the message using the provided input
	 * @param pipelineId
	 * @param responseCode
	 * @param responseMessage
	 */
	public PipelineSetupResponseMessage(final String pipelineId, final int responseCode, final String responseMessage) {
		this.pipelineId = pipelineId;
		this.responseCode = responseCode;
		this.responseMessage = responseMessage;
	}
	
	/**
	 * Initializes the message using the provided input
	 * @param pipelineId
	 * @param responseCode
	 */
	public PipelineSetupResponseMessage(final String pipelineId, final int responseCode) {
		this.pipelineId = pipelineId;
		this.responseCode = responseCode;
	}

	public String getPipelineId() {
		return pipelineId;
	}

	public void setPipelineId(String pipelineId) {
		this.pipelineId = pipelineId;
	}

	public int getResponseCode() {
		return responseCode;
	}

	public void setResponseCode(int responseCode) {
		this.responseCode = responseCode;
	}

	public String getResponseMessage() {
		return responseMessage;
	}

	public void setResponseMessage(String responseMessage) {
		this.responseMessage = responseMessage;
	}

	
}
