/**
 * Copyright (c) 2014, otto group and/or its affiliates. All rights reserved.
 * OTTO GROUP PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.mnxfst.stream.listener.webtrends;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;
import com.mnxfst.stream.listener.StreamEventListenerConfiguration;

/**
 * 
 * @author mnxfst
 * @since 06.02.2014
 *
 */
@JsonRootName ( value = "webtrendsStreamListenerConfiguration" )
public class WebTrendsStreamListenerConfiguration implements
		StreamEventListenerConfiguration {

	private static final long serialVersionUID = 7518374251142479068L;

	/** listener id */
	@JsonProperty ( value = "identifier", required = true )
	private String identifier = null;
	/** id of dispatcher receiving all listener inbound traffic */
	@JsonProperty ( value = "dispatcherIdentifier", required = true )
	private String dispatcherIdentifier = null;
	/** class implementing the listener */
	@JsonProperty ( value = "listenerClassName", required = true )
	private String listenerClassName = null;
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
		this.identifier = identifier;
		this.dispatcherIdentifier = dispatcherIdentifier;
		this.listenerClassName = listenerClassName;
		this.clientId = clientId;
		this.clientSecret = clientSecret;
		this.streamType = streamType;
		this.streamQuery = streamQuery;
		this.streamVersion = streamVersion;
		this.streamSchemaVersion = streamSchemaVersion;
	}

	public String getIdentifier() {
		return identifier;
	}

	public void setIdentifier(String identifier) {
		this.identifier = identifier;
	}

	public String getDispatcherIdentifier() {
		return dispatcherIdentifier;
	}

	public void setDispatcherIdentifier(String dispatcherIdentifier) {
		this.dispatcherIdentifier = dispatcherIdentifier;
	}

	public String getListenerClassName() {
		return listenerClassName;
	}

	public void setListenerClassName(String listenerClassName) {
		this.listenerClassName = listenerClassName;
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
