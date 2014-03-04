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
import com.mnxfst.stream.directory.ComponentRegistry;
import com.mnxfst.stream.pipeline.config.PipelineRootConfiguration;

/**
 * Requests to set up a new pipeline using the provided configuration. The pipeline
 * head will be registered with the {@link ComponentRegistry registry} such that it
 * is accessible using the unique identifier
 * @author mnxfst
 * @since 03.03.2014
 *
 */
@JsonRootName ( value = "pipelineSetupMessage" )
public class PipelineSetupMessage implements Serializable {

	private static final long serialVersionUID = -5113829678940110979L;
	
	/** pipeline configuration */
	@JsonProperty ( value = "configuration", required = true )
	private PipelineRootConfiguration configuration;
	
	/**
	 * Default constructor
	 */
	public PipelineSetupMessage() {
	}
	
	/**
	 * Initializes setup message using the provided input
	 * @param configuration
	 */
	public PipelineSetupMessage(final PipelineRootConfiguration configuration) {
		this.configuration = configuration;
	}

	public PipelineRootConfiguration getConfiguration() {
		return configuration;
	}

	public void setConfiguration(PipelineRootConfiguration configuration) {
		this.configuration = configuration;
	}
	
	
	

}
