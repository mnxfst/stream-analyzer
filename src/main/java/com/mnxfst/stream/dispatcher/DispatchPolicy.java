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
package com.mnxfst.stream.dispatcher;

import java.util.Map;
import java.util.Set;

import com.mnxfst.stream.message.StreamEventMessage;

/**
 * Provides a common interface to policy implementation which take a
 * {@link StreamEventMessage event message} and determine dispatch
 * destinations
 * @author mnxfst
 * @since 28.02.2014
 *
 */
public abstract class DispatchPolicy {

	/** policy name */	
	private String name;

	/**
	 * Initializes the policy
	 * @param name
	 */
	public DispatchPolicy(final String name) {
		this.name = name;
	}
	
	/**
	 * Initializes the policy using implementation specific settings
	 * @param settings
	 */
	public abstract void init(final Map<String,String> settings);

	/**
	 * Determines the destinations an {@link StreamEventMessage event message} must
	 * be dispatched to 
	 * @param message
	 * @return
	 */
	public abstract Set<String> determineDestinations(final StreamEventMessage message);

	/**
	 * Returns the policy name 
	 * @return
	 */
	public String getName() {
		return this.name;
	}
	
	
}
