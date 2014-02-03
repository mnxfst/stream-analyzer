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
package com.mnxfst.stream.modifier;

import java.util.HashSet;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.mnxfst.stream.StreamEventScriptEvaluatorConfiguration;

/**
 * Provides the configuration required for a proper {@link StreamEventModifier modifier}
 * initialization
 * @author mnxfst
 * @since 03.02.2014
 */
public class StreamEventModifierConfiguration implements StreamEventScriptEvaluatorConfiguration {

	private static final long serialVersionUID = -7864121608291767788L;
	
	/** modifier id used for referencing the component from within other pipeline elements */ 
	@JsonProperty( value = "identifier", required = true )
	private String identifier = null;
	/** script to be applied on inbound stream event messages */
	@JsonProperty ( value = "script", required = true )
	private String script = null;	
	/** list of analyzers and modifiers to receive the message next */
	@JsonProperty ( value = "forwardingRules", required = true )
	private Set<String> forwardingRules = new HashSet<>();
	/** reference towards component receiving all error inbound messages */
	@JsonProperty( value = "errorHandlerId", required = true )
	private String errorHandlerId = null;
	
	/**
	 * Default constructor
	 */
	public StreamEventModifierConfiguration() {		
	}
	
	/**
	 * Initializes the instance using the provided content
	 * @param identifier
	 * @param script
	 * @param errorHandlerId
	 */
	public StreamEventModifierConfiguration(final String identifier, final String script, final String errorHandlerId) {
		this.identifier = identifier;
		this.script = script;
		this.errorHandlerId = errorHandlerId;
	}
		
	/**
	 * Adds the provided list of forwards to given result of script evaluation
	 * @param result
	 * @param forwards
	 */
	public void addForwardingRule(final Set<String> forwards) {
		if(forwards != null)
			this.forwardingRules.addAll(forwards);
	}

	public String getIdentifier() {
		return identifier;
	}

	public void setIdentifier(String identifier) {
		this.identifier = identifier;
	}

	public String getScript() {
		return script;
	}

	public void setScript(String script) {
		this.script = script;
	}
	
	public Set<String> getForwardingRules() {
		return forwardingRules;
	}

	public void setForwardingRules(Set<String> forwardingRules) {
		this.forwardingRules = forwardingRules;
	}

	public String getErrorHandlerId() {
		return errorHandlerId;
	}

	public void setErrorHandlerId(String errorHandlerId) {
		this.errorHandlerId = errorHandlerId;
	}

}
