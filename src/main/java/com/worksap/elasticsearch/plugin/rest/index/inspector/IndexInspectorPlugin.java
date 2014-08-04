package com.worksap.elasticsearch.plugin.rest.index.inspector;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import org.elasticsearch.common.inject.Module;
import org.elasticsearch.plugins.AbstractPlugin;
import org.elasticsearch.rest.RestModule;

import com.worksap.elasticsearch.rest.index.inspector.DetailAnalyzeRestHandler;

public class IndexInspectorPlugin extends AbstractPlugin {
	@Override
	public String name() {
		return "index-inspector-plugin";
	}

	@Override
	public String description() {
		return "Elasticsearch index inspector plugin";
	}
	
    public void onModule(RestModule restModule) {
        restModule.addRestAction(DetailAnalyzeRestHandler.class);
    }
    
	@Override
    public Collection<Class<? extends Module>> modules() {
        Collection<Class<? extends Module>> modules = new ArrayList<>();
        modules.add(IndexInspectorModule.class);
        return Collections.unmodifiableCollection(modules);
    }
}
