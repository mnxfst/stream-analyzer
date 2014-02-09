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
package com.mnxfst.stream.processing.evaluator;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;
import com.mnxfst.stream.processing.StreamEventProcessingNodeConfiguration;

/**
 * Common interface to all settings used to initialize derived instances of type {@link StreamEventScriptEvaluator}   
 * @author mnxfst
 * @since 03.02.2014
 */
@JsonRootName ( value = "scriptEvaluatorConfig" )
public class StreamEventScriptEvaluatorConfiguration extends StreamEventProcessingNodeConfiguration {

	private static final long serialVersionUID = -1272357972067640074L;

	/** script to be applied on inbound stream event messages */
	@JsonProperty ( value = "script", required = true )
	private String script = null;	
	/** script engine name */
	@JsonProperty ( value = "scriptEngineName", required = true )
	private String scriptEngineName = null; 
	/** rule set mapping a script result on a list of analyzers and modifiers to receive the message next */
	@JsonProperty ( value = "forwardingRules", required = true )
	private Map<String, Set<String>> forwardingRules = new HashMap<>();
	
	/**
	 * Default configuration
	 */
	public StreamEventScriptEvaluatorConfiguration() {		
	}
	
	/**
	 * Initializes the instance using the provided input
	 * @param processingNodeClass
	 * @param identifier
	 * @param description
	 * @param script
	 * @param scriptEngineName
	 */
	public StreamEventScriptEvaluatorConfiguration(final String processingNodeClass, final String identifier, final String description, final int numOfNodeInstances, final String script, final String scriptEngineName) {
		super(processingNodeClass, identifier, description, numOfNodeInstances);
		this.script = script;
		this.scriptEngineName = scriptEngineName;
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

	public String getScript() {
		return script;
	}

	public void setScript(String script) {
		this.script = script;
	}

	public String getScriptEngineName() {
		return scriptEngineName;
	}

	public void setScriptEngineName(String scriptEngineName) {
		this.scriptEngineName = scriptEngineName;
	}

	public Map<String, Set<String>> getForwardingRules() {
		return forwardingRules;
	}

	public void setForwardingRules(Map<String, Set<String>> forwardingRules) {
		this.forwardingRules = forwardingRules;
	}

	
}
