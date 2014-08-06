package com.cgroups.elasticsearch.rest.hotsearch;

import static org.elasticsearch.rest.RestRequest.Method.GET;
import static org.elasticsearch.rest.RestRequest.Method.POST;
import static org.elasticsearch.rest.RestStatus.OK;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.elasticsearch.rest.*;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.inject.Inject;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.index.query.FilterBuilders;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.rest.BaseRestHandler;
import org.elasticsearch.rest.RestChannel;
import org.elasticsearch.rest.RestController;
import org.elasticsearch.rest.RestHandler;
import org.elasticsearch.rest.RestRequest;
import org.elasticsearch.rest.RestResponse;
import org.elasticsearch.search.sort.*;
import org.json.JSONObject;

public class SearchNowHandler extends BaseRestHandler {

	@Inject
	protected SearchNowHandler(Settings settings, Client client,
			RestController controller) {
		super(settings, client);
		controller.registerHandler(GET, "/plugin_hotsearch/_searchnow", this);
		controller.registerHandler(POST, "/plugin_hotsearch/_searchnow", this);
	}

	@Override
	protected void handleRequest(RestRequest request, RestChannel channel,
			Client client) throws Exception {
		// String content =
		// "{\n    \"status\": \"ok\",\n    \"content\": {\n      \"location\": \"Tokyo\",\n      \"query\": \"find movie\"\n    }\n  }";
		// String who = request.param("who");
		// String whoSafe = (who != null) ? who : "world";
		Map<String, Object> res = action(client);
		Map<String, Object> jsonmap = new HashMap<String, Object>();
		jsonmap.put("city", res.get("city"));
		jsonmap.put("word", res.get("word"));
		jsonmap.put("date", res.get("date"));
		StringBuffer sb = new StringBuffer();
		for (Entry<String, Object> ent : res.entrySet()) {
			sb.append(ent.getKey() + " : " + ent.getValue());
		}
		channel.sendResponse(new BytesRestResponse(OK, new JSONObject(res)
				.toString()));

	}

	public static Map<String, Object> action(Client client) throws Exception {

		SearchResponse response = client.prepareSearch("query_data_10k")
				.setTypes("query")
				.setSearchType(SearchType.DFS_QUERY_THEN_FETCH)
				.setQuery(QueryBuilders.matchAllQuery())
				// Query
				.addSort(new FieldSortBuilder("date").order(SortOrder.DESC))
				.setFrom(0).setSize(1).setExplain(true).execute().actionGet();
		String id = response.getHits().getAt(0).getId();
		Map<String, Object> res = response.getHits().getAt(0).getSource();

		int s = (Integer) res.get("status");
		if (s == 0) {
			IndexResponse newresponse = client
					.prepareIndex("query_data_10k", "query", id).setSource(res)
					.execute().actionGet();
			return res;
		}
		return null;
	}

}
