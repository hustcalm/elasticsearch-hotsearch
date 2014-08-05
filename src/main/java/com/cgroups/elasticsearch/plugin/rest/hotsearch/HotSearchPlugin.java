package com.cgroups.elasticsearch.plugin.rest.hotsearch;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import org.elasticsearch.common.collect.Lists;
import org.elasticsearch.common.inject.Module;
import org.elasticsearch.plugins.AbstractPlugin;
import org.elasticsearch.rest.RestModule;

import com.cgroups.elasticsearch.rest.hotsearch.DetailAnalyzeRestHandler;

public class HotSearchPlugin extends AbstractPlugin {
	@Override
	public String name() {
		return "hotsearch-plugin";
	}

	@Override
	public String description() {
		return "Elasticsearch Hot Search plugin";
	}

	public void onModule(RestModule restModule) {
		restModule.addRestAction(DetailAnalyzeRestHandler.class);
	}

	@Override
	public Collection<Class<? extends Module>> modules() {
		/*
		 * Collection<Class<? extends
		 * com.sun.xml.internal.ws.api.server.Module>> modules = new
		 * ArrayList<>(); modules.add(HotSearchModule.class);
		 * modules.add(ExampleRestModule.class); return
		 * Collections.unmodifiableCollection(modules);
		 */

		Collection<Class<? extends Module>> modules = Lists.newArrayList();
		modules.add(SearchNowRestModule.class);
		modules.add(ExampleRestModule.class);
		modules.add(AllHotSearchRestModule.class);
		return modules;
	}
}
