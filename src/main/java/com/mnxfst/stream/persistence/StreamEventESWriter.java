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
package com.mnxfst.stream.persistence;

import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.transport.InetSocketTransportAddress;

import akka.actor.UntypedActor;

import com.mnxfst.stream.message.StreamEventMessage;

/**
 * Writes the {@link StreamEventMessage#getContent() content} of a {@link StreamEventMessage stream event} to a configured
 * elasticsearch destination. All relevant modifications to the json content must be applied by now as the writer simply
 * inserts the {@link StreamEventMessage#getContent() json content} into elastisearch.
 * @author mnxfst
 * @since 31.01.2014
 */
public class StreamEventESWriter extends UntypedActor {

	/** client used for transporting content to elasticsearch server/cluster */
	private final TransportClient esClient;
	/** index to use as destination for inbound events */
	private final String index;
	/** message type to use for storing inbound events */
	private final String type;
	
	/**
	 * Initializes the stream event writer using the provided input 
	 * @param elasticSearchHosts
	 * @param index 
	 * @param type
	 */
	public StreamEventESWriter(Set<Pair<String, Integer>> elasticSearchHosts, final String index, final String type) {

		// create a new elasticsearch client and add transports according to provided host configurations
		this.esClient = new TransportClient();
		this.index = index;
		this.type = type;
		for(Pair<String, Integer> hostConfig : elasticSearchHosts) {
			if(hostConfig != null && StringUtils.isNotBlank(hostConfig.getLeft()) && hostConfig.getRight() != null && hostConfig.getRight().intValue() > 0) 
				this.esClient.addTransportAddress(new InetSocketTransportAddress(hostConfig.getLeft(), hostConfig.getRight()));
		}
			
	}
	
	/**
	 * @see akka.actor.UntypedActor#onReceive(java.lang.Object)
	 */
	public void onReceive(Object message) throws Exception {

		if(message instanceof StreamEventMessage) {
			StreamEventMessage msg = (StreamEventMessage)message;
			if(StringUtils.isNotBlank(msg.getContent()))
				this.esClient.prepareIndex(index, type).setSource(msg.getContent()).execute().actionGet();
//			else 
				// TODO error handling
		} else {
			unhandled(message);
		}
		
	}

}
