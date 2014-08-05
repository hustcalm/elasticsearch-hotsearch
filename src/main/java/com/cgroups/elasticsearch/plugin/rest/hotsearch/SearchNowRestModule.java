package com.cgroups.elasticsearch.plugin.rest.hotsearch;

import org.elasticsearch.common.inject.AbstractModule;

import com.cgroups.elasticsearch.rest.hotsearch.SearchNowHandler;

public class SearchNowRestModule extends AbstractModule {

	@Override
	protected void configure() {
		bind(SearchNowHandler.class).asEagerSingleton();
	}

}
