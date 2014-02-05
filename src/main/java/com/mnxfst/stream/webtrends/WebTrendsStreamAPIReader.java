/**
 * Copyright (c) 2014, otto group and/or its affiliates. All rights reserved.
 * OTTO GROUP PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.mnxfst.stream.webtrends;

import java.net.URI;
import java.util.concurrent.TimeUnit;

import org.eclipse.jetty.websocket.client.ClientUpgradeRequest;
import org.eclipse.jetty.websocket.client.WebSocketClient;

import akka.actor.ActorRef;

/**
 * Implements a reader for fetching contents from {@linkplain http://streams.webtrends.com}
 * @author mnxfst
 * @since 05.02.2014
 *
 */
public class WebTrendsStreamAPIReader implements Runnable {

	private final String clientId;
	private final String clientSecret;
	private final String streamType;
	private final String streamVersion;
	private final String streamSchemaVersion;
	private final String streamQuery;
	private final ActorRef dispatcherRef;
	private String oAuthToken;
	private WebSocketClient webSocketClient;
	private boolean isRunning = false;

	/**
	 * Initialize stream api reader using the provided input
	 * @param clientId 
	 * @param clientSecret
	 * @param streamType
	 * @param streamVersion
	 * @param streamQuery
	 * @param dispatcherRef
	 */
	public WebTrendsStreamAPIReader(final String clientId, final String clientSecret, final String streamType, final String streamVersion, final String streamSchemaVersion, final String streamQuery, final ActorRef dispatcherRef) {
		this.clientId = clientId;
		this.clientSecret = clientSecret;
		this.streamType = streamType;
		this.streamVersion = streamVersion;
		this.streamSchemaVersion = streamSchemaVersion;
		this.streamQuery = streamQuery;
		this.dispatcherRef = dispatcherRef;
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
		WebTrendsStreamSocket socket = new WebTrendsStreamSocket(this.oAuthToken, this.streamType, this.streamQuery, this.streamVersion, this.streamSchemaVersion, this.dispatcherRef);
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
	
	public void stop() {
		this.isRunning = false;
	}

}
