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
import com.mnxfst.stream.pipeline.PipelineRoot;

/**
 * Reports an unknown next element towards the {@link PipelineRoot pipeline root}
 * @author mnxfst
 * @since 05.03.2014
 *
 */
@JsonRootName ( value = "pipelineUnknownElementMessage" )
public class PipelineElementReferenceUnknownMessage implements Serializable {

	private static final long serialVersionUID = -209285184846457219L;
	
	/** identifier of pipeline the reporting element belongs to */  
	@JsonProperty ( value = "pipelineId", required = true )
	private String pipelineId;
	/** identifier of reporting element */
	@JsonProperty ( value = "elementId", required = true )
	private String elementId;
	/** missing element identifier */
	@JsonProperty ( value = "unknownElementId", required = true )
	private String unknownElementId;
	
	/**
	 * Default constructor
	 */
	public PipelineElementReferenceUnknownMessage() {		
	}
	
	/**
	 * Initializes the message using the provided input
	 * @param pipelineId
	 * @param elementId
	 * @param unknownElementId
	 */
	public PipelineElementReferenceUnknownMessage(final String pipelineId, final String elementId, final String unknownElementId) {
		this.pipelineId = pipelineId;
		this.elementId = elementId;
		this.unknownElementId = unknownElementId;
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

	public String getUnknownElementId() {
		return unknownElementId;
	}

	public void setUnknownElementId(String unknownElementId) {
		this.unknownElementId = unknownElementId;
	}
	
	
}
