/**
 * @fileoverview Manager for indices, types, documents, analyzers.
 */
var com = com || {};
com.worksap = com.worksap || {};
com.worksap.elasticsearch = com.worksap.elasticsearch || {};

/**
 * @global
 */
com.worksap.elasticsearch.MappingManager = function() {
	this.init();
};

com.worksap.elasticsearch._indexIdPrefix = "index-id-";
com.worksap.elasticsearch._typeIdPrefix = "type-id-";

com.worksap.elasticsearch.MappingManager.prototype.init = function () {
	this._mapping = null;
	this._id_counter = 0;
}

com.worksap.elasticsearch.MappingManager.prototype.load = function () {
	var d = new $.Deferred;
	var self = this;
	$.getJSON("/../../_all/_mapping").done(function (data) {
//		console.log(data);
		if (data) {
			self._updateMappingInfo(data);
		}
		d.resolve();
	});
	return d.promise();
};

com.worksap.elasticsearch.MappingManager.prototype._updateMappingInfo = function (data) {
	this.init();
	this._annotateIds(data);
	this._mapping = data;
};

com.worksap.elasticsearch.MappingManager.prototype._annotateIds = function (data) {
	var self = this;
	$.each(data, function (index, mappings) {
		var indexId = com.worksap.elasticsearch._indexIdPrefix + (self._id_counter++);
		mappings[com.worksap.elasticsearch.Constants.ID_KEY] = indexId;
		if (mappings["mappings"]) {
			$.each(mappings["mappings"], function (type, mapping) {
				var typeId = com.worksap.elasticsearch._typeIdPrefix + (self._id_counter++);
				mapping[com.worksap.elasticsearch.Constants.ID_KEY] = typeId;
			});
		}
	});
};

com.worksap.elasticsearch.MappingManager.prototype.getMappingInfo = function () {
	// TODO: should return cloned
	return this._mapping;
}

com.worksap.elasticsearch.MappingManager.prototype.findIndex = function(id) {
	if (!this._mapping) {
		return null;
	}
	var ret = null;
	$.each(this._mapping, function(index, mappings) {
		if (mappings[com.worksap.elasticsearch.Constants.ID_KEY] == id) {
			ret = index;
			return false;
		}
	});
	return ret;
};

com.worksap.elasticsearch.MappingManager.prototype.findType = function (id) {
	if (!this._mapping) {
		return null;
	}
	var ret = null;
	$.each(this._mapping, function(index, mappings) {
		if (mappings["mappings"]) {
			$.each(mappings["mappings"], function (type, mapping) {
				if (mapping[com.worksap.elasticsearch.Constants.ID_KEY] == id) {
					ret = type;
					return false;
				}
			});
		}
		if (ret != null) {
			return false;
		}
	});
	return ret;
};
