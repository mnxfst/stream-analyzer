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

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.mnxfst.stream.message.StreamEventMessage;

/**
 * @author mnxfst
 * @since 28.02.2014
 *
 */
public class TestDispatcher extends DispatchPolicy {
	
	private Set<String> defaultDispatchers = new HashSet<>();
	
	public TestDispatcher(final String name) {
		super(name);
	}

	/**
	 * @see com.mnxfst.stream.dispatcher.DispatchPolicy#init(java.util.Map)
	 */
	public void init(Map<String, String> settings) {
		this.defaultDispatchers.addAll(settings.keySet());
	}

	/**
	 * @see com.mnxfst.stream.dispatcher.DispatchPolicy#determineDestinations(com.mnxfst.stream.message.StreamEventMessage)
	 */
	public Set<String> determineDestinations(StreamEventMessage message) {
		return this.defaultDispatchers;
	}
	
	
	
}