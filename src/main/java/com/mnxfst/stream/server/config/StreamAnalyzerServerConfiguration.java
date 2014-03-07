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
package com.mnxfst.stream.server.config;

import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.yammer.dropwizard.config.Configuration;

/**
 * Provides all information required for setting up the stream analyzer server component
 * @author mnxfst
 * @since 06.03.2014
 *
 */
public class StreamAnalyzerServerConfiguration extends Configuration {

	/** references the json file which holds the configuration for a pipeline */
	@NotNull
	@JsonProperty ( value = "pipelineConfigurationFile", required = true )
	private String pipelineConfigurationFile = null;
	
	/** unique name of actor system to use */
	@NotNull
	@JsonProperty ( value = "actorSystemId", required = true )
	private String actorSystemId = null;
	
	/**
	 * Default constructor
	 */
	public StreamAnalyzerServerConfiguration() {		
	}

	public String getPipelineConfigurationFile() {
		return pipelineConfigurationFile;
	}

	public void setPipelineConfigurationFile(String pipelineConfigurationFile) {
		this.pipelineConfigurationFile = pipelineConfigurationFile;
	}

	public String getActorSystemId() {
		return actorSystemId;
	}

	public void setActorSystemId(String actorSystemId) {
		this.actorSystemId = actorSystemId;
	}
	
	
}
