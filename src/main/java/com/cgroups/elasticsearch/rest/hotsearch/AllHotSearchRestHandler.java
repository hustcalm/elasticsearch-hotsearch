package com.cgroups.elasticsearch.rest.hotsearch;

import org.elasticsearch.rest.*;

import org.elasticsearch.common.inject.Inject;

import static org.elasticsearch.rest.RestRequest.Method.GET;
import static org.elasticsearch.rest.RestStatus.OK;

public class AllHotSearchRestHandler implements RestHandler {

    @Inject
    public AllHotSearchRestHandler(RestController restController) {
        restController.registerHandler(GET, "/plugin_hotsearch/_allhotsearch", this);
    }


	@Override
	public void handleRequest(final RestRequest request,
			final RestChannel channel) {
		String time_range = request.param("time");
		String time_safe = (time_range != null) ? time_range : "5";
		channel.sendResponse(new BytesRestResponse(OK, "Hello, " + time_safe
				+ "!"));
	}
}