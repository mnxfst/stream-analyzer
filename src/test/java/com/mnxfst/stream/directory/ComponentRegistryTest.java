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
package com.mnxfst.stream.directory;

import java.util.concurrent.TimeUnit;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import scala.concurrent.duration.FiniteDuration;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.testkit.JavaTestKit;
import akka.testkit.TestActorRef;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mnxfst.stream.config.StreamAnalyzerConfiguration;
import com.mnxfst.stream.directory.message.ComponentLookupMessage;
import com.mnxfst.stream.directory.message.ComponentLookupResponseMessage;
import com.mnxfst.stream.directory.message.ComponentRegistrationMessage;
import com.mnxfst.stream.directory.message.ComponentRegistrationResponseMessage;
import com.mnxfst.stream.dispatcher.config.DispatchPolicyConfiguration;
import com.mnxfst.stream.dispatcher.config.StreamEventMessageDispatcherConfiguration;
import com.mnxfst.stream.dispatcher.policy.BroadcastDispatchPolicy;
import com.mnxfst.stream.listener.StreamEventListenerConfiguration;
import com.mnxfst.stream.listener.webtrends.WebtrendsStreamListenerActor;
import com.mnxfst.stream.pipeline.config.PipelineElementConfiguration;
import com.mnxfst.stream.pipeline.config.PipelineRootConfiguration;
import com.mnxfst.stream.pipeline.element.es.ElasticSearchWriterPipelineElement;
import com.mnxfst.stream.pipeline.element.script.ScriptEvaluatorPipelineElement;

/**
 * Test case for {@link ComponentRegistry component registry}
 * @author mnxfst
 * @since 03.03.2014
 *
 */
public class ComponentRegistryTest {
	
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
	 * Test case for {@link ComponentRegistry} being provided a valid {@link ComponentRegistrationMessage}
	 */
	@Test
	public void testRegisterComponentWithValidMessage() {
				
		TestActorRef<ComponentRegistry> registryRef = TestActorRef.create(system, Props.create(ComponentRegistry.class), "testRegisterComponentWithValidMessage");
		ComponentRegistrationResponseMessage response = registryRef.underlyingActor().registerComponent(new ComponentRegistrationMessage("testRegisterComponentWithValidMessage", ComponentType.PIPELINE_MASTER, registryRef));
		Assert.assertNotNull("The response must not be null", response);
		Assert.assertEquals("The identifier must be 'testRegisterComponentWithValidMessage'", "testRegisterComponentWithValidMessage", response.getId());
		Assert.assertEquals("The state must be 0", 0, response.getState());
		Assert.assertEquals("The type must be " + ComponentType.PIPELINE_MASTER, ComponentType.PIPELINE_MASTER, response.getType());

		ComponentLookupMessage lookupMessage = new ComponentLookupMessage(ComponentType.PIPELINE_MASTER);
		lookupMessage.addComponentId("testRegisterComponentWithValidMessage");
		ComponentLookupResponseMessage lookupResponse = registryRef.underlyingActor().lookupComponentReferences(lookupMessage);
		Assert.assertNotNull("The response must not be null", lookupResponse);
		Assert.assertEquals("The type must be equal", ComponentType.PIPELINE_MASTER, lookupResponse.getType());
		Assert.assertEquals("The response must hold one reference", 1, lookupMessage.getComponentIds().size());
		Assert.assertEquals("The ref must be equal", registryRef, lookupResponse.getComponentReferences().get("testRegisterComponentWithValidMessage"));
	}
	
	/**
	 * Test case for {@link ComponentRegistry} being provided null 
	 */
	@Test
	public void testRegisterComponentWithNull() {
				
		TestActorRef<ComponentRegistry> registryRef = TestActorRef.create(system, Props.create(ComponentRegistry.class), "testRegisterComponentWithValidNull");
		ComponentRegistrationResponseMessage response = registryRef.underlyingActor().registerComponent(null);		
		Assert.assertNotNull("The response must not be null", response);
		Assert.assertNull("The identifier must be null", response.getId());
		Assert.assertEquals("The state must be " + ComponentRegistrationResponseMessage.REGISTRATION_FAILED_EMPTY_MESSAGE, ComponentRegistrationResponseMessage.REGISTRATION_FAILED_EMPTY_MESSAGE, response.getState());
		Assert.assertNull("The type must be null", response.getType());

	}
	
	/**
	 * Test case for {@link ComponentRegistry} being provided null as identifier 
	 */
	@Test
	public void testRegisterComponentWithNullId() {
				
		TestActorRef<ComponentRegistry> registryRef = TestActorRef.create(system, Props.create(ComponentRegistry.class), "testRegisterComponentWithNullId");
		ComponentRegistrationResponseMessage response = registryRef.underlyingActor().registerComponent(new ComponentRegistrationMessage(null, ComponentType.PIPELINE_MASTER, registryRef));		
		Assert.assertNotNull("The response must not be null", response);
		Assert.assertNull("The identifier must be null", response.getId());
		Assert.assertEquals("The state must be " + ComponentRegistrationResponseMessage.REGISTRATION_FAILED_MISSING_ID, ComponentRegistrationResponseMessage.REGISTRATION_FAILED_MISSING_ID, response.getState());
		Assert.assertEquals("The type must be " + ComponentType.PIPELINE_MASTER, ComponentType.PIPELINE_MASTER, response.getType());

	}
	
	/**
	 * Test case for {@link ComponentRegistry} being provided null as type
	 */
	@Test
	public void testRegisterComponentWithNullType() {
				
		TestActorRef<ComponentRegistry> registryRef = TestActorRef.create(system, Props.create(ComponentRegistry.class), "testRegisterComponentWithNullType");
		ComponentRegistrationResponseMessage response = registryRef.underlyingActor().registerComponent(new ComponentRegistrationMessage("testRegisterComponentWithNullType", null, registryRef));		
		Assert.assertNotNull("The response must not be null", response);
		Assert.assertEquals("The identifier must be equal", "testRegisterComponentWithNullType", response.getId());
		Assert.assertEquals("The state must be " + ComponentRegistrationResponseMessage.REGISTRATION_FAILED_MISSING_TYPE, ComponentRegistrationResponseMessage.REGISTRATION_FAILED_MISSING_TYPE, response.getState());
		Assert.assertNull("The type must be null" , response.getType());

		ComponentLookupMessage lookupMessage = new ComponentLookupMessage(ComponentType.PIPELINE_MASTER);
		lookupMessage.addComponentId("testRegisterComponentWithNullType");
		ComponentLookupResponseMessage lookupResponse = registryRef.underlyingActor().lookupComponentReferences(lookupMessage);
		Assert.assertNotNull("The response must not be null", lookupResponse);
		Assert.assertEquals("The type must be equal", ComponentType.PIPELINE_MASTER, lookupResponse.getType());
		Assert.assertEquals("The response must hold one reference", 1, lookupMessage.getComponentIds().size());
		Assert.assertNull("The ref must be null", lookupResponse.getComponentReferences().get("testRegisterComponentWithNullType"));
	}
	
	/**
	 * Test case for {@link ComponentRegistry} being provided null as reference
	 */
	@Test
	public void testRegisterComponentWithNullRef() {
				
		TestActorRef<ComponentRegistry> registryRef = TestActorRef.create(system, Props.create(ComponentRegistry.class), "testRegisterComponentWithNullRef");
		ComponentRegistrationResponseMessage response = registryRef.underlyingActor().registerComponent(new ComponentRegistrationMessage("testRegisterComponentWithNullRef", ComponentType.PIPELINE_MASTER, null));		
		Assert.assertNotNull("The response must not be null", response);
		Assert.assertEquals("The identifier must be equal", "testRegisterComponentWithNullRef", response.getId());
		Assert.assertEquals("The state must be " + ComponentRegistrationResponseMessage.REGISTRATION_FAILED_MISSING_REFERENCE, ComponentRegistrationResponseMessage.REGISTRATION_FAILED_MISSING_REFERENCE, response.getState());
		Assert.assertEquals("The type must be " + ComponentType.PIPELINE_MASTER, ComponentType.PIPELINE_MASTER, response.getType());

		ComponentLookupMessage lookupMessage = new ComponentLookupMessage(ComponentType.PIPELINE_MASTER);
		lookupMessage.addComponentId("testRegisterComponentWithNullRef");
		ComponentLookupResponseMessage lookupResponse = registryRef.underlyingActor().lookupComponentReferences(lookupMessage);
		Assert.assertNotNull("The response must not be null", lookupResponse);
		Assert.assertEquals("The type must be equal", ComponentType.PIPELINE_MASTER, lookupResponse.getType());
		Assert.assertEquals("The response must hold one reference", 1, lookupMessage.getComponentIds().size());
		Assert.assertNull("The ref must be null", lookupResponse.getComponentReferences().get("testRegisterComponentWithNullRef"));
	}
	
	
	/**
	 * Test case for {@link ComponentRegistry} being provided a valid {@link ComponentRegistrationMessage} via {@link ComponentRegistry#onReceive(Object)}
	 */
	@Test
	public void testRegisterComponentWithValidMessageViaOnReceive() {
				
		new JavaTestKit(system) { 
			{
				final Props props = Props.create(ComponentRegistry.class);
				final ActorRef registryRef = system.actorOf(props, "testRegisterComponentWithValidMessageViaOnReceive");				
				ComponentRegistrationMessage msg = new ComponentRegistrationMessage("testRegisterComponentWithValidMessage", ComponentType.PIPELINE_MASTER, registryRef);
				registryRef.tell(new ComponentRegistrationMessage("testRegisterComponentWithValidMessage", ComponentType.PIPELINE_MASTER, registryRef), getRef());
				expectMsgAllOf(FiniteDuration.apply(500, TimeUnit.MILLISECONDS), new ComponentRegistrationResponseMessage(msg.getId(), msg.getType(), ComponentRegistrationResponseMessage.REGISTRATION_OK));
				
				ComponentLookupMessage lookupMsg = new ComponentLookupMessage(ComponentType.PIPELINE_MASTER);
				lookupMsg.addComponentId(msg.getId());
				registryRef.tell(lookupMsg, getRef());
				ComponentLookupResponseMessage expectedResponse = new ComponentLookupResponseMessage(ComponentType.PIPELINE_MASTER);
				expectedResponse.addComponentReference(msg.getId(), registryRef);
				expectMsgAllOf(FiniteDuration.apply(500,  TimeUnit.MILLISECONDS), expectedResponse);
			}
			
		};
	}
	
	@Test
	public void test() throws Exception {
		
		///////////////////// LISTENER 
		StreamEventListenerConfiguration streamListenerCfg = new StreamEventListenerConfiguration(WebtrendsStreamListenerActor.class.getName(), "wt-listener-1", "wt-listener-1", "wt-listener-1", "0.1");		
		streamListenerCfg.getSettings().put(WebtrendsStreamListenerActor.WT_CONFIG_AUTH_AUDIENCE, "auth-audience");
		streamListenerCfg.getSettings().put(WebtrendsStreamListenerActor.WT_CONFIG_AUTH_SCOPE, "auth-scope");
		streamListenerCfg.getSettings().put(WebtrendsStreamListenerActor.WT_CONFIG_AUTH_URL, "auth-url");
		streamListenerCfg.getSettings().put(WebtrendsStreamListenerActor.WT_CONFIG_EVENT_STREAM_URL, "stream-url");
		streamListenerCfg.getSettings().put(WebtrendsStreamListenerActor.WT_CONFIG_CLIENT_ID, "client-id");
		streamListenerCfg.getSettings().put(WebtrendsStreamListenerActor.WT_CONFIG_CLIENT_SECRET, "client-secret");
		streamListenerCfg.getSettings().put(WebtrendsStreamListenerActor.WT_CONFIG_STREAM_TYPE, "stream-type");
		streamListenerCfg.getSettings().put(WebtrendsStreamListenerActor.WT_CONFIG_STREAM_QUERY, "stream-query");
		streamListenerCfg.getSettings().put(WebtrendsStreamListenerActor.WT_CONFIG_STREAM_VERSION, "stream-version");
		streamListenerCfg.getSettings().put(WebtrendsStreamListenerActor.WT_CONFIG_SCHEMA_VERSION, "schema-version");
		streamListenerCfg.addDispatcher("root-disp");
		
		///////////////////// PIPELINE 
		PipelineElementConfiguration esElementCfg = new PipelineElementConfiguration("wt-pipe", "es-writer", "Elastic Search Writer", ElasticSearchWriterPipelineElement.class.getName(), 1);
		esElementCfg.addSetting(ElasticSearchWriterPipelineElement.ES_CLUSTER_NAME, "tracker");
		esElementCfg.addSetting(ElasticSearchWriterPipelineElement.ES_DOCUMENT_TYPE, "error");
		esElementCfg.addSetting(ElasticSearchWriterPipelineElement.ES_HOST_PREFIX + "0", "localhost");
		esElementCfg.addSetting(ElasticSearchWriterPipelineElement.ES_PORT_PREFIX + "0", "9300");
		esElementCfg.addSetting(ElasticSearchWriterPipelineElement.ES_WRITE_EVENT_ONLY, "true");
		
		PipelineElementConfiguration scriptElementCfg = new PipelineElementConfiguration("wt-pipe", "script-eval", "Script evaluator", ScriptEvaluatorPipelineElement.class.getName(), 1);
		scriptElementCfg.addSetting(ScriptEvaluatorPipelineElement.CONFIG_SCRIPT_ENGINE_NAME, "JavaScript");
		scriptElementCfg.addSetting(ScriptEvaluatorPipelineElement.CONFIG_SCRIPT_INIT_CODE_PREFIX + "0", "spahql");
		scriptElementCfg.addSetting(ScriptEvaluatorPipelineElement.CONFIG_SCRIPT_EVAL_CODE, "eval-script");
		scriptElementCfg.addSetting(ScriptEvaluatorPipelineElement.CONFIG_SCRIPT_INPUT_VARIABLE, "eventContent");
		scriptElementCfg.addSetting(ScriptEvaluatorPipelineElement.CONFIG_SCRIPT_OUTPUT_NEXT_ELEMENT_VARIABLE, "nextElement");
		scriptElementCfg.addSetting(ScriptEvaluatorPipelineElement.CONFIG_SCRIPT_DEFAULT_DESTINATION_ELEMENT_ID, "es-writer");
		
		PipelineRootConfiguration pipelineCfg = new PipelineRootConfiguration("wt-pipe", "webtrends event analyzer pipeline", scriptElementCfg.getElementId());
		pipelineCfg.addElementConfiguration(esElementCfg);
		pipelineCfg.addElementConfiguration(scriptElementCfg);

		///////////////////// DISPATCHER
		DispatchPolicyConfiguration policyCfg = new DispatchPolicyConfiguration("broadcast-dispatch-policy", BroadcastDispatchPolicy.class.getName());
		policyCfg.addSetting(BroadcastDispatchPolicy.BROADCAST_DESTINATION_PREFIX + "0", "wt-pipe");
		StreamEventMessageDispatcherConfiguration dispatcherConfiguration = new StreamEventMessageDispatcherConfiguration("root-disp", "disp-1", "dispatcher #1", policyCfg);
		
		///////////////////// STREAM ANALYZER CONFIG  
		StreamAnalyzerConfiguration root = new StreamAnalyzerConfiguration();
		root.addListener(streamListenerCfg);
		root.addDispatcher(dispatcherConfiguration);
		root.addPipeline(pipelineCfg);
		
		ObjectMapper m = new ObjectMapper();
		System.out.println(m.writeValueAsString(root));
		
		
	}
	
}
