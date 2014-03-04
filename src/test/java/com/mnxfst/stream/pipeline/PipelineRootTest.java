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

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.testkit.JavaTestKit;

import com.mnxfst.stream.pipeline.config.PipelineElementConfiguration;
import com.mnxfst.stream.pipeline.config.PipelineRootConfiguration;

/**
 * Test case for {@link PipelineRoot}
 * @author mnxfst
 * @since 04.03.2014
 *
 */
public class PipelineRootTest {

protected static ActorSystem system;
	
	@BeforeClass
	public static void initialize() {
	    system = ActorSystem.create();
	}
	  
	@AfterClass
	public static void shutdown() {
		JavaTestKit.shutdownActorSystem(system);
		system = null;
	}
	
	/**
	 * Test case for {@link PipelineRoot} being initialized with null
	 */
	@Test
	public void testSetupWithNullElementSettings() {
		new JavaTestKit(system) {{
			
			PipelineRootConfiguration rootCfg = null;
			system.actorOf(Props.create(PipelineRoot.class, rootCfg));
			
			// TODO how to handle this - need responses?
		}};
	}
	
	/**
	 * Test case for {@link PipelineRoot} being initialized with settings referencing invalid classes
	 */
	@Test
	public void testSetupWithInvalidElementClass() {
		new JavaTestKit(system) {{
			
			PipelineRootConfiguration rootCfg = new PipelineRootConfiguration("test-pipeline", "description");
			rootCfg.addElementConfiguration(new PipelineElementConfiguration("element-id", "description", "elementClass", 1));
			system.actorOf(Props.create(PipelineRoot.class, rootCfg));

			// TODO how to handle this - need responses?
		}};
	}
	
	
	
}
