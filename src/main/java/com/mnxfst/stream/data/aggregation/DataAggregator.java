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
package com.mnxfst.stream.data.aggregation;

import java.util.HashSet;
import java.util.Set;

import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.facet.FacetBuilder;
import org.elasticsearch.search.facet.FacetBuilders;
import org.elasticsearch.search.facet.terms.TermsFacet;
import org.elasticsearch.search.facet.terms.TermsFacet.Entry;
import org.elasticsearch.search.fetch.FetchSubPhase.HitContext;

/**
 * Aggregates data .. obviously
 * @author mnxfst
 * @since Mar 10, 2014
 */
public class DataAggregator { // extends UntypedActor {

	private TransportClient elasticSearchTransportClient;
	private final String host;
	private final int port;
	private final String cluster;
	private final String index;
	private final String documentType;
	
	public DataAggregator(final String host, final int port, final String cluster, final String index, final String documentType) {		
		this.host = host;
		this.port = port;
		this.cluster = cluster;
		this.index = index;
		this.documentType = documentType;		
	}
	
	/**
	 * @see akka.actor.UntypedActor#preStart()
	 */
	public void preStart() throws Exception {
		
		// initialize elasticsearch client
		ImmutableSettings.Builder settingsBuilder = ImmutableSettings.settingsBuilder();
		settingsBuilder.put("cluster.name", cluster);
		Settings settings = settingsBuilder.build();
		this.elasticSearchTransportClient = new TransportClient(settings);
		this.elasticSearchTransportClient.addTransportAddress(new InetSocketTransportAddress(host, port));
	}

	/**
	 * @see akka.actor.UntypedActor#onReceive(java.lang.Object)
	 */
	public void onReceive(Object message) throws Exception {

		
	}
	
	public static void main(String[] args) {
		ImmutableSettings.Builder settingsBuilder = ImmutableSettings.settingsBuilder();
		settingsBuilder.put("cluster.name", "graylog2");
		Settings settings = settingsBuilder.build();
		TransportClient elasticSearchTransportClient;
		elasticSearchTransportClient = new TransportClient(settings);
		elasticSearchTransportClient.addTransportAddress(new InetSocketTransportAddress("localhost", 9300));

		QueryBuilder db = QueryBuilders.boolQuery()
//								.must(QueryBuilders.matchPhraseQuery("cc2", "md"));
								.must(QueryBuilders.matchPhraseQuery("cg_n", "Productdetailpage"));
		
		long s2 = System.currentTimeMillis();
		SearchResponse sr = elasticSearchTransportClient.prepareSearch("wterrors")
				.setTypes("error").setSearchType(SearchType.DFS_QUERY_THEN_FETCH)
//				.addFacet(FacetBuilders.termsFacet("f1").field("z_ecid"))
				.addFacet(FacetBuilders.termsFacet("f2").field("tz"))
				.setQuery(db)
				
				.setFrom(0).setSize(20000).execute().actionGet();
		long se = System.currentTimeMillis();
		Set<String> contentGroups = new HashSet<>();
		Set<String> companies = new HashSet<>();
		long s = System.currentTimeMillis();
		TermsFacet cgns = (TermsFacet)sr.getFacets().facetsAsMap().get("f2");
		for(Entry e : cgns.getEntries()) {
			contentGroups.add(e.getTerm().string());
		}
		
//		TermsFacet companiesFacet = (TermsFacet)sr.getFacets().facetsAsMap().get("f1");
//		for(Entry e : companiesFacet.getEntries()) {
//			companies.add(e.getTerm().string());
//		}
		long e1 = System.currentTimeMillis();
		for(String c : contentGroups)
			System.out.println(c);
		for(String c : companies)
			System.out.println(c);
		
//			
		long e2 = System.currentTimeMillis();
		System.out.println("hits: " + sr.getHits().hits().length + " of " +sr.getHits().getTotalHits() + " in " +sr.getTookInMillis()+"ms. Companies: "+ companies.size() + ", ContentGroups: " + contentGroups.size()+", Reading: "+ (e1-s)+"ms, showing: " + (e2-s)+"ms, Overall: " + (e2-s2)+"ms. Search: " + (se-s2)+"ms");
	}

}
