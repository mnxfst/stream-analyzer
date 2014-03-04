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
 * Reports the completed initialization of {@link PipelineElement elements} belonging to a {@link PipelineRoot pipeline root}.
 * The message is sent towards the {@link PipelinesMaster pipelines master} which in turn notifies the initiator about it.
 * @author mnxfst
 * @since 04.03.2014
 *
 */
@JsonRootName ( value = "pipelineElementsInitializedMessage" )
public class PipelineRootInitializedMessage implements Serializable {

	private static final long serialVersionUID = 3959815309927630698L;

	/** identifier of pipeline the elements belong to */
	@JsonProperty ( value = "pipelineId", required = true )
	private String pipelineId;
	
	/**
	 * Default constructor
	 */
	public PipelineRootInitializedMessage() {		
	}
	
	/**
	 * Initializes the message using the provided input
	 * @param pipelineId
	 */
	public PipelineRootInitializedMessage(final String pipelineId) {
		this.pipelineId = pipelineId;
	}

	public String getPipelineId() {
		return pipelineId;
	}

	public void setPipelineId(String pipelineId) {
		this.pipelineId = pipelineId;
	}
	
}
