package com.cgroups.elasticsearch.plugin.rest.hotsearch;

import org.elasticsearch.common.inject.AbstractModule;

import com.cgroups.elasticsearch.rest.hotsearch.HelloRestHandler;

public class ExampleRestModule extends AbstractModule {
	@Override
    protected void configure() {
        bind(HelloRestHandler.class).asEagerSingleton();
    }
}
