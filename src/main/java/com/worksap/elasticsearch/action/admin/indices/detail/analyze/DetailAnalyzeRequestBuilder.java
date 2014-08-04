package com.worksap.elasticsearch.action.admin.indices.detail.analyze;

import org.elasticsearch.action.ActionListener;
import org.elasticsearch.action.support.single.custom.SingleCustomOperationRequestBuilder;
import org.elasticsearch.client.IndicesAdminClient;

public class DetailAnalyzeRequestBuilder
		extends
		SingleCustomOperationRequestBuilder<DetailAnalyzeRequest, DetailAnalyzeResponse, DetailAnalyzeRequestBuilder> {

	protected DetailAnalyzeRequestBuilder(IndicesAdminClient client,
			DetailAnalyzeRequest request) {
		super(client, request);
	}

	public DetailAnalyzeRequestBuilder(IndicesAdminClient client) {
		super(client, new DetailAnalyzeRequest());
	}

	@Override
	protected void doExecute(ActionListener<DetailAnalyzeResponse> listener) {
		client.execute(DetailAnalyzeAction.INSTANCE, request, listener);
	}
}
