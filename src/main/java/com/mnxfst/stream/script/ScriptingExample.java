package com.mnxfst.stream.script;

import java.io.FileInputStream;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

import com.mnxfst.stream.message.StreamEventMessage;

public class ScriptingExample {

	
	public static void main(String[] args)  throws Exception {
		ScriptEngineManager factory = new ScriptEngineManager();
		ScriptEngine engine = factory.getEngineByName("JavaScript");
		StreamEventMessage msg = new StreamEventMessage("tes-source", "collector", System.currentTimeMillis(),  "Content");
	    engine.put("msg", msg);
	    
	    FileInputStream fin = new FileInputStream("C:/dev/git-repo/stream-analyzer/src/main/resources/spahql.js");
	    StringBuffer f = new StringBuffer();
	    int c = 0;
	    while(( c = fin.read()) != -1) {
	    	f.append((char)c);
	    }
	    fin.close();
	    
	    fin = new FileInputStream("C:/dev/git-repo/stream-analyzer/src/main/resources/sample.json");
	    StringBuffer json = new StringBuffer();
	    c = 0;
	    while(( c = fin.read()) != -1) {
	    	json.append((char)c);
	    }
	    fin.close();

//	    engine.eval(json.toString());
	    engine.eval(f.toString());
	    engine.eval("var data = " + json.toString());
	    engine.eval("var db = SpahQL.db(data);");
	    engine.eval("var user = db.select('/draft_status'); println(user);");
	    engine.eval("db.select('/draft_status').replace('Test State');");
	    engine.eval("var user = db.select('/draft_status'); println(user);");
	    engine.eval("var result = 'true';");
	    System.out.println(engine.get("result").getClass().getName());
	    
//	    engine.p
	    
	    
	    
	}
}
