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
package com.mnxfst.stream.listener;

import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;

/**
 * Common base for all {@link StreamEventListener event listener configurations}
 * @author mnxfst
 * @since 28.02.2014
 *
 */
@JsonRootName ( value = "streamEventListenerConfiguration" )
public class StreamEventListenerConfiguration implements Serializable {

	private static final long serialVersionUID = -6549690615195743137L;

	@JsonProperty ( value = "listenerClass", required = true )
	private String listenerClass = null;
	
	@JsonProperty ( value = "id", required = true )
	private String id = null;
	
	@JsonProperty ( value = "name", required = true )
	private String name = null;
	
	@JsonProperty ( value = "description", required = true ) 
	private String description = null;
	
	@JsonProperty ( value = "version", required = true )
	private String version = null;
	
	@JsonProperty ( value = "dispatchers", required = true )
	private Set<String> dispatchers = new HashSet<>();
	
	@JsonProperty ( value = "settings", required = true ) 
	private Map<String, String> settings = new HashMap<>();
	
	/**
	 * Default constructor - quite obvious, eh ;-)
	 */
	public StreamEventListenerConfiguration() {		
	}
	
	/**
	 * Initializes the configuration using the provided input
	 * @param listenerClass
	 * @param id
	 * @param name
	 * @param description
	 * @param version
	 */
	public StreamEventListenerConfiguration(final String listenerClass, final String id, final String name, final String description, final String version) {
		this.listenerClass = listenerClass;
		this.id = id;
		this.name = name;
		this.description = description;
		this.version = version;
	}

	/**
	 * Adds a new dispatcher as destination of inbound messages
	 * @param dispatcherId
	 */
	public void addDispatcher(final String dispatcherId) {
		if(StringUtils.isNotBlank(dispatcherId))
			this.dispatchers.add(dispatcherId);
	}
	
	/**
	 * Adds a new setting
	 * @param key
	 * @param value
	 */
	public void addSetting(final String key, final String value) {
		this.settings.put(key, value);
	}
	
	public String getListenerClass() {
		return listenerClass;
	}

	public void setListenerClass(String listenerClass) {
		this.listenerClass = listenerClass;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public Set<String> getDispatchers() {
		return dispatchers;
	}

	public void setDispatchers(Set<String> dispatchers) {
		this.dispatchers = dispatchers;
	}

	public Map<String, String> getSettings() {
		return settings;
	}

	public void setSettings(Map<String, String> settings) {
		this.settings = settings;
	}
	 
	
}
