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

import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.transport.InetSocketTransportAddress;

import com.mnxfst.stream.message.StreamEventMessage;
import com.mnxfst.stream.model.TransportAddress;
import com.mnxfst.stream.processing.AbstractStreamEventProcessingNode;

/**
 * Writes the {@link StreamEventMessage#getContent() content} of a {@link StreamEventMessage stream event} to a configured
 * elasticsearch destination. All relevant modifications to the json content must be applied by now as the writer simply
 * inserts the {@link StreamEventMessage#getContent() json content} into elastisearch.
 * @author mnxfst
 * @since 31.01.2014
 */
public class StreamEventESWriter extends AbstractStreamEventProcessingNode {

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
		
		// create a new elasticsearch client and add transports according to provided host configurations
//		this.esClient = new TransportClient();
//		
//		for(TransportAddress hostConfig : configuration.getEsClusterNodes()) {
//			if(hostConfig != null) 
//				this.esClient.addTransportAddress(new InetSocketTransportAddress(hostConfig.getHost(), hostConfig.getPort()));
//		}
		
	}



	/**
	 * @see akka.actor.UntypedActor#onReceive(java.lang.Object)
	 */
	public void onReceive(Object message) throws Exception {
			if(message instanceof StreamEventMessage) {
			StreamEventMessage msg = (StreamEventMessage)message;
//			if(StringUtils.isNotBlank(msg.getContent()))
//				this.esClient.prepareIndex(index, type).setSource(msg.getContent()).execute().actionGet();
////			else 
//				// TODO error handling
			System.out.println(msg.getContent());
		} else {
			unhandled(message);
		}
		
	}

}
