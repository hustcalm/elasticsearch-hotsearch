var com = com || {};
com.worksap = com.worksap || {};
com.worksap.elasticsearch = com.worksap.elasticsearch || {};

com.worksap.elasticsearch.IndexesTypesSelector = function(analyzedResultRenderer) {
	this._analyzedResultRenderer = analyzedResultRenderer;
};

com.worksap.elasticsearch.IndexesTypesSelector.prototype._collectSelectedIndicesOrTypes = function () {
	var all = $("#index-type-all-select-node");
	var allValue = all.children(".jstree-anchor");
	if (allValue && allValue.hasClass("jstree-clicked")) {
		return [];
	}
	
	var ret = [];
	
	var indices = all.children(".jstree-children");
	if (!indices) {
		return [];
	}
	indices.children("li").each(function () {
		var index = $(this);
		var indexlValue = index.children(".jstree-anchor");
		if (!indexlValue) {
			return true;
		}
		if (indexlValue.hasClass("jstree-clicked")) {
			ret.push({
				"index" : index.attr("id"),
				"types" : []
			});
			return true;
		}
		
		var typeIds = [];
		var types = index.children(".jstree-children");
		if (!types) {
			return true;
		}
		types.children("li").each(function(){
			var type = $(this);
			var typeValue = type.children(".jstree-anchor");
			if (!typeValue) {
				return true;
			}
			if (typeValue.hasClass("jstree-clicked")) {
				typeIds.push(type.attr("id"));
			}
		});
		
		if (typeIds.length > 0) {
			ret.push({
				"index" : index.attr("id"),
				"types" : typeIds
			});
		}
	});
	return ret;
};

com.worksap.elasticsearch.IndexesTypesSelector.prototype.select = function () {
	var selectedIndicesOrTypes = this._collectSelectedIndicesOrTypes();
	this._analyzedResultRenderer.clear();
	$_IndexManager.load(selectedIndicesOrTypes);
};