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
package com.mnxfst.stream.analyzer;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;
import com.mnxfst.stream.StreamEventScriptEvaluatorConfiguration;

/**
 * Provides the configuration required for a proper {@link StreamEventAnalyzer analyzer}
 * initialization
 * @author mnxfst
 * @since 03.02.2014
 */
@JsonRootName ( value = "analyzerConfig" )
public class StreamEventAnalyzerConfiguration implements StreamEventScriptEvaluatorConfiguration {

	private static final long serialVersionUID = -3858867422494437353L;

	/** analyzer id used for referencing the component from within other pipeline elements */ 
	@JsonProperty( value = "identifier", required = true )
	private String identifier = null;
	/** script to be applied on inbound stream event messages */
	@JsonProperty ( value = "script", required = true )
	private String script = null;	
	/** rule set mapping a script result on a list of analyzers and modifiers to receive the message next */
	@JsonProperty ( value = "forwardingRules", required = true )
	private Map<String, Set<String>> forwardingRules = new HashMap<>();
	/** reference towards component receiving all error inbound messages */
	@JsonProperty( value = "errorHandlerId", required = true )
	private String errorHandlerId = null;
	
	/**
	 * Default constructor
	 */
	public StreamEventAnalyzerConfiguration() {		
	}
	
	/**
	 * Initializes the instance using the provided content
	 * @param identifier
	 * @param script
	 * @param errorHandlerId
	 */
	public StreamEventAnalyzerConfiguration(final String identifier, final String script, final String errorHandlerId) {
		this.identifier = identifier;
		this.script = script;
		this.errorHandlerId = errorHandlerId;
	}
	
	
	/**
	 * Adds the provided list of forwards to given result of script evaluation. 
	 * @param result
	 * @param forwards
	 */
	public void addForwardingRule(final String result, final Set<String> forwards) {
		Set<String> rules = this.forwardingRules.get(result);
		if(rules == null)
			rules = new HashSet<>();
		if(forwards != null)
			rules.addAll(forwards);
		this.forwardingRules.put(result, forwards);
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

	public Map<String, Set<String>> getForwardingRules() {
		return forwardingRules;
	}

	public void setForwardingRules(Map<String, Set<String>> forwardingRules) {
		this.forwardingRules = forwardingRules;
	}

	public String getErrorHandlerId() {
		return errorHandlerId;
	}

	public void setErrorHandlerId(String errorHandlerId) {
		this.errorHandlerId = errorHandlerId;
	}
	
	
	
}
