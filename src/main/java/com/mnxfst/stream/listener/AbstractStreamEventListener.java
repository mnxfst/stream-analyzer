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
public abstract class AbstractStreamEventListener implements Runnable {

	/** configuration */
	private final StreamEventListenerConfiguration configuration;
	/** reference towards message receiving dispatcher */
	private final ActorRef dispatcherRef;
	
	/**
	 * Initializes the listener
	 * @param cfg
	 * @param dispatcherRef
	 */
	public AbstractStreamEventListener(final StreamEventListenerConfiguration cfg, final ActorRef dispatcherRef) {
		this.configuration = cfg;
		this.dispatcherRef = dispatcherRef;
	}
	
	/**
	 * Forwards the {@link StreamEventMessage message} to the configured {@link StreamEventDispatcher dispatcher}
	 * @param streamEventMessage
	 */
	protected void dispatchMessage(final StreamEventMessage streamEventMessage) {
		this.dispatcherRef.tell(streamEventMessage, null);
	}
	
	/**
	 * Shutsdown the listener
	 */
	public abstract void shutdown();


}
