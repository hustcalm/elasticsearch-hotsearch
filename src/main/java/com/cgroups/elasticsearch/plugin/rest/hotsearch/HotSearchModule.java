package com.cgroups.elasticsearch.plugin.rest.hotsearch;

import org.elasticsearch.action.GenericAction;
import org.elasticsearch.action.support.TransportAction;
import org.elasticsearch.common.inject.AbstractModule;
import org.elasticsearch.common.inject.multibindings.MapBinder;

import com.cgroups.elasticsearch.action.admin.indices.detail.analyze.DetailAnalyzeAction;
import com.cgroups.elasticsearch.action.admin.indices.detail.analyze.TransportDetailAnalyzeAction;
import com.cgroups.elasticsearch.rest.hotsearch.SearchNowHandler;

public class HotSearchModule extends AbstractModule {

	@SuppressWarnings("rawtypes")
	@Override
	protected void configure() {
		bind(SearchNowHandler.class).asEagerSingleton();
	}

}
