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
package com.mnxfst.stream.server.config;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;
import com.mnxfst.stream.dispatcher.config.StreamEventMessageDispatcherConfiguration;
import com.mnxfst.stream.listener.StreamEventListenerConfiguration;

/**
 * Holds all required configuration for setting up the stream analyzer server
 * @author mnxfst
 * @since 05.03.2014
 *
 */
@JsonRootName ( value = "streamAnalyzerServerConfiguration" )
public class StreamAnalyzerServerConfiguration implements Serializable {

	private static final long serialVersionUID = 3441450724545429049L;
	
	/** listener configurations */
	@JsonProperty ( value = "listeners", required = true )
	private List<StreamEventListenerConfiguration> listeners = new ArrayList<>();
	
	/** dispatcher configurations */
	@JsonProperty ( value = "dispatchers", required = true )
	private List<StreamEventMessageDispatcherConfiguration> dispatchers = new ArrayList<>();
	

}
