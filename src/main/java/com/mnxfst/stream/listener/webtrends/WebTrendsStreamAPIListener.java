/**
 * Copyright (c) 2014, otto group and/or its affiliates. All rights reserved.
 * OTTO GROUP PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.mnxfst.stream.listener.webtrends;

import java.net.URI;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.jetty.websocket.client.ClientUpgradeRequest;
import org.eclipse.jetty.websocket.client.WebSocketClient;

import akka.actor.ActorRef;

import com.mnxfst.stream.listener.AbstractStreamAnalyzerListener;

/**
 * Implements a reader for fetching contents from {@linkplain http://streams.webtrends.com}
 * @author mnxfst
 * @since 05.02.2014
 *
 */
public class WebTrendsStreamAPIListener extends AbstractStreamAnalyzerListener {

	private final WebTrendsStreamListenerConfiguration configuration;
	private final ActorRef dispatcherRef;

	private String oAuthToken;
	private WebSocketClient webSocketClient;
	private boolean isRunning = false;

	/**
	 * Initialize stream api reader using the provided input
	 * @param configuration
	 * @param dispatcherRef
	 */
	public WebTrendsStreamAPIListener(final WebTrendsStreamListenerConfiguration configuration, final ActorRef dispatcherRef) {

		super((configuration != null ? configuration.getIdentifier() : null), (configuration != null ? configuration.getDispatcherIdentifier() : null),
				dispatcherRef);

		if(configuration == null)
			throw new RuntimeException("Missing required listener configuraton");
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
		if(dispatcherRef == null)
			throw new RuntimeException("Missing required dispatcher reference");

		this.configuration = configuration;
		this.dispatcherRef = dispatcherRef;
	}
	
	/**
	 * @see java.lang.Runnable#run()
	 */
	public void run() {

		try {
			this.oAuthToken = new WebTrendsTokenRequest(this.configuration.getClientId(), this.configuration.getClientSecret()).execute();
		} catch(Exception e) {
			throw new RuntimeException("Failed to request webtrends token. Error: " + e.getMessage(), e);
		}
		
		this.webSocketClient = new WebSocketClient();
		WebTrendsStreamSocket socket = new WebTrendsStreamSocket(this.oAuthToken, this.configuration.getStreamType(), this.configuration.getStreamQuery(), 
				this.configuration.getStreamVersion(), this.configuration.getStreamSchemaVersion(), this.dispatcherRef);
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
