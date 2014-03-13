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

import java.text.SimpleDateFormat;
import java.util.Date;
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

import com.mnxfst.stream.message.StreamEventMessage;
import com.mnxfst.stream.pipeline.config.PipelineElementConfiguration;
import com.mnxfst.stream.pipeline.config.PipelineRootConfiguration;
import com.mnxfst.stream.pipeline.message.PipelineElementReferenceUpdateMessage;

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
			
			PipelineRootConfiguration rootCfg = new PipelineRootConfiguration("test-pipeline", "description", "element-id");
			rootCfg.addElementConfiguration(new PipelineElementConfiguration(rootCfg.getPipelineId(), "element-id", "description", "elementClass", 1, ""));
			system.actorOf(Props.create(PipelineRoot.class, rootCfg));
			// TODO how to handle this - need responses?
		}};
	}
	
	/**
	 * Test case for {@link PipelineRoot} being initialized with settings referencing valid classes, expecting notifications
	 */
	@Test
	public void testSetupWithValidElementClassExpectingReferenceUpdates() {
		new JavaTestKit(system) {{
			
			PipelineRootConfiguration rootCfg = new PipelineRootConfiguration("test-pipeline", "description", "element-id");
			PipelineElementConfiguration cfg = new PipelineElementConfiguration(rootCfg.getPipelineId(), "element-id", "description", TestPipelineElement.class.getName(), 1, "");
			cfg.addSetting("logRefPath", getRef().path().toString());
			rootCfg.addElementConfiguration(cfg);
			final ActorRef pipelineRootRef = system.actorOf(Props.create(PipelineRoot.class, rootCfg));
			PipelineElementReferenceUpdateMessage updateMessage = (PipelineElementReferenceUpdateMessage)receiveOne(Duration.create(500, TimeUnit.MILLISECONDS));
			Assert.assertNotEquals("The message must not be null", updateMessage);
			Assert.assertEquals("The pipeline ids must be equal", rootCfg.getPipelineId(), updateMessage.getPipelineId());
			Assert.assertEquals("The elements map must have 1 entry", 1, updateMessage.getElementReferences().size());
			Assert.assertTrue("The entry must exit", updateMessage.getElementReferences().containsKey(cfg.getElementId()));
			
		}};
	}
	
	/**
	 * Test case for {@link PipelineRoot} being initialized and handed over a message
	 */
	@Test
	public void testSetupWithValidSettingsAndStreamEventMessage() throws Exception {
		new JavaTestKit(system) {{
			
//			PipelineRootConfiguration rootCfg = new PipelineRootConfiguration("test-pipeline", "description", "element-id");
//			PipelineElementConfiguration cfg = new PipelineElementConfiguration(rootCfg.getPipelineId(), "element-id", "description", TestPipelineElement.class.getName(), 1);
//			cfg.addSetting("logRefPath", getRef().path().name());
//			cfg.addSetting("receiverId", "element-id");
//			rootCfg.addElementConfiguration(cfg);
//			final ActorRef pipelineRootRef = system.actorOf(Props.create(PipelineRoot.class, rootCfg));
//			PipelineElementReferenceUpdateMessage updateMessage = (PipelineElementReferenceUpdateMessage)receiveOne(Duration.create(500, TimeUnit.MILLISECONDS));
//			Assert.assertNotEquals("The message must not be null", updateMessage);
//			
//			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
//			String formattedDate = format.format(new Date());
//			StreamEventMessage msg = new StreamEventMessage("message-id", "message-origin", formattedDate, "message-event");
//			pipelineRootRef.tell(msg, getRef());
//						
//			Object message = receiveOne(Duration.create(500, TimeUnit.MILLISECONDS));
//			StreamEventMessage eventMsg = (StreamEventMessage)message;
//			
//			Assert.assertNotNull("The message must not be null", eventMsg);
//			Assert.assertEquals("Values must be equal", msg.getEvent(), eventMsg.getEvent());
//			Assert.assertEquals("Values must be equal", msg.getIdentifier(), eventMsg.getIdentifier());
//			Assert.assertEquals("Values must be equal", msg.getOrigin(), eventMsg.getOrigin());
//			Assert.assertEquals("Values must be equal", msg.getTimestamp(), eventMsg.getTimestamp());
			
		}};
	}
	
	
	
}
