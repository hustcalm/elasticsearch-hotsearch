package com.worksap.elasticsearch.plugin.rest.index.inspector;

import org.elasticsearch.action.GenericAction;
import org.elasticsearch.action.support.TransportAction;
import org.elasticsearch.common.inject.AbstractModule;
import org.elasticsearch.common.inject.multibindings.MapBinder;

import com.worksap.elasticsearch.action.admin.indices.detail.analyze.DetailAnalyzeAction;
import com.worksap.elasticsearch.action.admin.indices.detail.analyze.TransportDetailAnalyzeAction;

public class IndexInspectorModule extends AbstractModule {

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
