/**
 * @fileoverview Top-level Manager for indices, types, documents, analyzers.
 */
var com = com || {};
com.worksap = com.worksap || {};
com.worksap.elasticsearch = com.worksap.elasticsearch || {};

/**
 * @global
 */
com.worksap.elasticsearch.IndexManager = function() {
	this._mappingManager = new com.worksap.elasticsearch.MappingManager();
	this._documentManager = new com.worksap.elasticsearch.DocumentManager();
	this._analyzerService = new com.worksap.elasticsearch.AnalyzeService(this._mappingManager, this._documentManager);
};

com.worksap.elasticsearch.IndexManager.prototype.load = function(indexIds, updatesMappings) {
	var self = this;
	this._doLoadAndUpdateMappings(updatesMappings).then(function () {
		var indexTypeInfo = self._collectIndexAndTypeData(indexIds || []);
		
		return self._documentManager.load(indexTypeInfo);
	}).then(function() {
		self._callDocumentsUpdateCallback();
	}).fail(function(e) {
		console.log("Failed to load documents: ");
		console.log(e);
	});
};
com.worksap.elasticsearch.IndexManager.prototype._doLoadAndUpdateMappings = function(updatesMappings) {
	if (updatesMappings) {
		var self = this;
		return this._mappingManager.load().then(function () {
			self._callMappingUpdateCallback();
		});
	}
	return $.Deferred().resolve();
}

com.worksap.elasticsearch.IndexManager.prototype._collectIndexAndTypeData = function(
		indexIds) {
	if (indexIds.length < 1) {
		return [];
	}
	var self = this;
	var ret = [];
	$.each(indexIds, function(i, indexIdInfo) {
		var index = self._mappingManager.findIndex(indexIdInfo["index"]);
		if (!index) {
			return true;
		}
		var types = [];
		$.each(indexIdInfo["types"], function(j, typeId) {
			var type = self._mappingManager.findType(typeId);
			if (type) {
				types.push(type);
			}
		});
		ret.push({
			"index" : index,
			"types" : types
		});
	})
	return ret;
}

com.worksap.elasticsearch.IndexManager.prototype.setMappingUpdateCallback = function(
		callback) {
	this._mappingUpdateCallback = callback;
};

com.worksap.elasticsearch.IndexManager.prototype._callMappingUpdateCallback = function() {
	if (this._mappingUpdateCallback) {
		this._mappingUpdateCallback(this._mappingManager.getMappingInfo());
	}
};

com.worksap.elasticsearch.IndexManager.prototype.setDocumentsUpdateCallback = function(
		callback) {
	this._documentsUpdateCallback = callback;
};

com.worksap.elasticsearch.IndexManager.prototype._callDocumentsUpdateCallback = function() {
	if (this._documentsUpdateCallback) {
		this._documentsUpdateCallback(this._documentManager.getFieldInfos(), this._documentManager.getDocuments());
	}
};

com.worksap.elasticsearch.IndexManager.prototype.setAnalyzedResultUpdateCallback = function (callback) {
	this._analyzedResultUpdateCallback = callback;
};

com.worksap.elasticsearch.IndexManager.prototype._callAnalyzedResultUpdateCallback = function(standardAnalyzedResult, detailAnalyzeResult) {
	if (this._analyzedResultUpdateCallback) {
		this._analyzedResultUpdateCallback(standardAnalyzedResult, detailAnalyzeResult);
	}
};

com.worksap.elasticsearch.IndexManager.prototype.analyze = function (docId, field) {
	var self = this;
	this._analyzerService.analyze(docId, field).then(function (standard, detail) {
		self._callAnalyzedResultUpdateCallback(standard, detail);
	});
};