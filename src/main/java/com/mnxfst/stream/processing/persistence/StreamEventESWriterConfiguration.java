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
package com.mnxfst.stream.processing.persistence;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.codehaus.jackson.map.ObjectMapper;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;
import com.mnxfst.stream.processing.StreamEventProcessingNodeConfiguration;
import com.mnxfst.stream.processing.model.TransportAddress;

/**
 * Configuration required for {@link StreamEventESWriter elasticsearch writer} initialization
 * @author mnxfst
 * @since Feb 4, 2014
 */
@JsonRootName ( value = "esWriterConfiguration")
public class StreamEventESWriterConfiguration extends StreamEventProcessingNodeConfiguration {

	private static final long serialVersionUID = 6338252958784216081L;

	/** index to write inbound events to */
	@JsonProperty ( value = "esIndex", required = true )
	private String esIndex = null;
	/** document type to use when writing to index */
	@JsonProperty ( value = "documentType", required = true )
	private String documentType = null;
	/** elastic search cluster nodes */
	@JsonProperty ( value = "esClusterNodes", required = true ) 
	private Set<TransportAddress> esClusterNodes = new HashSet<>();
	/** additional settings */
	@JsonProperty ( value = "clientSettings", required = true )
	private Map<String, String> clientSettings = new HashMap<>();
	
	/**
	 * Default constructor
	 */
	public StreamEventESWriterConfiguration() {		
	}
	
	/**
	 * Initializes the configuration using the provided input
	 * @param processingNodeClass
	 * @param identifier
	 * @param description
	 * @param entryPointId
	 * @param esIndex
	 * @param documentType
	 */
	public StreamEventESWriterConfiguration(final String processingNodeClass, final String identifier, final String description, final int numOfNodeInstances, final String esIndex, final String documentType) {
		super(processingNodeClass, identifier, description, numOfNodeInstances);
		this.esIndex = esIndex;
		this.documentType = documentType;
	}
	
	/**
	 * Adds a cluster node address to the configuration
	 * @param host
	 * @param port
	 */
	public void addESClusterNode(final String host, final Integer port) {
		
		this.esClusterNodes.add(new TransportAddress(host, port));
	}
	
	/**
	 * Adds a new client setting
	 * @param key
	 * @param value
	 */
	public void addClientSetting(final String key, final String value) {
		this.clientSettings.put(key, value);
	}
	public String getEsIndex() {
		return esIndex;
	}

	public void setEsIndex(String esIndex) {
		this.esIndex = esIndex;
	}

	public String getDocumentType() {
		return documentType;
	}

	public void setDocumentType(String documentType) {
		this.documentType = documentType;
	}

	public Set<TransportAddress> getEsClusterNodes() {
		return esClusterNodes;
	}

	public void setEsClusterNodes(Set<TransportAddress> esClusterNodes) {
		this.esClusterNodes = esClusterNodes;
	}

	public Map<String, String> getClientSettings() {
		return clientSettings;
	}

	public void setClientSettings(Map<String, String> clientSettings) {
		this.clientSettings = clientSettings;
	}

	public static void main(String[] args) throws Exception {
		StreamEventESWriterConfiguration cfg = new StreamEventESWriterConfiguration();
		cfg.addESClusterNode("localhost",  9300);
		cfg.addClientSetting("cluster.name", "tracker");
		System.out.println((new ObjectMapper()).writeValueAsString(cfg));
	}
	
}
