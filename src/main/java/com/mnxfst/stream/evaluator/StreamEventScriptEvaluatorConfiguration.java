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
package com.mnxfst.stream.evaluator;

import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;

/**
 * Common interface to all settings used to initialize derived instances of type {@link StreamEventScriptEvaluator}   
 * @author mnxfst
 * @since 03.02.2014
 */
@JsonRootName ( value = "scriptEvaluatorConfig" )
public class StreamEventScriptEvaluatorConfiguration implements Serializable {

	private static final long serialVersionUID = -1272357972067640074L;

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
	 * Default configuration
	 */
	public StreamEventScriptEvaluatorConfiguration() {		
	}
	
	/**
	 * Initializes the instance using the provided input
	 * @param identifier
	 * @param script
	 * @param errorHandlerId
	 */
	public StreamEventScriptEvaluatorConfiguration(final String identifier, final String script, final String errorHandlerId) {
		this.identifier = identifier;
		this.script = script;
		this.errorHandlerId = errorHandlerId;
	}
	
	/**
	 * Adds the provided destinations as <i>forwards</i> for the given script result 
	 * @param scriptEvaluationResult
	 * @param forwards
	 */
	public void addForwardingRule(final String scriptEvaluationResult, final Set<String> forwards) {
		if(StringUtils.isNotBlank(scriptEvaluationResult) && forwards != null && !forwards.isEmpty()) {
			Set<String> fwds = this.forwardingRules.get(scriptEvaluationResult);
			if(fwds == null)
				fwds = new HashSet<>();
			fwds.addAll(forwards);
			this.forwardingRules.put(scriptEvaluationResult, fwds);
		}
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
