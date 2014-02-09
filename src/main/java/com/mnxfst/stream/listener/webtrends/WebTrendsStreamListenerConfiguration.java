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

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;
import com.mnxfst.stream.listener.StreamEventListenerConfiguration;

/**
 * Holds all information required for setting up the {@link WebTrendsStreamAPIListener webtrends stream api listener} 
 * @author mnxfst
 * @since 06.02.2014
 *
 */
@JsonRootName ( value = "webTrendsListenerConfiguration" )
public class WebTrendsStreamListenerConfiguration extends StreamEventListenerConfiguration {

	private static final long serialVersionUID = 7518374251142479068L;

	/** client identifier required for authentication */
	@JsonProperty ( value = "clientId", required = true )
	private String clientId = null;
	/** client secret required for authentication */
	@JsonProperty ( value = "clientSecret", required = true )
	private String clientSecret = null;
	/** type of stream to fetch from webtrends api */
	@JsonProperty ( value = "streamType", required = true )	
	private String streamType = null;
	/** query to issue against webtrends api to fetch content */
	@JsonProperty ( value = "streamQuery", required = true )
	private String streamQuery = null;
	/** version of stream to access */
	@JsonProperty ( value = "streamVersion", required = true )	
	private String streamVersion = null;
	/** schema version of retrieved events */
	@JsonProperty ( value = "streamSchemaVersion", required = true )
	private String streamSchemaVersion = null;
	
	/**
	 * Default constructor
	 */
	public WebTrendsStreamListenerConfiguration() {		
	}
	
	/**
	 * Initializes the configuration using the provided input
	 * @param identifier
	 * @param dispatcherIdentifier
	 * @param listenerClassName
	 * @param clientId
	 * @param clientSecret
	 * @param streamType
	 * @param streamQuery
	 * @param streamVersion
	 * @param streamSchemaVersion
	 */
	public WebTrendsStreamListenerConfiguration(final String identifier, final String dispatcherIdentifier, final String listenerClassName, 
			final String clientId, final String clientSecret, final String streamType, final String streamQuery, final String streamVersion, final String streamSchemaVersion) {
		super(identifier, listenerClassName);
		this.clientId = clientId;
		this.clientSecret = clientSecret;
		this.streamType = streamType;
		this.streamQuery = streamQuery;
		this.streamVersion = streamVersion;
		this.streamSchemaVersion = streamSchemaVersion;
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

	public String getStreamSchemaVersion() {
		return streamSchemaVersion;
	}

	public void setStreamSchemaVersion(String streamSchemaVersion) {
		this.streamSchemaVersion = streamSchemaVersion;
	}


}
