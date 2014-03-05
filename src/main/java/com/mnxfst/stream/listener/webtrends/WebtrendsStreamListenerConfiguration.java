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

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;
import com.mnxfst.stream.listener.StreamEventListenerConfiguration;

/**
 * Required settings for initializing the {@link WebtrendsStreamListener webtrends stream} listener
 * @author mnxfst
 * @since 28.02.2014
 * @deprecated use {@link StreamEventListenerConfiguration}
 */
@JsonRootName ( value = "webtrendsStreamListener" )
public class WebtrendsStreamListenerConfiguration extends StreamEventListenerConfiguration {

	private static final long serialVersionUID = -5898868657735758904L;

	/** audience required for authentication */
	@JsonProperty ( value = "authAudience", required = true )
	public String authAudience;
	
	/** access scope */
	@JsonProperty ( value = "authScope", required = true )
    public String authScope = "sapi.webtrends.com";
	
	/** authentication url */
	@JsonProperty ( value = "authUrl", required = true )
    public String authUrl = "https://sauth.webtrends.com/v1/token";

	/** url to read webtrends stream events from */ 
	@JsonProperty ( value = "eventStreamUrl", required = true )
	private String eventStreamUrl;
	
	/** client identifier required for authentication */
	@JsonProperty ( value = "clientId", required = true  )	
	private String clientId;
	
	/** password required for authentication */
	@JsonProperty ( value = "clientSecret", required = true )
	private String clientSecret;
	
	/** stream to read from, eg. event or session - see webtrends docs for detailed information */
	@JsonProperty ( value = "streamType", required = true )
	private String streamType;
	
	/** query to filter inbound events */
	@JsonProperty ( value = "streamQuery", required = true )
	private String streamQuery;

	/** version of stream */
	@JsonProperty ( value = "streamVersion", required = true )
	private String streamVersion;
	
	/** schema version */
	@JsonProperty ( value = "schemaVersion", required = true )
	private String schemaVersion;
	
	/**
	 * Default constructor
	 */
	public WebtrendsStreamListenerConfiguration() {		
	}
	
	/**
	 * Initializes the configuration using the provided input
	 * @param id
	 * @param name
	 * @param description
	 * @param version
	 * @param authUrl
	 * @param authScope
	 * @param authAudience
	 * @param eventStreamUrl
	 * @param clientId
	 * @param clientSecret
	 * @param streamType
	 * @param streamQuery
	 * @param streamVersion
	 * @param schemaVersion
	 */
	public WebtrendsStreamListenerConfiguration(final String id, final String name, final String description, final String version, 
			final String authUrl, final String authScope, final String authAudience, final String eventStreamUrl, final String clientId, 
			final String clientSecret, final String streamType, final String streamQuery, final String streamVersion, final String schemaVersion) {
		
		this.authUrl = authUrl;
		this.authScope = authScope;
		this.authAudience = authAudience;
		this.eventStreamUrl = eventStreamUrl;
		this.clientId = clientId;
		this.clientSecret = clientSecret;
		this.streamType = streamType;
		this.streamQuery = streamQuery;
		this.streamVersion = streamVersion;
		this.schemaVersion = schemaVersion;
	}

	public String getAuthAudience() {
		return authAudience;
	}

	public void setAuthAudience(String authAudience) {
		this.authAudience = authAudience;
	}

	public String getAuthScope() {
		return authScope;
	}

	public void setAuthScope(String authScope) {
		this.authScope = authScope;
	}

	public String getAuthUrl() {
		return authUrl;
	}

	public void setAuthUrl(String authUrl) {
		this.authUrl = authUrl;
	}

	public String getEventStreamUrl() {
		return eventStreamUrl;
	}

	public void setEventStreamUrl(String eventStreamUrl) {
		this.eventStreamUrl = eventStreamUrl;
	}

	public String getClientId() {
		return clientId;
	}

	public void setClientId(String clientId) {
		this.clientId = clientId;
	}

	public String getClientSecret() {
		return clientSecret;
	}

	public void setClientSecret(String clientSecret) {
		this.clientSecret = clientSecret;
	}

	public String getStreamType() {
		return streamType;
	}

	public void setStreamType(String streamType) {
		this.streamType = streamType;
	}

	public String getStreamQuery() {
		return streamQuery;
	}

	public void setStreamQuery(String streamQuery) {
		this.streamQuery = streamQuery;
	}

	public String getStreamVersion() {
		return streamVersion;
	}

	public void setStreamVersion(String streamVersion) {
		this.streamVersion = streamVersion;
	}

	public String getSchemaVersion() {
		return schemaVersion;
	}

	public void setSchemaVersion(String schemaVersion) {
		this.schemaVersion = schemaVersion;
	}
	
	
}
