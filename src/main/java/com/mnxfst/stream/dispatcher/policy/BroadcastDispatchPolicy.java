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
package com.mnxfst.stream.dispatcher.policy;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;

import com.mnxfst.stream.dispatcher.DispatchPolicy;
import com.mnxfst.stream.message.StreamEventMessage;

/**
 * Implements a broadcast dispatch policy which simply returns all destination identifiers previously received via settings
 * @author mnxfst
 * @since 05.03.2014
 *
 */
public class BroadcastDispatchPolicy extends DispatchPolicy {

	public static final String BROADCAST_DESTINATION_PREFIX = "broadcast.destination.";
	
	private final Set<String> broadcastDestinations = new HashSet<>();
	
	/**
	 * Initializes the policy
	 * @param name
	 */
	public BroadcastDispatchPolicy(String name) {
		super(name);
	}

	/**
	 * @see com.mnxfst.stream.dispatcher.DispatchPolicy#init(java.util.Map)
	 */
	public void init(Map<String, String> settings) {
		
		if(settings != null && !settings.isEmpty()) {
			for(int i = 0; i < Integer.MAX_VALUE; i++) {
				if(settings.containsKey(BROADCAST_DESTINATION_PREFIX + i)) {
					String value = settings.get(BROADCAST_DESTINATION_PREFIX+i);
					if(StringUtils.isNotBlank(value))
						this.broadcastDestinations.add(value.trim());
				}
				break;
			}
		}		
	}

	/**
	 * @see com.mnxfst.stream.dispatcher.DispatchPolicy#determineDestinations(com.mnxfst.stream.message.StreamEventMessage)
	 */
	public Set<String> determineDestinations(StreamEventMessage message) {
		return broadcastDestinations;
	}

}
