package com.mnxfst.stream.listener.webtrends;

import java.io.IOException;
import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import org.eclipse.jetty.websocket.client.ClientUpgradeRequest;
import org.eclipse.jetty.websocket.client.WebSocketClient;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.UntypedActor;
import akka.event.EventStream;

import com.fasterxml.uuid.EthernetAddress;
import com.fasterxml.uuid.Generators;
import com.fasterxml.uuid.impl.TimeBasedGenerator;
import com.mnxfst.stream.directory.ComponentRegistry;
import com.mnxfst.stream.directory.ComponentType;
import com.mnxfst.stream.directory.message.ComponentRegistrationMessage;
import com.mnxfst.stream.directory.message.ComponentRegistrationResponseMessage;
import com.mnxfst.stream.listener.message.SubscribeStreamEventListenerMessage;
import com.mnxfst.stream.message.StreamEventMessage;

@WebSocket
public class WebtrendsStreamListenerActor extends UntypedActor {

	public static final String EVENT_SOURCE_ID = "webtrendsStreamsApi";
	private static final Logger logger = Logger.getLogger(WebtrendsStreamListenerActor.class);
	private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
	
	private final String oAuthToken;
	private final String streamType;
	private final String streamQuery;
	private final String streamVersion;
	private final String streamSchemaVersion;
	private final String eventStreamUrl;
	private final Set<ActorRef> dispatchers = new HashSet<>();
	final ActorRef componentRegistryRef;
		  
	private final CountDownLatch latch = new CountDownLatch(1);
	
	private WebSocketClient webtrendsStreamSocketClient = null;
	private TimeBasedGenerator uuidGenerator = null;

	/**
	 * Initializes the socket using the provided input
	 * @param oAuthToken token received from webtrends on previous authentication
	 * @param streamType type of stream to access
	 * @param streamQuery query that must be applied on the stream to filter messages
	 * @param streamVersion stream version
	 * @param streamSchemaVersion schema version
	 * @param eventStreamUrl url to fetch the webtrends events from
	 * @param componentRegistryRef reference towards {@link ComponentRegistry component registry}
	 */
	public WebtrendsStreamListenerActor(final String oAuthToken, final String streamType, final String streamQuery, final String streamVersion, final String streamSchemaVersion, final String eventStreamUrl, final ActorRef componentRegistryRef) {
		this.oAuthToken = oAuthToken;
		this.streamType = streamType;
		this.streamQuery = streamQuery;
		this.streamSchemaVersion = streamSchemaVersion;
		this.streamVersion = streamVersion;
		this.eventStreamUrl = eventStreamUrl;
		this.componentRegistryRef = componentRegistryRef;
	}
	
	/**
	 * Establishes a connection with the webtrends stream api
	 * @see akka.actor.UntypedActor#preStart()
	 */
	public void preStart() throws Exception {
		
		// initialize the uuid generator which is based on time and ethernet address
		this.uuidGenerator = Generators.timeBasedGenerator(EthernetAddress.fromInterface());
		
		// initialize the webtrends stream socket client and connect the listener
		this.webtrendsStreamSocketClient = new WebSocketClient();
		try {
			this.webtrendsStreamSocketClient.start();
			ClientUpgradeRequest upgradeRequest = new ClientUpgradeRequest();
			this.webtrendsStreamSocketClient.connect(this, new URI(this.eventStreamUrl), upgradeRequest);
			await(5, TimeUnit.SECONDS);
		} catch(Exception e) {
			throw new RuntimeException("Unable to connect to web socket: " + e.getMessage(), e);
		}
		
		this.componentRegistryRef.tell(new ComponentRegistrationMessage(EVENT_SOURCE_ID, ComponentType.STREAM_LISTENER, getSelf()), getSelf());
	}


	/**
	 * Executed after establishing web socket connection with streams api
	 * @param session
	 */
	@OnWebSocketConnect
	public void onConnect(Session session) {
		
		// build SAPI query object
		final StringBuilder sb = new StringBuilder();
		sb.append("{\"access_token\":\"");
	    sb.append(oAuthToken);
	    sb.append("\",\"command\":\"stream\"");
	    sb.append(",\"stream_type\":\"");
	    sb.append(streamType);
	    sb.append("\",\"query\":\"");
	    sb.append(streamQuery);
	    sb.append("\",\"api_version\":\"");
	    sb.append(streamVersion);
	    sb.append("\",\"schema_version\":\"");
	    sb.append(streamSchemaVersion);
	    sb.append("\"}");

	    try {
	    	session.getRemote().sendString(sb.toString());
	    	logger.info("WebTrends Streams API reader connected");
	    } catch(IOException e) {
	    	throw new RuntimeException("Unable to open stream", e);
	    }
	    
    	
	}

	/**
	 * Executed by web socket implementation when receiving a message from the
	 * streams api. The message will be directly forwarded to the current 
	 * {@link ActorSystem actor systems} {@link EventStream event stream} 
	 * @param message
	 */
	@OnWebSocketMessage
	public void onMessage(String message) {
		
		try {
			for(final ActorRef ref : this.dispatchers) {
				ref.tell(new StreamEventMessage(uuidGenerator.generate().toString(), EVENT_SOURCE_ID, sdf.format(new Date()), message), getSender());
			}
		} catch(Exception e) {
			logger.error("Failed to insert webtrends stream event into processing pipeline. Error: " + e.getMessage());
		}
	}

	/**
	 * Executed when closing the web socket connection
	 * @param statusCode
	 * @param reason
	 */
	@OnWebSocketClose		    
	public void onClose(int statusCode, String reason) {
		logger.info("websocket closing[status="+statusCode+", reason="+reason+"]");
	}

	/**
	 * Timeout handler
	 * @param duration
	 * @param unit
	 * @return
	 * @throws InterruptedException
	 */
	public boolean await(int duration, TimeUnit unit) throws InterruptedException {
		return latch.await(duration, unit);
	}
	
	/**
	 * @see akka.actor.UntypedActor#onReceive(java.lang.Object)
	 */
	public void onReceive(Object message) throws Exception {
		
		if(message instanceof SubscribeStreamEventListenerMessage) {
			SubscribeStreamEventListenerMessage msg = (SubscribeStreamEventListenerMessage)message;
			if(msg.getSubscriberRef() != null) {
				this.dispatchers.add(msg.getSubscriberRef());
				context().system().log().info("New stream subscriber: " + msg.getSubscriberRef());
			}
		} else if(message instanceof ComponentRegistrationResponseMessage) {
			ComponentRegistrationResponseMessage msg = (ComponentRegistrationResponseMessage)message;
			context().system().log().info("webtrends listener registration[id="+msg.getId()+", type="+msg.getType()+", state="+msg.getState()+"]");
		} else {
			unhandled(message);
		}
		
		// TODO suspend message
		// TODO disconnect message
		// TODO reconnect message
	}

}
