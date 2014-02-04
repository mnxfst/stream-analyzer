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
package com.mnxfst.stream.pipeline;


import java.util.HashSet;
import java.util.Set;

import org.junit.Test;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mnxfst.stream.message.StreamEventMessage;
import com.mnxfst.stream.processing.AbstractStreamEventProcessingNode;
import com.mnxfst.stream.processing.evaluator.StreamEventScriptEvaluator;
import com.mnxfst.stream.processing.evaluator.StreamEventScriptEvaluatorConfiguration;
import com.mnxfst.stream.processing.persistence.StreamEventESWriter;
import com.mnxfst.stream.processing.persistence.StreamEventESWriterConfiguration;
import com.mnxfst.stream.processing.pipeline.StreamEventPipelineConfiguration;
import com.mnxfst.stream.processing.pipeline.StreamEventPipelineEntryPoint;

/**
 * Test case for {@link StreamEventPipelineConfiguration}
 * @author mnxfst
 * @since Feb 4, 2014
 */
public class StreamEventPipelineConfigurationTest {

	@Test
	public void testMarhsalling() throws Exception {
		
		ActorSystem system = ActorSystem.create("junit");
		
		StreamEventScriptEvaluatorConfiguration evaluatorCfg = new StreamEventScriptEvaluatorConfiguration(StreamEventScriptEvaluator.class.getName(), "sv-1", "script-1", "var a = 1; var result = '2';", "JavaScript");
		Set<String> res = new HashSet<>();
		res.add("es-writer-1");
		evaluatorCfg.addForwardingRule("2", res);
		Set<String> errorHandlers = new HashSet<String>();
		errorHandlers.add("test");
		evaluatorCfg.addErrorHandlers(AbstractStreamEventProcessingNode.DEFAULT_ERROR_HANDLER, errorHandlers);
		
		StreamEventESWriterConfiguration esCfg = new StreamEventESWriterConfiguration(StreamEventESWriter.class.getName(), "es-writer-1", "test-description", "test", "junit");
		esCfg.addESClusterNode("localhost", 1);
		esCfg.addErrorHandlers(AbstractStreamEventProcessingNode.DEFAULT_ERROR_HANDLER, errorHandlers);
		
		StreamEventPipelineConfiguration cfg = new StreamEventPipelineConfiguration("pipe-1", "test pipeline", "sv-1");
		cfg.addPipelineNode(evaluatorCfg);
		cfg.addPipelineNode(esCfg);
		
		ActorRef pipeline = system.actorOf(Props.create(StreamEventPipelineEntryPoint.class, cfg), "pipeline");
		
		System.in.read();
		
		ObjectMapper m = new ObjectMapper();
		
		String json = m.writeValueAsString(cfg);
		System.out.println("jsonm: "  + json);
//		cfg = m.readValue(json, StreamEventPipelineConfiguration.class);
		
		pipeline.tell(new StreamEventMessage("es-1", "ev-1", System.currentTimeMillis(), "wtf"), null);

		System.in.read();
		
		system.shutdown();
		
	}
	
}


