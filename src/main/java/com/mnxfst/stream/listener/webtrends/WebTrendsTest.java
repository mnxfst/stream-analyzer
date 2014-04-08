package com.mnxfst.stream.listener.webtrends;

import com.mnxfst.stream.directory.ComponentRegistry;
import com.mnxfst.stream.dispatcher.StreamEventMessageDispatcher;
import com.mnxfst.stream.dispatcher.config.DispatchPolicyConfiguration;
import com.mnxfst.stream.dispatcher.config.StreamEventMessageDispatcherConfiguration;
import com.mnxfst.stream.dispatcher.policy.BroadcastDispatchPolicy;
import com.mnxfst.stream.listener.message.SubscribeStreamEventListenerMessage;
import com.mnxfst.stream.pipeline.PipelinesMaster;
import com.mnxfst.stream.pipeline.config.PipelineElementConfiguration;
import com.mnxfst.stream.pipeline.config.PipelineRootConfiguration;
import com.mnxfst.stream.pipeline.element.log.LogWriterPipelineElement;
import com.mnxfst.stream.pipeline.message.PipelineSetupMessage;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;

public class WebTrendsTest {

	public static void main(String[] args) throws Exception {
		
		ActorSystem system = ActorSystem.create("test");

		String streamType = "return_all";
		String streamQuery = "select *";
		String streamVersion = "2.1";
		String streamSchemaVersion = "2.1";
		
		String audience = "auth.webtrends.com";
	    String scope = "sapi.webtrends.com";
	    String authUrl = "https://sauth.webtrends.com/v1/token";
		
		WebtrendsTokenRequest req = new WebtrendsTokenRequest(authUrl, audience, scope, "", "");
		String oAuthToken = req.execute();

		
		// component registry
		final ActorRef componentRegistry = system.actorOf(Props.create(ComponentRegistry.class), ComponentRegistry.COMPONENT_REGISTRY_ID);
		
		final ActorRef webtrendsStreamListenerRef = system.actorOf(Props.create(WebtrendsStreamListenerActor.class, oAuthToken, streamType, streamQuery, streamVersion, streamSchemaVersion, "ws://sapi.webtrends.com/streaming", componentRegistry));
		
		PipelineRootConfiguration prc = new PipelineRootConfiguration("pipe-1", "pipe-1", "log-1");
		prc.addElementConfiguration(new PipelineElementConfiguration("pipe-1", "log-1", "log-1", LogWriterPipelineElement.class.getName(), 1, "default"));
		
		final ActorRef pipeMaster = system.actorOf(Props.create(PipelinesMaster.class, componentRegistry));
		pipeMaster.tell(new PipelineSetupMessage(prc), null);		
		
		DispatchPolicyConfiguration policyCfg = new DispatchPolicyConfiguration("policy-1", BroadcastDispatchPolicy.class.getName());
		policyCfg.addSetting(BroadcastDispatchPolicy.BROADCAST_DESTINATION_PREFIX+"0", "pipe-1");		
		StreamEventMessageDispatcherConfiguration dispCfg = new StreamEventMessageDispatcherConfiguration("disp-1", "disp-1", "disp-1", policyCfg);		
		final ActorRef dispatcherRef = system.actorOf(Props.create(StreamEventMessageDispatcher.class, dispCfg, componentRegistry), dispCfg.getId());
		
		webtrendsStreamListenerRef.tell(new SubscribeStreamEventListenerMessage(dispatcherRef), null);;
		
		
		
		System.in.read();
		system.shutdown();
		
	}
	
}
