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
package com.mnxfst.stream.persistence;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;
import com.mnxfst.stream.processing.StreamEventProcessingNodeConfiguration;

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
	/** reference towards component receiving all error inbound messages */
	@JsonProperty( value = "errorHandlers", required = true )
	private Map<String, Set<String>> errorHandlers = new HashMap<>();

	/**
	 * Default constructor
	 */
	public StreamEventESWriterConfiguration() {		
	}
	
	/**
	 * Initializes the configuration using the provided input
	 * @param identifier
	 * @param description
	 * @param entryPointId
	 */
	public StreamEventESWriterConfiguration(final String processingNodeClass, final String identifier, final String description) {
		this.processingNodeClass = processingNodeClass;
		this.identifier = identifier;
		this.description = description;
	}

	/**
	 * @see com.mnxfst.stream.processing.StreamEventProcessingNodeConfiguration#addErrorHandlers(java.lang.String, java.util.Set)
	 */
	public void addErrorHandlers(String errorKey, Set<String> handlers) {

		if(StringUtils.isNotBlank(errorKey) && handlers != null && !handlers.isEmpty()) {
			Set<String> hdlrs = this.errorHandlers.get(errorKey);
			if(hdlrs == null)
				hdlrs = new HashSet<>();
			hdlrs.addAll(handlers);
			this.errorHandlers.put(errorKey, hdlrs);
		}
		
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

	public Map<String, Set<String>> getErrorHandlers() {
		return errorHandlers;
	}

	public void setErrorHandlers(Map<String, Set<String>> errorHandlers) {
		this.errorHandlers = errorHandlers;
	}

}
