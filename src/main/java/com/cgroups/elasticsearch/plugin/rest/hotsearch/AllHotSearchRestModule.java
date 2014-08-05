package com.cgroups.elasticsearch.plugin.rest.hotsearch;

import org.elasticsearch.common.inject.AbstractModule;

import com.cgroups.elasticsearch.rest.hotsearch.AllHotSearchRestHandler;

public class AllHotSearchRestModule extends AbstractModule {
	@Override
    protected void configure() {
        bind(AllHotSearchRestHandler.class).asEagerSingleton();
    }
}
