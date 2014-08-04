var com = com || {};
com.worksap = com.worksap || {};
com.worksap.elasticsearch = com.worksap.elasticsearch || {};

com.worksap.elasticsearch._docIdPrefix = "doc-id-";

com.worksap.elasticsearch.DocumentManager = function() {
	this.init();
};

com.worksap.elasticsearch.DocumentManager.prototype.init = function () {
	this._id_counter = 0;
	this._documents = [];
	this._fieldInfos = [];
};

com.worksap.elasticsearch.DocumentManager.prototype.load = function (indexTypeInfo) {
	var pathes = this._createSearchPathes(indexTypeInfo);
	var prom = null;
	for (var i = 0; i < pathes.length; i++) {
		var path = pathes[i];
		if (i == 0) {
			prom = this._createLoadRequest(path, true)();
			continue;
		}
		prom = prom.then(this._createLoadRequest(path));
	};
	return prom;
};

com.worksap.elasticsearch.DocumentManager._maxFetchSize = 100;
com.worksap.elasticsearch.DocumentManager._allQuery = JSON.stringify({
		"query" : {
			"match_all" : {}
		},
		"sort" : [ {
			"_index" : "asc"
		}, {
			"_type" : "asc"
		}, {
			"_id" : "asc"
		} ],
		"size": com.worksap.elasticsearch.DocumentManager._maxFetchSize
	});
com.worksap.elasticsearch.DocumentManager._scrollKeepTime = "10m";
com.worksap.elasticsearch.DocumentManager.prototype._createLoadRequest = function (path, init) {
	
	var self = this;
	
	var pathWithScroll = path + "?scroll=" + com.worksap.elasticsearch.DocumentManager._scrollKeepTime;
	return function () {
		var d = new $.Deferred;
		$.post(pathWithScroll, com.worksap.elasticsearch.DocumentManager._allQuery, "json").done(function (data, statusText, jqXHR) {
			if (data) {
				if (init) {
					self.init();
				}
				
				self._updateDocuments(self._extractHits(data));
				
				var scrollId = self._extractScrollId(data);
				if (scrollId) {
					var scrollPath = "/../../_search/scroll?scroll=" + com.worksap.elasticsearch.DocumentManager._scrollKeepTime;
					self._loadRemains(scrollPath, scrollId, d);
				} else {
					console.log("load, but cannot get scrollId: " + path);
					d.resolve();
				}
			} else {
				console.log("load nothing " + path);
				d.resolve();
			}
		});
		return d.promise();
	};
};

com.worksap.elasticsearch.DocumentManager.prototype._loadRemains = function (path, scrollId, defferred) {
	var self = this;
	console.log("self: " + self.getDocuments().length);
	console.log(self);
	
	$.post(path, scrollId, function (data, statusText, jqXHR) {
		var hits = self._extractHits(data);
		if (hits && hits.length > 0) {
			self._updateDocuments(hits);
			self._loadRemains(path, scrollId, defferred);
		} else {
			defferred.resolve();
		}
	}, "json");
};

com.worksap.elasticsearch.DocumentManager.prototype._extractHits = function (data) {
	var hits = [];
	if (data && data['hits'] && data['hits']['hits']) {
		hits = data['hits']['hits'];
	}
	return hits;
};

com.worksap.elasticsearch.DocumentManager.prototype._extractScrollId = function (data) {
	if (!data) {
		return null;
	}
	return data["_scroll_id"];
};

com.worksap.elasticsearch.DocumentManager.prototype._updateDocuments = function (hits) {
	for (var i = 0; i < hits.length; i++) {
		var hit = hits[i];
		hit[com.worksap.elasticsearch.Constants.ID_KEY] = com.worksap.elasticsearch._docIdPrefix + (this._id_counter++);
		this._documents.push(hit);
	}
};

com.worksap.elasticsearch.DocumentManager.prototype._createSearchPathes = function(indexTypeInfo) {
	if (indexTypeInfo.length < 1) {
		return [ "/../../_search" ];
	}
	var ret = [];
	for (var i = 0; i < indexTypeInfo.length; i++) {
		var index = indexTypeInfo[i];
		if (index.types.length < 1) {
			ret.push("/../../" + encodeURIComponent(index.index) + "/_search");
		} else {
			ret.push("/../../" + encodeURIComponent(index.index) + "/"
					+ com.worksap.elasticsearch.DocumentManager._joinWithUrlEncode(index.types)
					+ "/_search");
		}
	}
	return ret;
};

com.worksap.elasticsearch.DocumentManager._joinWithUrlEncode = function(ss, separator) {
	var sep = separator || ",";
	var ret = "";
	for (var i = 0; i < ss.length; i++) {
		if (i != 0) {
			ret += sep;
		}
		ret += encodeURIComponent(ss[i]);
	}
	return ret;
};

com.worksap.elasticsearch.DocumentManager.prototype.getDocuments = function() {
	return this._documents;
};

com.worksap.elasticsearch.DocumentManager.prototype.getDocument = function(docId) {
	if (!this._documents || this._documents.length < 1) {
		return null;
	}
	for (var i = 0; i < this._documents.length; i++) {
		var doc = this._documents[i];
		if (doc[com.worksap.elasticsearch.Constants.ID_KEY] == docId) {
			return doc;
		}
	}
	return null;
}

com.worksap.elasticsearch.DocumentManager.prototype.getFieldInfos = function() {
	if (this._fieldInfos.length < 1) {
		this._fieldInfos = com.worksap.elasticsearch.DocumentManager._collectFieldInfoList(this._documents);
	}
	return this._fieldInfos;
};

com.worksap.elasticsearch.DocumentManager._collectFieldInfoList = function (docs) {
	// TODO: support for nested objects and arrays
	if (!docs) {
		return [];
	}
	var collect = [];
	var tmp = {};
	for (var i = 0; i < docs.length; i++) {
		var doc = docs[i];
		if (doc['_source']) {
			$.each(doc['_source'], function (k, v) {
				if (!tmp[k]) {
					collect.push(k);
					tmp[k] = true;
				}
			});
		}
	}
	return collect;
};