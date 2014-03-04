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
package com.mnxfst.stream.dispatcher;

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
import akka.testkit.TestActorRef;

import com.mnxfst.stream.directory.ComponentRegistry;
import com.mnxfst.stream.directory.ComponentType;
import com.mnxfst.stream.directory.message.ComponentLookupMessage;
import com.mnxfst.stream.directory.message.ComponentRegistrationMessage;
import com.mnxfst.stream.dispatcher.config.DispatchPolicyConfiguration;
import com.mnxfst.stream.dispatcher.config.StreamEventMessageDispatcherConfiguraton;
import com.mnxfst.stream.message.StreamEventMessage;

/**
 * Test case for {@link StreamEventMessageDispatcher}
 * @author mnxfst
 * @since 28.02.2014
 *
 */
public class StreamEventMessageDispatcherTest {
	
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
	 * Test case for {@link StreamEventMessageDispatcher#getDispatchPolicy(com.mnxfst.stream.dispatcher.config.DispatchPolicyConfiguration)}
	 * being provided valid input
	 */
	@Test
	public void testGetDispatchPolicyWithValidInput() throws Exception {

		DispatchPolicyConfiguration dispatchPolicyCfg = new DispatchPolicyConfiguration("policy-1", TestDispatcher.class.getName());
		StreamEventMessageDispatcherConfiguraton cfg = new StreamEventMessageDispatcherConfiguraton("disp-id-1", "disp-1", "test description", dispatchPolicyCfg);		

		TestActorRef<ComponentRegistry> componentRegistryRef = TestActorRef.create(system, Props.create(ComponentRegistry.class), "testGetDispatchPolicyWithValidInput");
		
		try {
			TestActorRef<StreamEventMessageDispatcher> testActorRef = TestActorRef.create(system, Props.create(StreamEventMessageDispatcher.class, cfg, componentRegistryRef));
			Assert.assertEquals("Name must be equal", dispatchPolicyCfg.getName(), testActorRef.underlyingActor().getDispatchPolicy().getName());
		} catch(Exception e) {
			Assert.fail("Failed to evaluate method. Error: " + e.getMessage());
		} finally {
			system.shutdown();
		}
	}
	
	/**
	 * Test case for {@link StreamEventMessageDispatcher} being initialized and sending component registration messages 	 
	 */
	@Test
	public void testDispatcherInitializationValidateComponentRegistration() {
		
		final DispatchPolicyConfiguration dispatchPolicyCfg = new DispatchPolicyConfiguration("policy-1", TestDispatcher.class.getName());
		final StreamEventMessageDispatcherConfiguraton cfg = new StreamEventMessageDispatcherConfiguraton("testDispatcherInitializationValidateComponentRegistration", "disp-1", "test description", dispatchPolicyCfg);		

		new JavaTestKit(system) { {
			
			final ActorRef dispatcherRef = system.actorOf(Props.create(StreamEventMessageDispatcher.class, cfg, getRef()), "testDispatcherInitializationValidateComponentRegistration");
			ComponentRegistrationMessage registrationRequest = (ComponentRegistrationMessage)receiveOne(Duration.create(500, TimeUnit.MILLISECONDS));
			Assert.assertNotNull("The registration request must not be null", registrationRequest);
			Assert.assertEquals("The id must be equal", cfg.getId(), registrationRequest.getId());
			Assert.assertEquals("The type must be equal", ComponentType.DISPATCHER, registrationRequest.getType());
			Assert.assertEquals("The reference must be equal", dispatcherRef, registrationRequest.getReference());
			
		}};
	}
	
	/**
	 * Test case for {@link StreamEventMessageDispatcher} being initialized and sending component lookup messages 	 
	 */
	@Test
	public void testDispatcherInitializationValidateComponentRegistrationWithLookupMessages() {
		
		final DispatchPolicyConfiguration dispatchPolicyCfg = new DispatchPolicyConfiguration("policy-1", TestDispatcher.class.getName());
		final StreamEventMessageDispatcherConfiguraton cfg = new StreamEventMessageDispatcherConfiguraton("testDispatcherInitializationValidateComponentRegistrationWithLookupMessages", "disp-1", "test description", dispatchPolicyCfg);
		cfg.addDestination("test-destination-1");
		cfg.addDestination("test-destination-2");

		new JavaTestKit(system) { {
			
			final ActorRef dispatcherRef = system.actorOf(Props.create(StreamEventMessageDispatcher.class, cfg, getRef()), "testDispatcherInitializationValidateComponentRegistrationWithLookupMessages");
			ComponentRegistrationMessage registrationRequest = (ComponentRegistrationMessage)receiveOne(Duration.create(500, TimeUnit.MILLISECONDS));
			Assert.assertNotNull("The registration request must not be null", registrationRequest);
			Assert.assertEquals("The id must be equal", cfg.getId(), registrationRequest.getId());
			Assert.assertEquals("The type must be equal", ComponentType.DISPATCHER, registrationRequest.getType());
			Assert.assertEquals("The reference must be equal", dispatcherRef, registrationRequest.getReference());
 
			ComponentLookupMessage lookupMessage = (ComponentLookupMessage)receiveOne(Duration.create(500, TimeUnit.MILLISECONDS)); 
			Assert.assertNotNull("The component lookup request must not be null", lookupMessage);
			Assert.assertEquals("The type must be equal", ComponentType.PIPELINE_ROOT, lookupMessage.getType());
			Assert.assertEquals("The number of requested components must be " + cfg.getDestinations().size(), cfg.getDestinations().size(), lookupMessage.getComponentIds().size());
			Assert.assertTrue("The component must be contained", lookupMessage.getComponentIds().contains("test-destination-1"));
			Assert.assertTrue("The component must be contained", lookupMessage.getComponentIds().contains("test-destination-2"));
			
		}};
	}
	
	/**
	 * Test case for {@link StreamEventMessageDispatcher} being initialized and receiving {@link StreamEventMessage stream event message} 	 
	 */
	@Test
	public void testDispatcherWithValidSetupAndStreamEventMessage() {
		
		final DispatchPolicyConfiguration dispatchPolicyCfg = new DispatchPolicyConfiguration("policy-1", TestDispatcher.class.getName());
		dispatchPolicyCfg.addSetting("test-destination-1", null);
		
		final StreamEventMessageDispatcherConfiguraton cfg = new StreamEventMessageDispatcherConfiguraton("testDispatcherWithValidSetupAndStreamEventMessage", "disp-1", "test description", dispatchPolicyCfg);
		cfg.addDestination("test-destination-1");
		cfg.addDestination("test-destination-2");
		cfg.addDestination("test-destination-3");

		new JavaTestKit(system) { {
			
			final ActorRef dispatcherRef = system.actorOf(Props.create(StreamEventMessageDispatcher.class, cfg, getRef()), "testDispatcherWithValidSetupAndStreamEventMessage");
			ComponentRegistrationMessage registrationRequest = (ComponentRegistrationMessage)receiveOne(Duration.create(500, TimeUnit.MILLISECONDS));
			Assert.assertNotNull("The registration request must not be null", registrationRequest);
			Assert.assertEquals("The id must be equal", cfg.getId(), registrationRequest.getId());
			Assert.assertEquals("The type must be equal", ComponentType.DISPATCHER, registrationRequest.getType());
			Assert.assertEquals("The reference must be equal", dispatcherRef, registrationRequest.getReference());
 
			ComponentLookupMessage lookupMessage = (ComponentLookupMessage)receiveOne(Duration.create(500, TimeUnit.MILLISECONDS)); 
			Assert.assertNotNull("The component lookup request must not be null", lookupMessage);
			Assert.assertEquals("The type must be equal", ComponentType.PIPELINE_ROOT, lookupMessage.getType());
			Assert.assertEquals("The number of requested components must be " + cfg.getDestinations().size(), cfg.getDestinations().size(), lookupMessage.getComponentIds().size());
			Assert.assertTrue("The component must be contained", lookupMessage.getComponentIds().contains("test-destination-1"));
			Assert.assertTrue("The component must be contained", lookupMessage.getComponentIds().contains("test-destination-2"));
			
			dispatcherRef.tell(new StreamEventMessage("test-id", "test-origin", "2014-03-03", "TestEvent"), getRef());
			
			lookupMessage = (ComponentLookupMessage)receiveOne(Duration.apply(500, TimeUnit.MILLISECONDS));
			Assert.assertNotNull("The component lookup request must not be null", lookupMessage);
			Assert.assertEquals("The type must be equal", ComponentType.PIPELINE_ROOT, lookupMessage.getType());
			Assert.assertEquals("The number of requested components must be " + dispatchPolicyCfg.getSettings().size(), dispatchPolicyCfg.getSettings().size(), lookupMessage.getComponentIds().size());
			Assert.assertTrue("The component must be contained", lookupMessage.getComponentIds().contains("test-destination-1"));
		}};
	}

}
