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
package com.mnxfst.stream.listener.webtrends;

import java.io.IOException;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Assert;
import org.junit.Test;

/**
 * Test case for {@link WebtrendsStreamListenerConfiguration}
 * @author mnxfst
 * @since 28.02.2014
 *
 */
public class WebtrendsStreamListenerConfigurationTest {

	/**
	 * Serializes an instance of {@link WebtrendsStreamListenerConfiguration}, deserializes it
	 * and checks if all properties are still available - just to see if abstraction works
	 * @throws IOException 
	 * @throws JsonMappingException 
	 * @throws JsonGenerationException 
	 */
	@Test
	public void testSerializeDeserializeWithValidInput() throws JsonGenerationException, JsonMappingException, IOException {

		ObjectMapper mapper = new ObjectMapper();		

		String id = "testId";
		String name = "testName";
		String description = "testDescription";
		String version = "testVersion";
		String authUrl = "testAuthUrl";
		String authAudience = "testAuthAudience";
		String authScope = "testAuthScope";
		String eventStreamUrl = "testUrl";
		String clientId = "testClientId";
		String clientSecret = "testSecret";
		String streamType = "testStreamType";
		String streamQuery = "testStreamQuery";
		String streamVersion = "testStreamVersion";
		String schemaVersion = "testSchemaVersion";
		
		WebtrendsStreamListenerConfiguration cfg = new WebtrendsStreamListenerConfiguration(
				id, name, description, version, authUrl, authScope, authAudience, eventStreamUrl, clientId, clientSecret, streamType, streamQuery, streamVersion, schemaVersion);
		cfg.addDispatcher("test-dispatcher-1");
		Assert.assertEquals("The size of dispatcher set must be 1", 1, cfg.getDispatchers().size());
		cfg.addDispatcher("");
		Assert.assertEquals("The size of dispatcher set must be 1", 1, cfg.getDispatchers().size());
		cfg.addDispatcher(null);
		Assert.assertEquals("The size of dispatcher set must be 1", 1, cfg.getDispatchers().size());
		cfg.addDispatcher("test-dispatcher-2");
		Assert.assertEquals("The size of dispatcher set must be 2", 2, cfg.getDispatchers().size());

		// serialize and check
		String serializedCfg = mapper.writeValueAsString(cfg);		
		Assert.assertNotNull("Serialized configuration must not be null", serializedCfg);
		Assert.assertFalse("Serialized configuration must not be empty", serializedCfg.isEmpty());
		
		// deserialize and check 
		WebtrendsStreamListenerConfiguration deserializedCfg = mapper.readValue(serializedCfg, WebtrendsStreamListenerConfiguration.class);
		Assert.assertNotEquals("The deserialized configuration must not be null", deserializedCfg);
		Assert.assertEquals("The contents of both must be equal", cfg.getId(), deserializedCfg.getId());
		Assert.assertEquals("The contents of both must be equal", cfg.getName(), deserializedCfg.getName());
		Assert.assertEquals("The contents of both must be equal", cfg.getDescription(), deserializedCfg.getDescription());
		Assert.assertEquals("The contents of both must be equal", cfg.getVersion(), deserializedCfg.getVersion());
		Assert.assertEquals("The contents of both must be equal", cfg.getAuthUrl(), deserializedCfg.getAuthUrl());
		Assert.assertEquals("The contents of both must be equal", cfg.getAuthScope(), deserializedCfg.getAuthScope());
		Assert.assertEquals("The contents of both must be equal", cfg.getAuthAudience(), deserializedCfg.getAuthAudience());
		Assert.assertEquals("The contents of both must be equal", cfg.getEventStreamUrl(), deserializedCfg.getEventStreamUrl());
		Assert.assertEquals("The contents of both must be equal", cfg.getClientId(), deserializedCfg.getClientId());
		Assert.assertEquals("The contents of both must be equal", cfg.getClientSecret(), deserializedCfg.getClientSecret());
		Assert.assertEquals("The contents of both must be equal", cfg.getStreamType(), deserializedCfg.getStreamType());
		Assert.assertEquals("The contents of both must be equal", cfg.getStreamQuery(), deserializedCfg.getStreamQuery());
		Assert.assertEquals("The contents of both must be equal", cfg.getStreamVersion(), deserializedCfg.getStreamVersion());
		Assert.assertEquals("The contents of both must be equal", cfg.getSchemaVersion(), deserializedCfg.getSchemaVersion());
		Assert.assertEquals("The size of dispatcher set must be 2", 2, cfg.getDispatchers().size());
		Assert.assertTrue("The dispatcher must be contained", cfg.getDispatchers().contains("test-dispatcher-1"));
		Assert.assertTrue("The dispatcher must be contained", cfg.getDispatchers().contains("test-dispatcher-2"));

		
		
	}
	
}
