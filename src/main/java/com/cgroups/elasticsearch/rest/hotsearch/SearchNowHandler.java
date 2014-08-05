package com.cgroups.elasticsearch.rest.hotsearch;

import static org.elasticsearch.rest.RestRequest.Method.GET;
import static org.elasticsearch.rest.RestRequest.Method.POST;
import static org.elasticsearch.rest.RestStatus.OK;

import org.elasticsearch.rest.*;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.inject.Inject;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.rest.BaseRestHandler;
import org.elasticsearch.rest.RestChannel;
import org.elasticsearch.rest.RestController;
import org.elasticsearch.rest.RestHandler;
import org.elasticsearch.rest.RestRequest;
import org.elasticsearch.rest.RestResponse;

public class SearchNowHandler implements RestHandler {

	@Inject
	public SearchNowHandler(RestController restController) {
		restController.registerHandler(GET, "/plugin_hotsearch/_searchnow",
				this);
	}

	@Override
	public void handleRequest(final RestRequest request,
			final RestChannel channel) {

		String content = "{\n    \"status\": \"ok\",\n    \"content\": {\n      \"location\": \"Tokyo\",\n      \"query\": \"find movie\"\n    }\n  }";
		String who = request.param("who");
		String whoSafe = (who != null) ? who : "world";
		channel.sendResponse(new BytesRestResponse(OK, content));
	}
}
