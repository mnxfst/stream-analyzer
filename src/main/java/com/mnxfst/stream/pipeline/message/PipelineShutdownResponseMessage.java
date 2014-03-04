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
import com.mnxfst.stream.pipeline.PipelineRoot;
import com.mnxfst.stream.pipeline.PipelinesMaster;

/**
 * Notifies the {@link PipelinesMaster pipeline master} about a successful shutdown of a {@link PipelineRoot pipeline}
 * and its {@link PipelineElement elements}.
 * @author mnxfst
 * @since 04.03.2014
 *
 */
@JsonRootName ( value = "pipelineShutdownResponseMessage")
public class PipelineShutdownResponseMessage implements Serializable {

	private static final long serialVersionUID = 7587391942518688567L;

	/** identifier of shut down pipeline */
	@JsonProperty ( value = "pipelineId", required = true )
	private String pipelineId;
	
	/**
	 * Default constructor
	 */
	public PipelineShutdownResponseMessage() {		
	}
	
	/**
	 * Initializes the message using the provided input
	 * @param pipelineId
	 */
	public PipelineShutdownResponseMessage(final String pipelineId) {
		this.pipelineId = pipelineId;
	}

	public String getPipelineId() {
		return pipelineId;
	}

	public void setPipelineId(String pipelineId) {
		this.pipelineId = pipelineId;
	}
	
	
}
