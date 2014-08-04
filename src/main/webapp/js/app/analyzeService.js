var com = com || {};
com.worksap = com.worksap || {};
com.worksap.elasticsearch = com.worksap.elasticsearch || {};

com.worksap.elasticsearch.AnalyzeService = function(mappingManager,
		documentManager) {
	this._mappingManager = mappingManager;
	this._documentManager = documentManager;
};

com.worksap.elasticsearch.AnalyzeService.prototype.analyze = function(docId,
		field) {
	var doc = this._documentManager.getDocument(docId);
	if (!doc) {
		return;
	}
	var index = doc["_index"];
	var type = doc["_type"];
	var source = doc["_source"];
	if (!source || !source[field]) {
		return;
	}
	var text = source[field];
	var analyzer = this._getAnalyzer(index, type, field);

	var standardApi = "/../../" + encodeURIComponent(index) + "/_analyze?analyzer=" + encodeURIComponent(analyzer);
	var detailApi = "/../../" + encodeURIComponent(index) + "/_detail_analyze?analyzer=" + encodeURIComponent(analyzer);

	console.log("yyyyyyyyyyyy");
	console.log(doc);
	console.log(analyzer);

	var standardAnalyzedResult = null;
	var detailAnalyzeResult = null;
	return $.post(standardApi, text, "json").then(
			function(data, statusText, jqXHR) {
				var d = new $.Deferred;
				standardAnalyzedResult = data;
				d.resolve();
				return d.promise();
			}).then(function() {
				var d = new $.Deferred;
				$.post(detailApi, text, "json").done(
						function(data, statusText, jqXHR) {
							detailAnalyzeResult = data;
							console.log("debug");
							console.log(detailAnalyzeResult);
							d.resolve(standardAnalyzedResult,
									detailAnalyzeResult);
						});
				return d.promise();
			});
};

com.worksap.elasticsearch.AnalyzeService.defaultAnalyzer_ = "standard";
com.worksap.elasticsearch.AnalyzeService.prototype._getAnalyzer = function(
		index, type, field) {
	// TODO: もっとまじめに default analyzer を求める
	var mappingInfo = this._mappingManager.getMappingInfo();
	if (!mappingInfo) {
		return com.worksap.elasticsearch.AnalyzeService.defaultAnalyzer_;
	}
	var indexInfo = mappingInfo[index];
	if (!indexInfo) {
		return com.worksap.elasticsearch.AnalyzeService.defaultAnalyzer_;
	}
	var typeMappings = indexInfo["mappings"];
	if (!typeMappings) {
		return com.worksap.elasticsearch.AnalyzeService.defaultAnalyzer_;
	}
	var typeInfo = typeMappings[type];
	if (!typeInfo) {
		return com.worksap.elasticsearch.AnalyzeService.defaultAnalyzer_;
	}
	var properties = typeInfo["properties"];
	if (!properties) {
		return com.worksap.elasticsearch.AnalyzeService.defaultAnalyzer_;
	}
	var fieldMapping = properties[field];
	if (!fieldMapping) {
		return com.worksap.elasticsearch.AnalyzeService.defaultAnalyzer_;
	}
	var analyzer = fieldMapping["analyzer"];
	if (!analyzer) {
		return com.worksap.elasticsearch.AnalyzeService.defaultAnalyzer_;
	}
	return analyzer;
};
