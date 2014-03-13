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
package com.mnxfst.stream.pipeline.element.es;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;

import com.mnxfst.stream.message.StreamEventMessage;
import com.mnxfst.stream.pipeline.PipelineElement;
import com.mnxfst.stream.pipeline.config.PipelineElementConfiguration;
import com.mnxfst.stream.pipeline.message.PipelineElementSetupFailedMessage;

/**
 * Writers inbound messages to elasticsearch instance
 * @author mnxfst
 * @since 05.03.2014
 *
 */
public class ElasticSearchWriterPipelineElement extends PipelineElement {

	public static final String ES_CLUSTER_NAME = "eswriter.cluster.name";
	public static final String ES_HOST_PREFIX = "eswriter.host."; 
	public static final String ES_PORT_PREFIX = "eswriter.port.";
	public static final String ES_INDEX = "eswriter.index";
	public static final String ES_DOCUMENT_TYPE = "eswriter.document.type";
	public static final String ES_WRITE_EVENT_ONLY = "eswriter.document.writeEventOnly";
	
	private TransportClient elasticSearchTransportClient;
	private String indexName;
	private String documentType;	
	private boolean isWriteEventOnly = false;

	/**
	 * Initializes the instance using the provided configuration
	 * @param pipelineElementConfiguration
	 */
	public ElasticSearchWriterPipelineElement(PipelineElementConfiguration pipelineElementConfiguration) {
		super(pipelineElementConfiguration);
	}
	
	/**
	 * @see akka.actor.UntypedActor#preStart()
	 */
	public void preStart() throws Exception {
		super.preStart();
		
		// fetch and validate index
		this.indexName = getStringProperty(ES_INDEX);
		if(StringUtils.isBlank(this.indexName)) {
			context().parent().tell(new PipelineElementSetupFailedMessage(
					getPipelineElementConfiguration().getPipelineId(), getPipelineElementConfiguration().getElementId(),
					PipelineElementSetupFailedMessage.GENERAL, "Required index name missing"), getSelf());
			return;
		}
		
		// fetch and validate document type
		this.documentType = getStringProperty(ES_DOCUMENT_TYPE);
		if(StringUtils.isBlank(this.documentType)) {
			context().parent().tell(new PipelineElementSetupFailedMessage(
					getPipelineElementConfiguration().getPipelineId(), getPipelineElementConfiguration().getElementId(),
					PipelineElementSetupFailedMessage.GENERAL, "Required document type missing"), getSelf());
			return;
		}

		// fetch and validate cluster name
		String clusterName = getStringProperty(ES_CLUSTER_NAME);
		if(StringUtils.isBlank(clusterName)) {
			context().parent().tell(new PipelineElementSetupFailedMessage(
					getPipelineElementConfiguration().getPipelineId(), getPipelineElementConfiguration().getElementId(),
					PipelineElementSetupFailedMessage.GENERAL, "Required cluster name missing"), getSelf());
			return;
		}
		
		this.isWriteEventOnly = getBooleanProperty(ES_WRITE_EVENT_ONLY, false);

		// fetch all transport addresses
		Map<String, Integer> transportAddressSettings = new HashMap<>();
		for(int i = 0; i < Integer.MAX_VALUE; i++) {
			String host = getStringProperty(ES_HOST_PREFIX + i);
			int port = getIntProperty(ES_PORT_PREFIX + i, -1);
			
			if(StringUtils.isNotBlank(host) && port > 0) {
				transportAddressSettings.put(host, Integer.valueOf(port));
			} else {
				break;
			}
		}
		
		// validate host
		if(transportAddressSettings.isEmpty()) {
			context().parent().tell(new PipelineElementSetupFailedMessage(
					getPipelineElementConfiguration().getPipelineId(), getPipelineElementConfiguration().getElementId(),
					PipelineElementSetupFailedMessage.GENERAL, "Required host / port settings missing"), getSelf());
			return;
		}
		
		// initialize elasticsearch client
		ImmutableSettings.Builder settingsBuilder = ImmutableSettings.settingsBuilder();
		settingsBuilder.put("cluster.name", clusterName);
		Settings settings = settingsBuilder.build();
		this.elasticSearchTransportClient = new TransportClient(settings);
		for(String host : transportAddressSettings.keySet()) {
			Integer port = transportAddressSettings.get(host);
			this.elasticSearchTransportClient.addTransportAddress(new InetSocketTransportAddress(host, port));
			context().system().log().info("elasticsearch client init [pipeline="+getPipelineElementConfiguration().getPipelineId()+", element="+getPipelineElementConfiguration().getElementId()+", cluster="+clusterName+", host="+host+", port="+port+"]");		
		}
		
	}



	/**
	 * @see com.mnxfst.stream.pipeline.PipelineElement#processEvent(com.mnxfst.stream.message.StreamEventMessage)
	 */
	protected void processEvent(StreamEventMessage message) throws Exception {
		if(isWriteEventOnly) {
			this.elasticSearchTransportClient.prepareIndex(indexName.toLowerCase(), documentType.toLowerCase()).setSource(message.getEvent()).execute().actionGet(); 
		} else {
			this.elasticSearchTransportClient.prepareIndex(indexName.toLowerCase(), documentType.toLowerCase()).setSource(message).execute().actionGet();
		}		
	}

}
