package com.cgroups.elasticsearch.rest.hotsearch;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.elasticsearch.rest.*;

import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.inject.Inject;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.index.query.FilterBuilders;
import org.elasticsearch.index.query.QueryBuilders;

import static org.elasticsearch.rest.RestRequest.Method.GET;
import static org.elasticsearch.rest.RestStatus.OK;

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
        
        SearchResponse response = client.prepareSearch("query_data_1k")
                .setTypes("query")
                .setSearchType(SearchType.DFS_QUERY_THEN_FETCH)
                .setQuery(QueryBuilders.termQuery("word", "World"))             // Query
                .setPostFilter(FilterBuilders.rangeFilter("date").from(backDay).to(today))   // Filter
                .setFrom(0).setSize(60).setExplain(true)
                .execute()
                .actionGet();
        channel.sendResponse(new BytesRestResponse(OK, response.toString()));
	}
}