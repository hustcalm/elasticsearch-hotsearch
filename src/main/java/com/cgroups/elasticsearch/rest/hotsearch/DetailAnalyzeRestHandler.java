package com.cgroups.elasticsearch.rest.hotsearch;

import static org.elasticsearch.rest.RestRequest.Method.GET;
import static org.elasticsearch.rest.RestRequest.Method.POST;

import org.elasticsearch.ElasticsearchIllegalArgumentException;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.inject.Inject;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.rest.BaseRestHandler;
import org.elasticsearch.rest.RestChannel;
import org.elasticsearch.rest.RestController;
import org.elasticsearch.rest.RestRequest;
import org.elasticsearch.rest.action.support.RestToXContentListener;

import com.cgroups.elasticsearch.action.admin.indices.detail.analyze.DetailAnalyzeAction;
import com.cgroups.elasticsearch.action.admin.indices.detail.analyze.DetailAnalyzeRequest;
import com.cgroups.elasticsearch.action.admin.indices.detail.analyze.DetailAnalyzeResponse;

public class DetailAnalyzeRestHandler  extends BaseRestHandler {
	

	@Inject
	protected DetailAnalyzeRestHandler(Settings settings, Client client, RestController controller) {
		super(settings, client);
        controller.registerHandler(GET, "/{index}/_detail_analyze", this);
        controller.registerHandler(POST, "/{index}/_detail_analyze", this);
	}

	@Override
	protected void handleRequest(RestRequest request, RestChannel channel,
			Client client) throws Exception {
		String index = request.param("index");
		if (index == null) {
			throw new ElasticsearchIllegalArgumentException("index is missing");
		}
		
        String text = request.param("text");
        if (text == null && request.hasContent()) {
            text = request.content().toUtf8();
        }
        if (text == null) {
            throw new ElasticsearchIllegalArgumentException("text is missing");
        }

        DetailAnalyzeRequest analyzeRequest = new DetailAnalyzeRequest(index, text, request.param("analyzer"));
        analyzeRequest.listenerThreaded(false);
        client.admin().indices().execute(DetailAnalyzeAction.INSTANCE, analyzeRequest, new RestToXContentListener<DetailAnalyzeResponse>(channel));
	}
}
