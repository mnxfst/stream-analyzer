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

import java.util.concurrent.TimeUnit;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import scala.concurrent.duration.Duration;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.testkit.JavaTestKit;

import com.mnxfst.stream.directory.ComponentType;
import com.mnxfst.stream.directory.message.ComponentDeregistrationMessage;
import com.mnxfst.stream.directory.message.ComponentRegistrationMessage;
import com.mnxfst.stream.pipeline.config.PipelineElementConfiguration;
import com.mnxfst.stream.pipeline.config.PipelineRootConfiguration;
import com.mnxfst.stream.pipeline.message.PipelineSetupMessage;
import com.mnxfst.stream.pipeline.message.PipelineSetupResponseMessage;
import com.mnxfst.stream.pipeline.message.PipelineShutdownMessage;

/**
 * Test case for {@link PipelinesMaster}
 * @author mnxfst
 * @since 03.03.2014
 *
 */
public class PipelinesMasterTest {

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
	 * Test case for {@link PipelinesMaster} being provided a "null" configuration
	 */
	@Test
	public void testRegisterPipelineWithNullConfig() {
		new JavaTestKit(system) { {

			final ActorRef pipelineMaster = system.actorOf(Props.create(PipelinesMaster.class, getRef()));
			pipelineMaster.tell(new PipelineSetupMessage(null), getRef());
			
			PipelineSetupResponseMessage response = (PipelineSetupResponseMessage)receiveOne(Duration.create(500, TimeUnit.MILLISECONDS));
			Assert.assertNotNull("The response must not be null", response);
			Assert.assertEquals("The state must be equal", PipelineSetupResponseMessage.SETUP_RESPONSE_MISSING_PIPELINE_ELEMENT_CONFIGURATION, response.getResponseCode());
		}};
	}

	/**
	 * Test case for {@link PipelinesMaster} being provided a configuration missing the pipeline id
	 */
	@Test
	public void testRegisterPipelineWithNullPipelineId() {
		new JavaTestKit(system) { {

			final ActorRef pipelineMaster = system.actorOf(Props.create(PipelinesMaster.class, getRef()));
			PipelineRootConfiguration cfg = new PipelineRootConfiguration(null, "description", "element-id");
			cfg.addElementConfiguration(new PipelineElementConfiguration(cfg.getPipelineId(), "element-id", "description", TestPipelineElement.class.getName(), 5));
			pipelineMaster.tell(new PipelineSetupMessage(cfg), getRef());
			
			PipelineSetupResponseMessage response = (PipelineSetupResponseMessage)receiveOne(Duration.create(500, TimeUnit.MILLISECONDS));
			Assert.assertNotNull("The response must not be null", response);
			Assert.assertEquals("The state must be equal", PipelineSetupResponseMessage.SETUP_RESPONSE_MISSING_PIPELINE_ID, response.getResponseCode());
		}};
	}

	/**
	 * Test case for {@link PipelinesMaster} being provided a configuration missing the pipeline elements config
	 */
	@Test
	public void testRegisterPipelineWithEmptyPipelineElementsConfig() {
		new JavaTestKit(system) { {

			final ActorRef pipelineMaster = system.actorOf(Props.create(PipelinesMaster.class, getRef()));
			PipelineRootConfiguration cfg = new PipelineRootConfiguration("pipeline-1", "description", "element-id");
			pipelineMaster.tell(new PipelineSetupMessage(cfg), getRef());
			
			PipelineSetupResponseMessage response = (PipelineSetupResponseMessage)receiveOne(Duration.create(500, TimeUnit.MILLISECONDS));
			Assert.assertNotNull("The response must not be null", response);
			Assert.assertEquals("The state must be equal", PipelineSetupResponseMessage.SETUP_RESPONSE_MISSING_PIPELINE_ELEMENT_CONFIGURATION, response.getResponseCode());
		}};
	}

	/**
	 * Test case for {@link PipelinesMaster} being provided a configuration missing a pipeline elements id
	 */
	@Test
	public void testRegisterPipelineWithEmptyPipelineElementsId() {
		new JavaTestKit(system) { {

			final ActorRef pipelineMaster = system.actorOf(Props.create(PipelinesMaster.class, getRef()));
			PipelineRootConfiguration cfg = new PipelineRootConfiguration("pipeline-1", "description", "element-id");
			cfg.addElementConfiguration(new PipelineElementConfiguration(cfg.getPipelineId(), null, "desc-1", "class", 5));
			pipelineMaster.tell(new PipelineSetupMessage(cfg), getRef());
			
			PipelineSetupResponseMessage response = (PipelineSetupResponseMessage)receiveOne(Duration.create(500, TimeUnit.MILLISECONDS));
			Assert.assertNotNull("The response must not be null", response);
			Assert.assertEquals("The state must be equal", PipelineSetupResponseMessage.SETUP_RESPONSE_MISSING_PIPELINE_ELEMENT_ID, response.getResponseCode());
		}};
	}

	/**
	 * Test case for {@link PipelinesMaster} being provided a configuration missing a pipeline elements class
	 */
	@Test
	public void testRegisterPipelineWithEmptyPipelineElementsClass() {
		new JavaTestKit(system) { {

			final ActorRef pipelineMaster = system.actorOf(Props.create(PipelinesMaster.class, getRef()));
			PipelineRootConfiguration cfg = new PipelineRootConfiguration("pipeline-1", "description", "id");
			cfg.addElementConfiguration(new PipelineElementConfiguration(cfg.getPipelineId(), "id", "desc-1", null, 5));
			pipelineMaster.tell(new PipelineSetupMessage(cfg), getRef());
			
			PipelineSetupResponseMessage response = (PipelineSetupResponseMessage)receiveOne(Duration.create(500, TimeUnit.MILLISECONDS));
			Assert.assertNotNull("The response must not be null", response);
			Assert.assertEquals("The state must be equal", PipelineSetupResponseMessage.SETUP_RESPONSE_MISSING_PIPELINE_ELEMENT_CLASS, response.getResponseCode());
		}};
	}

	/**
	 * Test case for {@link PipelinesMaster} being provided a configuration missing a pipeline elements settings
	 */
	@Test
	public void testRegisterPipelineWithEmptyPipelineElementsSettings() throws Exception {
		new JavaTestKit(system) { {

			final ActorRef pipelineMaster = system.actorOf(Props.create(PipelinesMaster.class, getRef()));
			PipelineRootConfiguration cfg = new PipelineRootConfiguration("pipeline-1", "description", "id");
			cfg.addElementConfiguration(new PipelineElementConfiguration(cfg.getPipelineId(), "id", "desc-1", TestPipelineElement.class.getName(), 5));
			pipelineMaster.tell(new PipelineSetupMessage(cfg), getRef());
			
			ComponentRegistrationMessage pipelineRegisteredMessage = (ComponentRegistrationMessage)receiveOne(Duration.create(500, TimeUnit.MILLISECONDS));
			Assert.assertNotNull("The message must not be null", pipelineRegisteredMessage);
			Assert.assertEquals("The component id must be ", cfg.getPipelineId(), pipelineRegisteredMessage.getId());
			
			pipelineMaster.tell(new PipelineShutdownMessage(cfg.getPipelineId()), getRef());
			
			ComponentDeregistrationMessage pipelineDeregistrationMessage = (ComponentDeregistrationMessage)receiveOne(Duration.create(500, TimeUnit.MILLISECONDS));
			Assert.assertNotNull("The message must not be null", pipelineDeregistrationMessage);
			Assert.assertEquals("Id must be equal", cfg.getPipelineId(), pipelineDeregistrationMessage.getId());
			Assert.assertEquals("Type must be equal", ComponentType.PIPELINE_ROOT, pipelineDeregistrationMessage.getType());
		}};
	}

	/**
	 * Test case for {@link PipelinesMaster} being provided a configuration missing a pipeline elements settings
	 */
	@Test
	public void testRegisterPipelineWithInvalidPipelineElement() throws Exception {
		new JavaTestKit(system) { {

			final ActorRef pipelineMaster = system.actorOf(Props.create(PipelinesMaster.class, getRef()));
			PipelineRootConfiguration cfg = new PipelineRootConfiguration("pipeline-1", "description", "id");
			PipelineElementConfiguration elementCfg = new PipelineElementConfiguration(cfg.getPipelineId(), "id", "desc-1", "unknown", 5);
			elementCfg.addSetting("test","test");
			cfg.addElementConfiguration(new PipelineElementConfiguration(cfg.getPipelineId(), "id1", "desc-1", TestPipelineElement.class.getName(), 5));
			cfg.addElementConfiguration(elementCfg);
			pipelineMaster.tell(new PipelineSetupMessage(cfg), getRef());
//			Thread.sleep(1000);
//			Assert.assertNotNull("The response must not be null", response);
//			Assert.assertEquals("The state must be equal", PipelineSetupResponseMessage.SETUP_RESPONSE_MISSING_PIPELINE_ELEMENT_SETTINGS, response.getResponseCode());
		}};
	}
	
}
