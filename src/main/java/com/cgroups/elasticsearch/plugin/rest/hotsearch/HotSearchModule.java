package com.cgroups.elasticsearch.plugin.rest.hotsearch;

import org.elasticsearch.action.GenericAction;
import org.elasticsearch.action.support.TransportAction;
import org.elasticsearch.common.inject.AbstractModule;
import org.elasticsearch.common.inject.multibindings.MapBinder;

import com.cgroups.elasticsearch.action.admin.indices.detail.analyze.DetailAnalyzeAction;
import com.cgroups.elasticsearch.action.admin.indices.detail.analyze.TransportDetailAnalyzeAction;

public class HotSearchModule extends AbstractModule {

	@SuppressWarnings("rawtypes")
	@Override
	protected void configure() {
		bind(TransportDetailAnalyzeAction.class).asEagerSingleton();
		
        MapBinder<GenericAction, TransportAction> transportActionsBinder =
                MapBinder.newMapBinder(binder(), GenericAction.class, TransportAction.class);
        transportActionsBinder.addBinding(DetailAnalyzeAction.INSTANCE).to(TransportDetailAnalyzeAction.class).asEagerSingleton();
        
        MapBinder<String, GenericAction> actionsBinder = MapBinder.newMapBinder(binder(), String.class, GenericAction.class);
        actionsBinder.addBinding(DetailAnalyzeAction.NAME).toInstance(DetailAnalyzeAction.INSTANCE);
	}

}
