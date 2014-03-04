/**
 * Copyright 2014 Christian Kreutzfeldt
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
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
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;

import akka.actor.ActorRef;

/**
 * Web socket implementation used for connecting with webtrends streams api
 * @author mnxfst
 * @since 22.01.2014
 * TODO testing
 */
@WebSocket
public class WebtrendsStreamSocket {

	private static final Logger logger = Logger.getLogger(WebtrendsStreamSocket.class);
	
	public static final String EVENT_SOURCE_ID = "webtrendsStreamsApi"; 
	
	private final String oAuthToken;
	private final String streamType;
	private final String streamQuery;
	private final String streamVersion;
	private final String streamSchemaVersion;
	private final Set<ActorRef> dispatcherRefs = new HashSet<>();
	
	private final CountDownLatch latch = new CountDownLatch(1);

	/**
	 * Initializes the socket using the provided input
	 * @param oAuthToken token received from webtrends on previous authentication
	 * @param streamType type of stream to access
	 * @param streamQuery query that must be applied on the stream to filter messages
	 * @param streamVersion stream version
	 * @param streamSchemaVersion schema version
	 * @param dispatcherRefs dispatcher references
	 */
	public WebtrendsStreamSocket(final String oAuthToken, final String streamType, final String streamQuery, final String streamVersion, final String streamSchemaVersion, final Set<ActorRef> dispatcherRefs) {
		this.oAuthToken = oAuthToken;
		this.streamType = streamType;
		this.streamQuery = streamQuery;
		this.streamSchemaVersion = streamSchemaVersion;
		this.streamVersion = streamVersion;
		this.dispatcherRefs.addAll(dispatcherRefs);
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
	 * streams api
	 * @param message
	 */
	@OnWebSocketMessage
	public void onMessage(String message) {		
		if(StringUtils.isNotBlank(message)) {
			for(final ActorRef dispRef : this.dispatcherRefs)
				dispRef.tell(message, null);
		}	
		// TODO broadcast in parallel?
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
		
}
