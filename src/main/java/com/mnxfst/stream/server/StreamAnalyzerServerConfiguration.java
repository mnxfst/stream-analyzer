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
package com.mnxfst.stream.server;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;

/**
 * Holds configuration for {@link StreamAnalyzerServer}
 * @author mnxfst
 * @since 05.02.2014
 */
@JsonRootName ( value = "serverConfigration" )
public class StreamAnalyzerServerConfiguration implements Serializable {

	private static final long serialVersionUID = 2229639751524073732L;

	/** port to reach the server */
	@JsonProperty ( value = "port", required = true )
	private int port = 9090;
	
	
}
