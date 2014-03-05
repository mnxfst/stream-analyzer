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
package com.mnxfst.stream.listener.webtrends;

import java.net.URI;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.eclipse.jetty.websocket.client.ClientUpgradeRequest;
import org.eclipse.jetty.websocket.client.WebSocketClient;

import akka.actor.ActorRef;

import com.mnxfst.stream.listener.StreamEventListener;
import com.mnxfst.stream.listener.StreamEventListenerConfiguration;

/**
 * Listens to {@link http://www.webtrends.com webtrends} stream api and emits events into analyzer
 * @author mnxfst
 * @since 28.02.2014
 * TODO testing
 */
public class WebtrendsStreamListener implements StreamEventListener {

	public static final String WT_CONFIG_AUTH_AUDIENCE = "wt.authAudience";
	public static final String WT_CONFIG_AUTH_SCOPE = "wt.authScope";
	public static final String WT_CONFIG_AUTH_URL = "wt.authUrl";
	public static final String WT_CONFIG_EVENT_STREAM_URL = "wt.eventStreamUrl";
	public static final String WT_CONFIG_CLIENT_ID = "wt.clientId";
	public static final String WT_CONFIG_CLIENT_SECRET = "wt.clientSecret";
	public static final String WT_CONFIG_STREAM_TYPE = "wt.streamType";
	public static final String WT_CONFIG_STREAM_QUERY = "wt.streamQuery";
	public static final String WT_CONFIG_STREAM_VERSION = "wt.streamVersion";
	public static final String WT_CONFIG_SCHEMA_VERSION = "wt.schemaVersion";
	
	private final StreamEventListenerConfiguration configuration;

	public String authAudience;
    public String authScope = "sapi.webtrends.com";
    public String authUrl = "https://sauth.webtrends.com/v1/token";
	private String eventStreamUrl;
	private String clientId;
	private String clientSecret;
	private String streamType;
	private String streamQuery;
	private String streamVersion;
	private String schemaVersion;
	
	private WebSocketClient webtrendsStreamSocketClient;
	private boolean isRunning = false;

	/**
	 * Initializes the listener instance using the provided configuration
	 * @param configuration
	 */
	public WebtrendsStreamListener(final StreamEventListenerConfiguration configuration) {
		this.configuration = configuration;
		
		this.authAudience = configuration.getSettings().get(WT_CONFIG_AUTH_AUDIENCE);
		this.authScope = configuration.getSettings().get(WT_CONFIG_AUTH_SCOPE);
		this.authUrl = configuration.getSettings().get(WT_CONFIG_AUTH_URL);
		this.eventStreamUrl = configuration.getSettings().get(WT_CONFIG_EVENT_STREAM_URL);
		this.clientId = configuration.getSettings().get(WT_CONFIG_CLIENT_ID);
		this.clientSecret = configuration.getSettings().get(WT_CONFIG_CLIENT_SECRET);
		this.streamType = configuration.getSettings().get(WT_CONFIG_STREAM_TYPE);
		this.streamQuery = configuration.getSettings().get(WT_CONFIG_STREAM_QUERY);
		this.streamVersion = configuration.getSettings().get(WT_CONFIG_STREAM_VERSION);
		this.schemaVersion = configuration.getSettings().get(WT_CONFIG_SCHEMA_VERSION);		
	}
	
	/**
	 * Establishes a connection with the webtrends streaming api
	 */
	public void connect() {

		String oAuthToken = null;
		try {
			oAuthToken = new WebtrendsTokenRequest(this.authUrl, this.authAudience, this.authScope, 
					this.clientId, this.clientSecret).execute();
		} catch(Exception e) {
			throw new RuntimeException("Failed to request webtrends token. Error: " + e.getMessage(), e);
		}
		
		// TODO dispatchers
		Set<ActorRef> dispatcherRefs = null;
		this.webtrendsStreamSocketClient = new WebSocketClient();
		WebtrendsStreamSocket socket = new WebtrendsStreamSocket(oAuthToken, this.streamType, this.streamQuery,
				this.streamVersion, this.schemaVersion, dispatcherRefs);

		try {
			this.webtrendsStreamSocketClient.start();
			ClientUpgradeRequest upgradeRequest = new ClientUpgradeRequest();
			this.webtrendsStreamSocketClient.connect(socket, new URI(this.eventStreamUrl), upgradeRequest);
			socket.await(5, TimeUnit.SECONDS);
		} catch(Exception e) {
			throw new RuntimeException("Unable to connect to web socket: " + e.getMessage(), e);
		}
		
		isRunning = true;
		
	}
	
	/**
	 * @see java.lang.Runnable#run()
	 */
	public void run() {
		
		connect();
		
		while(isRunning) {
			// 
		}

		disconnect();

	}
	
	/**
	 * Disconnects from webtrends streaming api
	 */
	public void disconnect() {
		
		try {
			this.webtrendsStreamSocketClient.stop();
		} catch (Exception e) {
			System.out.println("Failed to stop web socket client. Error: " + e.getMessage());
		}		
	}

	/**
	 * @see com.mnxfst.stream.listener.StreamEventListener#getId()
	 */
	public String getId() {
		return (this.configuration != null ? this.configuration.getId() : null);
	}

	/**
	 * @see com.mnxfst.stream.listener.StreamEventListener#getName()
	 */
	public String getName() {		
		return (this.configuration != null ? this.configuration.getName() : null);
	}

	/**
	 * @see com.mnxfst.stream.listener.StreamEventListener#getDescription()
	 */
	public String getDescription() {
		return (this.configuration != null ? this.configuration.getDescription() : null);
	}

	/**
	 * @see com.mnxfst.stream.listener.StreamEventListener#getVersion()
	 */
	public String getVersion() {
		return (this.configuration != null ? this.configuration.getVersion() : null); 	
	}
	
	

}
