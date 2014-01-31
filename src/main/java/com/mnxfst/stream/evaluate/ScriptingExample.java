package com.mnxfst.stream.evaluate;

import java.util.Date;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

import com.mnxfst.stream.message.StreamEventMessage;

public class ScriptingExample {

	
	public static void main(String[] args)  throws Exception {
		ScriptEngineManager factory = new ScriptEngineManager();
		ScriptEngine engine = factory.getEngineByName("rhino");
		StreamEventMessage msg = new StreamEventMessage("tes-source", "collector", System.currentTimeMillis(),  "Content",  null);
	    engine.put("msg", msg);
	    engine.eval("if(msg.eventCollectorId == 'collector') { print(msg.eventSourceId)}");
	}
}
