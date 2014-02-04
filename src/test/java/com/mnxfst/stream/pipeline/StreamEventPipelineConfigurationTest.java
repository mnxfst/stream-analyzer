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


import org.junit.Test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.mnxfst.stream.evaluator.StreamEventScriptEvaluatorConfiguration;
import com.mnxfst.stream.persistence.StreamEventESWriterConfiguration;

/**
 * Test case for {@link StreamEventPipelineConfiguration}
 * @author mnxfst
 * @since Feb 4, 2014
 */
public class StreamEventPipelineConfigurationTest {

	@Test
	public void testMarhsalling() throws Exception {
		
		StreamEventPipelineConfiguration cfg = new StreamEventPipelineConfiguration("pipe-1", "test pipeline", "start");
		cfg.addPipelineNode(new StreamEventScriptEvaluatorConfiguration("script-eval-1", "test-description-1", "test-script-1", "JavaScript"));
		cfg.addPipelineNode(new StreamEventScriptEvaluatorConfiguration("script-eval-2", "test-description-2", "test-script-2", "JavaScript"));
		cfg.addPipelineNode(new StreamEventESWriterConfiguration("es-writer-1", "test-description-2"));
		
		ObjectMapper m = new ObjectMapper(new YAMLFactory());
		String json = m.writeValueAsString(cfg);
		System.out.println(json);
		
		cfg = m.readValue(json, StreamEventPipelineConfiguration.class);
		
	}
	
}
