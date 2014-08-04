var com = com || {};
com.worksap = com.worksap || {};
com.worksap.elasticsearch = com.worksap.elasticsearch || {};

com.worksap.elasticsearch.IndexesTypesTreeRenderer = function() {
};

com.worksap.elasticsearch.IndexesTypesTreeRenderer.prototype._convert = function (mappingInfo) {
	var indices = [];
	var root = {
			"text": "All",
			"id": "index-type-all-select-node",
			"state" : {
				"opened" : true
			},
			"children" : indices,
	};
	$.each(mappingInfo, function (index, mappings) {
		var indexNode = {};
		indexNode["id"] = mappings[com.worksap.elasticsearch.Constants.ID_KEY];
		indexNode["text"] = index;
		indexNode["state"] = {"state": {"opend": false}};
		var types = [];
		indexNode["children"] = types;
		$.each(mappings["mappings"], function (type, mapping) {
			var typeNode = {};
			typeNode["id"] = mapping[com.worksap.elasticsearch.Constants.ID_KEY];
			typeNode["text"] = type;
			indexNode["state"] = {"state": {"opend": false}};
			types.push(typeNode);
		});
		indices.push(indexNode);
	});
	
	return [root];
};

com.worksap.elasticsearch.IndexesTypesTreeRenderer.prototype.render = function ($parent, mappingInfo) {
	$parent.empty();
	var $treeElement = $("<div />", {
		id: "index-select-list-tree"
	});
	$parent.append($treeElement);
	
	var data = this._convert(mappingInfo);
	$treeElement.jstree({
		"core": {
			"data": data,
		},
		"plugins": ["checkbox"]
	});
};