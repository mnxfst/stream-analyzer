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

import java.net.URI;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.jetty.websocket.client.ClientUpgradeRequest;
import org.eclipse.jetty.websocket.client.WebSocketClient;

import akka.actor.ActorRef;

import com.mnxfst.stream.listener.StreamEventListener;

/**
 * Implements a reader for fetching contents from {@linkplain http://streams.webtrends.com}
 * @author mnxfst
 * @since 05.02.2014
 *
 */
public class WebTrendsStreamAPIListener extends StreamEventListener {

	private final String clientId;
	private final String clientSecret;
	private final String streamType;
	private final String streamQuery;
	private final String streamVersion;
	private final String streamSchemaVersion;
	
	private String oAuthToken;
	private WebSocketClient webSocketClient;
	private boolean isRunning = false;

	/**
	 * Initialize stream api reader using the provided input
	 * @param configuration
	 * @param dispatcherRef
	 */
	public WebTrendsStreamAPIListener(final WebTrendsStreamListenerConfiguration configuration) {

		super(configuration);

		if(configuration == null)
			throw new RuntimeException("Missing required listener configuraton");
		if(configuration.getDispatchers() == null || configuration.getDispatchers().isEmpty())
			throw new RuntimeException("Missing required dispatchers");
		if(StringUtils.isBlank(configuration.getClientId()))
			throw new RuntimeException("Missing required client identifier");
		if(StringUtils.isBlank(configuration.getClientSecret()))
			throw new RuntimeException("Missing required client secret");
		if(StringUtils.isBlank(configuration.getStreamType()))
			throw new RuntimeException("Missing required stream type");
		if(StringUtils.isBlank(configuration.getStreamVersion()))
			throw new RuntimeException("Missing required stream version");
		if(StringUtils.isBlank(configuration.getStreamSchemaVersion()))
			throw new RuntimeException("Missing required stream schema version");
		if(StringUtils.isBlank(configuration.getStreamQuery()))
			throw new RuntimeException("Missing required stream query");

		this.clientId = configuration.getClientId();
		this.clientSecret = configuration.getClientSecret();
		this.streamType = configuration.getStreamType();
		this.streamVersion = configuration.getStreamVersion();
		this.streamSchemaVersion = configuration.getStreamSchemaVersion();
		this.streamQuery = configuration.getStreamQuery();
	}
	
	/**
	 * @see java.lang.Runnable#run()
	 */
	public void run() {

		try {
			this.oAuthToken = new WebTrendsTokenRequest(this.clientId, this.clientSecret).execute();
		} catch(Exception e) {
			throw new RuntimeException("Failed to request webtrends token. Error: " + e.getMessage(), e);
		}
		
		this.webSocketClient = new WebSocketClient();
		WebTrendsStreamSocket socket = new WebTrendsStreamSocket(this.oAuthToken, this.streamType, this.streamQuery, 
				this.streamVersion, this.streamSchemaVersion);
		for(ActorRef dispatcherReference : this.dispatcherReferences)
			socket.addDispatcherReference(dispatcherReference);
		
		try {
			this.webSocketClient.start();
			ClientUpgradeRequest upgradeRequest = new ClientUpgradeRequest();
			this.webSocketClient.connect(socket, new URI("ws://sapi.webtrends.com/streaming"), upgradeRequest);
			socket.await(5, TimeUnit.SECONDS);
		} catch(Exception e) {
			throw new RuntimeException("Unable to connect to web socket: " + e.getMessage(), e);
		}
		
		isRunning = true;
		
		while(isRunning) {
			// 
		}
		
		try {
			this.webSocketClient.stop();
		} catch (Exception e) {
			System.out.println("Failed to stop web socket client. Error: " + e.getMessage());
		}
	}
	
	/**
	 * @see com.mnxfst.stream.listener.StreamEventListener#shutdown()
	 */
	public void shutdown() {
		this.isRunning = false;
	}

}
