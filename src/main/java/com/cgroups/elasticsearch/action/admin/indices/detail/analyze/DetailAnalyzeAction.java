package com.cgroups.elasticsearch.action.admin.indices.detail.analyze;


import org.elasticsearch.action.admin.indices.IndicesAction;
import org.elasticsearch.client.IndicesAdminClient;

public class DetailAnalyzeAction
		extends
		IndicesAction<DetailAnalyzeRequest, DetailAnalyzeResponse, DetailAnalyzeRequestBuilder> {

	public static final DetailAnalyzeAction INSTANCE = new DetailAnalyzeAction();
	public static final String NAME = "indices/detail_analyze";

	public DetailAnalyzeAction() {
		super(NAME);
	}

	@Override
	public DetailAnalyzeRequestBuilder newRequestBuilder(
			IndicesAdminClient client) {
		return new DetailAnalyzeRequestBuilder(client);
	}

	@Override
	public DetailAnalyzeResponse newResponse() {
		return new DetailAnalyzeResponse();
	}
}
