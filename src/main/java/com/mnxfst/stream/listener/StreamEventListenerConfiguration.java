/**
 * Copyright (c) 2014, otto group and/or its affiliates. All rights reserved.
 * OTTO GROUP PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.mnxfst.stream.listener;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonRootName;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.mnxfst.stream.listener.webtrends.WebTrendsStreamListenerConfiguration;

/**
 * Common interface for listener configuration
 * @author mnxfst
 * @since 06.02.2014
 *
 */
@JsonRootName ( value = "listenerConfiguration" )
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
@JsonSubTypes ({ @JsonSubTypes.Type(WebTrendsStreamListenerConfiguration.class) })
public interface StreamEventListenerConfiguration extends Serializable {

	/** Sets the listener id */
	public void setIdentifier(String identifier);
	/** Returns the listener id */
	public String getIdentifier();
	/** Sets the id of the receiving dispatcher */
	public void setDispatcherIdentifier(String dispatcherIdentifier);
	/** Returns the identifier of the receiving dispatcher */
	public String getDispatcherIdentifier();
	/** sets the listener class name */
	public void setListenerClassName(String listenerClassName);
	/** get listener class name */
	public String getListenerClassName();
	
}
