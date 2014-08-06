package com.cgroups.elasticsearch.rest.hotsearch;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.elasticsearch.rest.*;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.bucket.terms.Terms.Bucket;


import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.inject.Inject;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.index.mapper.object.ObjectMapper;
import org.elasticsearch.index.query.FilterBuilders;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;

import static org.elasticsearch.rest.RestRequest.Method.GET;
import static org.elasticsearch.rest.RestStatus.OK;

import org.json.JSONObject;

public class AllHotSearchRestHandler extends BaseRestHandler  {

    @Inject
    public AllHotSearchRestHandler(Settings settings, Client client, RestController restController) {
        super(settings, client);
    	restController.registerHandler(GET, "/plugin_hotsearch/_allhotsearch", this);
    }


	@Override
	protected void handleRequest(RestRequest request,
			RestChannel channel, Client client) throws Exception {
		String time_range = request.param("time");
		String time_safe = (time_range != null) ? time_range : "5";
		
		/*
		channel.sendResponse(new BytesRestResponse(OK, "Hello, " + time_safe + "!"));
		
		if(time_range == null) {
			channel.sendResponse(new BytesRestResponse(OK, "Hello, please spcify the days that you want!"));
		}
		*/
		
        Date date = new Date();  
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");  
        String today = dateFormat.format(date);
        
        int days = Integer.parseInt(time_safe);
        int minusDays = -days;
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, minusDays);
        Date backDate = cal.getTime();    
        String backDay = dateFormat.format(backDate);
        
        /*
		channel.sendResponse(new BytesRestResponse(OK, "You are requesting " + time_safe + " days, ranging from " 
				+ today + " back to " + backDay + "..."));
		*/
        
        /*
        SearchResponse response = client.prepareSearch("index1", "index2")
		        .setTypes("type1", "type2")
		        .setSearchType(SearchType.DFS_QUERY_THEN_FETCH)
		        .setQuery(QueryBuilders.termQuery("multi", "test"))             // Query
		        .setPostFilter(FilterBuilders.rangeFilter("age").from(12).to(18))   // Filter
		        .setFrom(0).setSize(60).setExplain(true)
		        .execute()
		        .actionGet();
		        */
        
        /*
        QueryBuilder qb = QueryBuilders.matchQuery("name", "kimchy elasticsearch");

        SearchResponse response = client.prepareSearch("query_data_10k")
                .setTypes("query")
                .setSearchType(SearchType.DFS_QUERY_THEN_FETCH)
                .setQuery(qb)             // Query
                //.setPostFilter(FilterBuilders.rangeFilter("date").from(backDay).to(today))   // Filter
                .setFrom(0).setSize(60).setExplain(true)
                .execute()
                .actionGet();
        */
        
        /*
        SearchResponse response = client.prepareSearch().execute().actionGet();
        */
        
        SearchResponse response = client.prepareSearch("query_data_10k").setTypes("query")
                .setQuery(QueryBuilders.matchAllQuery())
                .addAggregation(AggregationBuilders.terms("keys").field("city").size(0).order(Terms.Order.count(false)))
                .execute().actionGet();

        Terms terms = response.getAggregations().get("keys");
        //Collection<Terms.Bucket> buckets = terms.getBuckets();
        
        Map<String, Map<String, Map<String, Object>>> ret_json = new HashMap<String, Map<String, Map<String, Object>>>();

        Map<String, Map<String, Object>> json = new HashMap<String, Map<String, Object>>();
        
        long totalDocs = 0;
        totalDocs = response.getHits().getTotalHits();
        
        for(Bucket b:terms.getBuckets()){
        	//System.out.println("filedname:"+b.getKey()+"     docCount:"+b.getDocCount());
        	//Key:city  getDocCount:doc numbers
        	String city = b.getKey();  // city name
        	long docNum = b.getDocCount(); // doc numbers
        	double population = (double)docNum/(double)totalDocs; // population among world
        	
            Map<String, Object> single_city = new HashMap<String, Object>();
            //single_city.put("Population", String.valueOf(population));
            single_city.put("Population", population);
            //single_city.put("Docs", docNum);
            //single_city.put("TotalDocs", totalDocs);
            single_city.put("hot_words", "test");
            
            json.put(city, single_city);
        }
        
        ret_json.put("Citys", json);
                
        //channel.sendResponse(new BytesRestResponse(OK, response.toString()));
        
        channel.sendResponse(new BytesRestResponse(OK, new JSONObject(ret_json).toString()));

	}
}