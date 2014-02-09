/**
 * Copyright 2014 Christian Kreutzfeldt
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
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
import java.util.HashSet;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonRootName;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.mnxfst.stream.listener.webtrends.WebTrendsStreamListenerConfiguration;
import com.mnxfst.stream.processing.dispatcher.StreamEventDispatcher;

/**
 * Common interface for listener configuration
 * @author mnxfst
 * @since 06.02.2014
 *
 */
@JsonRootName ( value = "listenerConfiguration" )
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
@JsonSubTypes ({ @JsonSubTypes.Type(WebTrendsStreamListenerConfiguration.class) })
public abstract class StreamEventListenerConfiguration implements Serializable {

	private static final long serialVersionUID = -193085026753017503L;

	/** event listener identifier */
	private String identifier = null;
	/** listener class */
	private String listenerClassName = null;
	/** references towards dispatchers receiving the inbound traffic */
	private Set<String> dispatchers = new HashSet<>();

	/**
	 * Default constructor
	 */
	public StreamEventListenerConfiguration() {		
	}
	
	/**
	 * Initializes the configuration using the provided input
	 * @param identifier
	 * @param listenerClassName
	 */
	public StreamEventListenerConfiguration(final String identifier, final String listenerClassName) {
		this.identifier = identifier;
		this.listenerClassName = listenerClassName;
	}
	
	/**
	 * Adds a new references towards a {@link StreamEventDispatcher dispatcher} that
	 * receives traffic from the listener configured through these settings
	 * @param dispatcherIdentifier
	 */
	public void addDispatcher(final String dispatcherIdentifier) {
		this.dispatchers.add(dispatcherIdentifier);
	}

	public String getIdentifier() {
		return identifier;
	}

	public void setIdentifier(String identifier) {
		this.identifier = identifier;
	}

	public String getListenerClassName() {
		return listenerClassName;
	}

	public void setListenerClassName(String listenerClassName) {
		this.listenerClassName = listenerClassName;
	}

	public Set<String> getDispatchers() {
		return dispatchers;
	}

	public void setDispatchers(Set<String> dispatchers) {
		this.dispatchers = dispatchers;
	}
	
	
}
