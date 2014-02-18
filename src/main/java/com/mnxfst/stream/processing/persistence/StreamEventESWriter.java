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
package com.mnxfst.stream.processing.persistence;

import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;

import com.mnxfst.stream.processing.StreamEventProcessingNode;
import com.mnxfst.stream.processing.message.StreamEventMessage;
import com.mnxfst.stream.processing.model.TransportAddress;

/**
 * Writes the {@link StreamEventMessage#getContent() content} of a {@link StreamEventMessage stream event} to a configured
 * elasticsearch destination. All relevant modifications to the json content must be applied by now as the writer simply
 * inserts the {@link StreamEventMessage#getContent() json content} into elastisearch.
 * @author mnxfst
 * @since 31.01.2014
 */
public class StreamEventESWriter extends StreamEventProcessingNode {

	/** custom attribute possibly holding index */
	public static final String CUSTOM_ATTR_INDEX = "esWriterIndex";
	/** custom attribute possibly holding document type */
	public static final String CUSTOM_ATTR_DOC_TYPE = "esWriterDocumentType";
	
	
	/** es writer configuration */
	private final StreamEventESWriterConfiguration configuration;
	
	/** client used for transporting content to elasticsearch server/cluster */
	private TransportClient esClient;
	
	/**
	 * Initializes the stream event writer using the provided input
	 * @param configuration 
	 */
	public StreamEventESWriter(final StreamEventESWriterConfiguration configuration) {
		super(configuration);
		this.configuration = configuration;
	}
		
	/**
	 * @see akka.actor.UntypedActor#preStart()
	 */
	public void preStart() throws Exception {
		super.preStart();
		
		if(configuration.getEsClusterNodes() == null || configuration.getEsClusterNodes().isEmpty())
			throw new RuntimeException("No elasticsearch cluster node configurations found");
		
		ImmutableSettings.Builder settingsBuilder = ImmutableSettings.settingsBuilder();
		if(configuration.getClientSettings() != null && !configuration.getClientSettings().isEmpty()) {
			for(String key : configuration.getClientSettings().keySet()) {
				if(StringUtils.isNotBlank(key)) {
					settingsBuilder.put(key, configuration.getClientSettings().get(key));
				}
			}
		}
		
		Settings settings = settingsBuilder.build();

		// create a new elasticsearch client and add transports according to provided host configurations
		this.esClient = new TransportClient(settings);
		
		for(TransportAddress hostConfig : configuration.getEsClusterNodes()) {
			if(hostConfig != null) 
				this.esClient.addTransportAddress(new InetSocketTransportAddress(hostConfig.getHost(), hostConfig.getPort()));
		}
		
	}

	/**
	 * @see com.mnxfst.stream.processing.StreamEventProcessingNode#processEvent(java.lang.Object)
	 */
	protected void processEvent(Object message) throws Exception {

		if(message instanceof StreamEventMessage) {
			StreamEventMessage msg = (StreamEventMessage)message;
			
			String esIndex = msg.getCustomAttributes().get(CUSTOM_ATTR_INDEX);
			if(StringUtils.isBlank(esIndex))
				esIndex = configuration.getEsIndex();
			String esDocumentType = msg.getCustomAttributes().get(CUSTOM_ATTR_DOC_TYPE);
			if(StringUtils.isBlank(esDocumentType))
				esDocumentType = configuration.getDocumentType();
			
//			System.out.println("Writing to: " + esIndex + "/" + esDocumentType);
			
			if(StringUtils.isNotBlank(msg.getContent()))
				this.esClient.prepareIndex(esIndex.toLowerCase(), esDocumentType.toLowerCase()).setSource(msg.getContent()).execute().actionGet();
			else 
				System.out.println(msg.getContent());
			// TODO error handling
		} else {
			unhandled(message);
		}
		
	}

}
