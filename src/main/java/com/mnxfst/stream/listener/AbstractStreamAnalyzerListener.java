/**
 * Copyright (c) 2014, otto group and/or its affiliates. All rights reserved.
 * OTTO GROUP PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.mnxfst.stream.listener;

import akka.actor.ActorRef;

import com.mnxfst.stream.processing.dispatcher.StreamEventDispatcher;
import com.mnxfst.stream.processing.message.StreamEventMessage;

/**
 * Base class for all listeners that connect and fetch data from a remote location 
 * @author mnxfst
 * @since 06.02.2014
 *
 */
public abstract class AbstractStreamAnalyzerListener implements Runnable {

	/** listener id */
	private final String identifier;
	/** id of message receiving dispatcher */
	private final String dispatcherIdentifier;
	/** reference towards message receiving dispatcher */
	private final ActorRef dispatcherRef;
	
	/**
	 * Initializes the listener
	 * @param identifier
	 * @param dispatcherIdentifier
	 * @param dispatcherRef
	 */
	public AbstractStreamAnalyzerListener(final String identifier, final String dispatcherIdentifier, final ActorRef dispatcherRef) {
		this.identifier = identifier;
		this.dispatcherIdentifier = dispatcherIdentifier;
		this.dispatcherRef = dispatcherRef;
	}
	
	/**
	 * Forwards the {@link StreamEventMessage message} to the configured {@link StreamEventDispatcher dispatcher}
	 * @param streamEventMessage
	 */
	protected void dispatchMessage(final StreamEventMessage streamEventMessage) {
		this.dispatcherRef.tell(streamEventMessage, null);
	}

	public String getIdentifier() {
		return identifier;
	}

	public ActorRef getDispatcherRef() {
		return dispatcherRef;
	}

	public String getDispatcherIdentifier() {
		return dispatcherIdentifier;
	}

}
