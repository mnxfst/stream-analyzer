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

import java.io.FileReader;
import java.io.PrintWriter;
import java.io.StringWriter;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

import org.apache.commons.lang3.StringUtils;
import org.junit.Test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mnxfst.stream.message.StreamEventMessage;

/**
 * Test case for {@link ScriptEvaluatorPipelineElement}
 * @author mnxfst
 * @since 05.03.2014
 *
 */
public class ScriptEvaluatorPipelineElementTest {

	/**
	 * This is not a test case but more a kind of a sandbox for fiddling around with the script engine
	 */
	@Test
	public void testEvaluateScriptWithReturn() throws Exception {
		
		ScriptEngineManager factory = new ScriptEngineManager();
		ScriptEngine engine = factory.getEngineByName("JavaScript");
		StringWriter writer = new StringWriter ();
	    PrintWriter pw = new PrintWriter (writer, true);
	    engine.getContext ().setWriter (pw);
	    
	    engine.eval(new FileReader("/home/mnxfst/git/stream-analyzer/src/main/resources/spahql.js"));
	    
	    String script = "var result0 = '1';var result1 = '2';";
	    engine.eval(script);
	    StringBuffer sb = writer.getBuffer ();
	    System.out.println ("StringBuffer contains: " + sb + " - " + engine.get("test"));
	    System.out.println(engine.get("content"));
	    
	    for(int i = 0; i < Integer.MAX_VALUE; i++) {
	    	String content = (String)engine.get("result"+i);
	    	if(StringUtils.isBlank(content))
	    		break;
	    	System.out.println(content);
	    }
	    

		ObjectMapper mapper = new ObjectMapper();
		StreamEventMessage message = new StreamEventMessage();
		message.setIdentifier("test-id");
		message.setOrigin("test-origin");
		message.setTimestamp("2014-03-05");
		message.setEvent("10");
//		message.addCustomAttribute("test-key-1", "3");
//		message.addCustomAttribute("test-key-2", "value-1");
//		message.addCustomAttribute("test-key-3", "19");
//		message.addCustomAttribute("errors", (String)engine.get("test"));
			
		String json = mapper.writeValueAsString(message);
		System.out.println(json);
		StreamEventMessage msg = mapper.readValue(json,  StreamEventMessage.class);
		System.out.println(message.getCustomAttributes().get("json"));
	    
		
	}
	
}
