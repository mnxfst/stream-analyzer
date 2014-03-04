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
import java.util.HashSet;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;
import com.mnxfst.stream.dispatcher.StreamEventMessageDispatcher;

/**
 * Holds all relevant information required for properly setting up an {@link StreamEventMessageDispatcher stream event message dispatcher} 
 * @author mnxfst
 * @since 28.02.2014
 *
 */
@JsonRootName ( value = "dispatcherConfiguration" )
public class StreamEventMessageDispatcherConfiguraton implements Serializable {

	private static final long serialVersionUID = 5180913580358704014L;
	
	/** identifier to be used for referencing the dispatcher, eg. by stream listeners */
	@JsonProperty ( value = "id", required = true )
	private String id = null;
	/** dispatcher name */
	@JsonProperty ( value = "name", required = true )
	private String name = null;
	/** description */
	@JsonProperty ( value = "description", required = true )
	private String description = null;
	/** destinations */
	@JsonProperty ( value = "destinations", required = true )
	private Set<String> destinations = new HashSet<>();
	/** dispatch policy */
	@JsonProperty ( value = "dispatchPolicy", required = true )
	private DispatchPolicyConfiguration dispatchPolicy = null;
	
	/**
	 * Default constructor
	 */
	public StreamEventMessageDispatcherConfiguraton() {		
	}
	
	/**
	 * Initializes the dispatcher configuration using the provided input
	 * @param id
	 * @param name
	 * @param description
	 * @param dispatchPolicy
	 */
	public StreamEventMessageDispatcherConfiguraton(final String id, final String name, final String description, final DispatchPolicyConfiguration dispatchPolicy) {
		this.id = id;
		this.name = name;
		this.description = description;
		this.dispatchPolicy = dispatchPolicy;
	}
	
	public void addDestination(final String destinationId) {
		this.destinations.add(destinationId);
	}

	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(String id) {
		this.id = id;
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
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * @param description the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * @return the destinations
	 */
	public Set<String> getDestinations() {
		return destinations;
	}

	/**
	 * @param destinations the destinations to set
	 */
	public void setDestinations(Set<String> destinations) {
		this.destinations = destinations;
	}

	/**
	 * @return the dispatchPolicy
	 */
	public DispatchPolicyConfiguration getDispatchPolicy() {
		return dispatchPolicy;
	}

	/**
	 * @param dispatchPolicy the dispatchPolicy to set
	 */
	public void setDispatchPolicy(DispatchPolicyConfiguration dispatchPolicy) {
		this.dispatchPolicy = dispatchPolicy;
	}
	
	
}
