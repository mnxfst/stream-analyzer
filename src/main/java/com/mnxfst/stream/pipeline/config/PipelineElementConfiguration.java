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
package com.mnxfst.stream.pipeline.config;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;

/**
 * Pipeline element configuration
 * @author mnxfst
 * @since 03.03.2014
 *
 */
@JsonRootName ( value = "pipelineElementConfiguration" )
public class PipelineElementConfiguration implements Serializable {

	private static final long serialVersionUID = -4343068515242697150L;

	/** element identifier which must be unique per pipeline */
	@JsonProperty ( value = "id", required = true )
	private String elementId;
	
	/** element description */
	@JsonProperty ( value = "description", required = true )
	private String description;
	
	/** pipeline element class */
	@JsonProperty ( value = "elementClass", required = true )
	private String elementClass;
	
	/** number of instances - accessed through round-robin-router */
	@JsonProperty ( value = "numOfInstances", required = true )
	private int numOfInstances = 1;
	
	/** settings */
	@JsonProperty ( value = "settings", required = true)
	private Map<String, Serializable> settings = new HashMap<>();

	/**
	 * Default constructor
	 */
	public PipelineElementConfiguration() {		
	}
	
	/**
	 * Initializes the constructor using the provided input
	 * @param elementId
	 * @param description
	 * @param elementClass
	 * @param numOfInstances
	 */
	public PipelineElementConfiguration(final String elementId, final String description, final String elementClass, final int numOfInstances) {
		this.elementId = elementId;
		this.description = description;
		this.elementClass = elementClass;
		this.numOfInstances = numOfInstances;
	}
	
	/**
	 * Adds a new setting to the element configuration
	 * @param key
	 * @param value
	 */
	public void addSetting(final String key, final String value) {
		this.settings.put(key, value);
	}

	public String getElementId() {
		return elementId;
	}

	public void setElementId(String elementId) {
		this.elementId = elementId;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getElementClass() {
		return elementClass;
	}

	public void setElementClass(String elementClass) {
		this.elementClass = elementClass;
	}

	public Map<String, Serializable> getSettings() {
		return settings;
	}

	public void setSettings(Map<String, Serializable> settings) {
		this.settings = settings;
	}

	public int getNumOfInstances() {
		return numOfInstances;
	}

	public void setNumOfInstances(int numOfInstances) {
		this.numOfInstances = numOfInstances;
	}
}
