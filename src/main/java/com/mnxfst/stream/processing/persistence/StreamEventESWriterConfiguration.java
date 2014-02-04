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

import java.util.HashSet;
import java.util.Set;

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
public class StreamEventESWriterConfiguration implements StreamEventProcessingNodeConfiguration {

	private static final long serialVersionUID = 6338252958784216081L;

	/** processing node class - required for dynamic node instantiation */
	@JsonProperty ( value = "processingNodeClass", required = true )
	private String processingNodeClass = null;
	/** analyzer id used for referencing the component from within other pipeline elements */ 
	@JsonProperty( value = "identifier", required = true )
	private String identifier = null;
	/** description */
	@JsonProperty ( value = "description", required = false )
	private String description = null;
	/** number of node instances - value of less than 1 avoids the instantiation of any router */
	@JsonProperty ( value = "numOfNodeInstances", required = true )
	private int numOfNodeInstances = 0; // value o
	/** index to write inbound events to */
	@JsonProperty ( value = "esIndex", required = true )
	private String esIndex = null;
	/** document type to use when writing to index */
	@JsonProperty ( value = "documentType", required = true )
	private String documentType = null;
	/** elastic search cluster nodes */
	@JsonProperty ( value = "esClusterNodes", required = true ) 
	private Set<TransportAddress> esClusterNodes = new HashSet<>();

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
		this.processingNodeClass = processingNodeClass;
		this.identifier = identifier;
		this.description = description;
		this.esIndex = esIndex;
		this.documentType = documentType;
		this.numOfNodeInstances = numOfNodeInstances;
	}
	
	/**
	 * Adds a cluster node address to the configuration
	 * @param host
	 * @param port
	 */
	public void addESClusterNode(final String host, final Integer port) {
		
		this.esClusterNodes.add(new TransportAddress(host, port));
	}

	public String getProcessingNodeClass() {
		return processingNodeClass;
	}

	public void setProcessingNodeClass(String processingNodeClass) {
		this.processingNodeClass = processingNodeClass;
	}

	public String getIdentifier() {
		return identifier;
	}

	public void setIdentifier(String identifier) {
		this.identifier = identifier;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
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

	public int getNumOfNodeInstances() {
		return numOfNodeInstances;
	}

	public void setNumOfNodeInstances(int numOfNodeInstances) {
		this.numOfNodeInstances = numOfNodeInstances;
	}

}
