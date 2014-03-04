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
package com.mnxfst.stream.dispatcher.config;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;
import com.mnxfst.stream.dispatcher.DispatchPolicy;

/**
 * Holds the configuration required for setting up a {@link DispatchPolicy dispatch policy}
 * @author mnxfst
 * @since 28.02.2014
 *
 */
@JsonRootName ( value = "dispatchPolicy" )
public class DispatchPolicyConfiguration implements Serializable {

	private static final long serialVersionUID = 5595392776721988354L;

	/** policy name */
	@JsonProperty ( value = "name", required = true )
	private String name = null;
	
	/** class providing policy implementation */
	@JsonProperty ( value = "policyClass", required = true )
	private String policyClass = null;
	
	/** settings passed to policy instance right after instantiation */
	@JsonProperty ( value = "settings", required = true )
	private Map<String, String> settings = new HashMap<>();
	
	/**
	 * Default constructor
	 */
	public DispatchPolicyConfiguration() {		
	}
	
	/**
	 * Initializes the configuration using the provided input
	 * @param name
	 * @param policyClass
	 */
	public DispatchPolicyConfiguration(final String name, final String policyClass) {
		this.name = name;
		this.policyClass = policyClass;
	}
	
	/**
	 * Adds a new setting
	 * @param key
	 * @param value
	 */
	public void addSetting(final String key, final String value) {
		this.settings.put(key, value);
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the policyClass
	 */
	public String getPolicyClass() {
		return policyClass;
	}

	/**
	 * @param policyClass the policyClass to set
	 */
	public void setPolicyClass(String policyClass) {
		this.policyClass = policyClass;
	}

	/**
	 * @return the settings
	 */
	public Map<String, String> getSettings() {
		return settings;
	}

	/**
	 * @param settings the settings to set
	 */
	public void setSettings(Map<String, String> settings) {
		this.settings = settings;
	}
	
	
}
