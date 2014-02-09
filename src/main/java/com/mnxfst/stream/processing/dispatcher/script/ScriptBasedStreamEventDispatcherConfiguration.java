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
package com.mnxfst.stream.processing.dispatcher.script;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mnxfst.stream.processing.dispatcher.StreamEventDispatcherConfiguration;

/**
 * Holds relevant information for configuring {@link ScriptBasedStreamEventDispatcher script based dispatchers}
 * @author mnxfst
 * @since Feb 9, 2014
 */
@JsonRootName ( value = "scriptBasedDispatcherConfiguration" )
public class ScriptBasedStreamEventDispatcherConfiguration extends StreamEventDispatcherConfiguration {

	private static final long serialVersionUID = -1876604307536487716L;

	/** script used for evaluating message and determine destination pipeline */
	@JsonProperty ( value = "script", required = true )
	private String script = null;
	/** script engine to use */
	@JsonProperty ( value = "scriptEngineName", required = true )
	private String scriptEngineName = null;

	/**
	 * Initializes the configuration using the provided input
	 * @param identifier
	 * @param description
	 * @param dispatcherClass
	 * @param script
	 * @param scriptEngineName
	 */
	public ScriptBasedStreamEventDispatcherConfiguration(final String identifier, final String description, final String dispatcherClass, final String script, final String scriptEngineName) {
		super(identifier, description, dispatcherClass);
		this.script = script;
		this.scriptEngineName = scriptEngineName;
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
	
	public static void main(String[] args) throws Exception {
		ObjectMapper mapper = new ObjectMapper();
		ScriptBasedStreamEventDispatcherConfiguration cfg = new ScriptBasedStreamEventDispatcherConfiguration("sip", "desc", "me.class", "script.1", "engine");
		cfg.addDestinationPipeline("destination1", "pipe1");
		cfg.addDestinationPipeline("destination1", "pipe2");
		System.out.println(mapper.writeValueAsString(cfg));
	}
	
}
