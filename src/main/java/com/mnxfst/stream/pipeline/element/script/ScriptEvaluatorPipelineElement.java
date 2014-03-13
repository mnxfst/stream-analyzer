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
package com.mnxfst.stream.pipeline.element.script;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

import org.apache.commons.lang3.StringUtils;

import com.mnxfst.stream.message.StreamEventMessage;
import com.mnxfst.stream.pipeline.PipelineElement;
import com.mnxfst.stream.pipeline.config.PipelineElementConfiguration;
import com.mnxfst.stream.pipeline.message.PipelineElementSetupFailedMessage;

/**
 * Implements a script evaluator which extracts the {@link StreamEventMessage#getEvent() event content}
 * from the message and applies the configured script. The evaluator may modify the message content
 * and returns the identifier of the next pipeline element which must receive the message  
 * @author mnxfst
 * @since 04.03.2014
 *
 */
public class ScriptEvaluatorPipelineElement extends PipelineElement {

	public static final int ERROR_CODE_EVENT_CONTENT_MISSING = 1;

	/** configuration option holding the name of the script engine to use */
	public static final String CONFIG_SCRIPT_ENGINE_NAME = "script.engine.name";
	/** prefix to configuration option holding the scripts to be used for initialization - script.code.init.0 ... n */
	public static final String CONFIG_SCRIPT_INIT_CODE_PREFIX = "script.code.init.";
	/** configuration option holding the script to be executed for an event */
	public static final String CONFIG_SCRIPT_EVAL_CODE = "script.code.eval";
	/** configuration option holding the variable where the script expects the input */
	public static final String CONFIG_SCRIPT_INPUT_VARIABLE = "script.var.input";
	/** configuration option holding the variable where the script writes the identifier of the next pipeline element to */
	public static final String CONFIG_SCRIPT_OUTPUT_NEXT_ELEMENT_VARIABLE = "script.var.output.nextelement";
	
	private ScriptEngine scriptEngine;
	private List<String> initScripts = new ArrayList<>();	
	private String evalScript = null;
	private String scriptInputVariable = null;
	private String scriptOutputNextElementVariable = null;
	
	public ScriptEvaluatorPipelineElement(PipelineElementConfiguration pipelineElementConfiguration) {
		super(pipelineElementConfiguration);		 
	}

	/**
	 * @see akka.actor.UntypedActor#preStart()
	 */
	public void preStart() throws Exception {		
		
		// initialize the script engine 
		ScriptEngineManager factory = new ScriptEngineManager();
		try {
			this.scriptEngine = factory.getEngineByName(getStringProperty(CONFIG_SCRIPT_ENGINE_NAME));
		} catch(Exception e) {
			context().parent().tell(new PipelineElementSetupFailedMessage(
					getPipelineElementConfiguration().getPipelineId(), getPipelineElementConfiguration().getElementId(), 
					PipelineElementSetupFailedMessage.GENERAL, e.getMessage()), getSelf());
			return;
		}

		// iterate from zero to max, read out init code snippets and interrupt if an empty one occurs
		for(int i = 0; i < Integer.MAX_VALUE; i++) {
			String initScript = getStringProperty(CONFIG_SCRIPT_INIT_CODE_PREFIX + i);
			if(StringUtils.isNotBlank(initScript)) {
				this.initScripts.add(loadScript(initScript.toString()));
			} else {
				break;
			}
		}

		// fetch the script to be applied for each message
		String scriptUrl = getStringProperty(CONFIG_SCRIPT_EVAL_CODE);
//		this.evalScript = getStringProperty(CONFIG_SCRIPT_EVAL_CODE);
		if(StringUtils.isBlank(scriptUrl)) {
			context().parent().tell(new PipelineElementSetupFailedMessage(
					getPipelineElementConfiguration().getPipelineId(), getPipelineElementConfiguration().getElementId(),
					PipelineElementSetupFailedMessage.GENERAL, "Required script code missing"), getSelf());
			return;
		}
		this.evalScript = loadScript(scriptUrl);
		
		// retrieve the name of the variable where the script expects the input 
		this.scriptInputVariable = getStringProperty(CONFIG_SCRIPT_INPUT_VARIABLE);
		if(StringUtils.isBlank(this.scriptInputVariable)) {
			context().parent().tell(new PipelineElementSetupFailedMessage(
					getPipelineElementConfiguration().getPipelineId(), getPipelineElementConfiguration().getElementId(),
					PipelineElementSetupFailedMessage.GENERAL, "Required input variable missing"), getSelf());
			return;
		}
		
		// retrieve the name of the variable where the script writes the identifier of the next pipeline element to
		this.scriptOutputNextElementVariable = getStringProperty(CONFIG_SCRIPT_OUTPUT_NEXT_ELEMENT_VARIABLE);
		if(StringUtils.isBlank(this.scriptOutputNextElementVariable)) {
			context().parent().tell(new PipelineElementSetupFailedMessage(
					getPipelineElementConfiguration().getPipelineId(), getPipelineElementConfiguration().getElementId(),
					PipelineElementSetupFailedMessage.GENERAL, "Required output (next element) variable missing"), getSelf());
			return;
		}
		
		// if the set of init scripts is not empty, provide them to the script engine 
		if(!initScripts.isEmpty()) {
			for(String script : initScripts) {
				this.scriptEngine.eval(script);
			}
		}
	}

	/**
	 * @see com.mnxfst.stream.pipeline.PipelineElement#processEvent(com.mnxfst.stream.message.StreamEventMessage)
	 */
	protected void processEvent(StreamEventMessage message) throws Exception {		

		if(message != null) {
			
			if(StringUtils.isBlank(message.getEvent())) {
				// TODO what to do?
				reportError(ERROR_CODE_EVENT_CONTENT_MISSING, "Required event content missing");
				return;
			}
		
			// provide message to script engine
			this.scriptEngine.put(scriptInputVariable, (String)message.getEvent());
			this.scriptEngine.eval(this.evalScript);
			
			// fetch the content from the input variable as it may have been modified ... if the script sets it
			// to null, it will be ignored
			String modifiedEventContent = (String)this.scriptEngine.get(scriptInputVariable);
			if(StringUtils.isNotBlank(modifiedEventContent))
				message.setEvent(modifiedEventContent);
			
			// fetch the next element identifier
			String nextElementId = (String)this.scriptEngine.get(scriptOutputNextElementVariable);
			if(StringUtils.isNotBlank(nextElementId)) {
				forwardMessage(message, nextElementId, true);
			}
			
			
		}
	}

	/**
	 * Load scrtip from url
	 * @param url
	 * @return
	 */
	protected String loadScript(final String url) throws IOException {		
		StringBuffer scriptContent = new StringBuffer();
		URL initScriptUrl = new URL(url);
		BufferedReader reader = new BufferedReader(new InputStreamReader(initScriptUrl.openStream()));
		int c = -1;
		while((c = reader.read()) != -1) {
			scriptContent.append((char)c);
		}
		return scriptContent.toString();
	}
}
