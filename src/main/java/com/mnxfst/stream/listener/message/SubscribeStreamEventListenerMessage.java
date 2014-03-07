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
package com.mnxfst.stream.listener.message;

import java.io.Serializable;

import akka.actor.ActorRef;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;

/**
 * Tells a node to subscribe the referenced {@link ActorRef node} as receiver for inbound messsages
 * @author mnxfst
 * @since Mar 7, 2014
 *
 */
@JsonRootName ( value = "subscribeStreamEventListenerMessage" )
public class SubscribeStreamEventListenerMessage implements Serializable {

	private static final long serialVersionUID = 4018444978605621283L;
	
	@JsonProperty ( value = "subscriberRef", required = true )
	private ActorRef subscriberRef = null;
	
	/**
	 * Default constructor
	 */
	public SubscribeStreamEventListenerMessage() {		
	}
	
	/**
	 * Initializes the message using the provided input
	 * @param subscriberRef
	 */
	public SubscribeStreamEventListenerMessage(final ActorRef subscriberRef) {
		this.subscriberRef = subscriberRef;
	}

	public ActorRef getSubscriberRef() {
		return subscriberRef;
	}

	public void setSubscriberRef(ActorRef subscriberRef) {
		this.subscriberRef = subscriberRef;
	}

}
