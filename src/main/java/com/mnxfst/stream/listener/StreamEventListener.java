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

package com.mnxfst.stream.listener;

import java.util.HashSet;
import java.util.Set;

import akka.actor.ActorRef;

import com.mnxfst.stream.processing.dispatcher.StreamEventDispatcher;
import com.mnxfst.stream.processing.message.StreamEventMessage;

/**
 * Base class for all listeners that connect and fetch data from a remote location 
 * @author mnxfst
 * @since 06.02.2014
 *
 */
public abstract class StreamEventListener implements Runnable {

	/** reference towards message receiving dispatcher */
	protected final Set<ActorRef> dispatcherReferences = new HashSet<>();
	
	/**
	 * Initializes the listener
	 * @param cfg
	 */
	public StreamEventListener(final StreamEventListenerConfiguration cfg) {
		if(cfg == null)
			throw new RuntimeException("Missing required listener configuration");
	}
	
	/**
	 * Adds a new {@link StreamEventDispatcher dispatcher} receiving 
	 * @param dispatcherReference
	 */
	public void addDispatcherReference(final ActorRef dispatcherReference) {
		this.dispatcherReferences.add(dispatcherReference);
	}
	
	/**
	 * Forwards the {@link StreamEventMessage message} to the configured {@link StreamEventDispatcher dispatcher}
	 * @param streamEventMessage
	 */
	protected void dispatchMessage(final StreamEventMessage streamEventMessage) {
		for(final ActorRef dispatcher : this.dispatcherReferences)
			dispatcher.tell(streamEventMessage, null);
	}
	
	/**
	 * Shuts down the listener
	 */
	public abstract void shutdown();

	
}
