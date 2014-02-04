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
package com.mnxfst.stream.model;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;

/**
 * @author mnxfst
 * @since Feb 4, 2014
 */
@JsonRootName ( value = "transportAddress" )
public class TransportAddress implements Serializable {

	private static final long serialVersionUID = -4808981840592208089L;

	@JsonProperty ( value = "host", required = true )
	private String host = null;
	@JsonProperty ( value = "port", required = true )
	private Integer port = null;
	
	/**
	 * Default constructor
	 */
	public TransportAddress() {		
	}
	
	/**
	 * Initializes the address using the provided input
	 * @param host
	 * @param port
	 */
	public TransportAddress(final String host, final Integer port) {
		this.host = host;
		this.port = port;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public Integer getPort() {
		return port;
	}

	public void setPort(Integer port) {
		this.port = port;
	}
	

}
