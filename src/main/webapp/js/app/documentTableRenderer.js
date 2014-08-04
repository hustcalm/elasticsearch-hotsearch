var com = com || {};
com.worksap = com.worksap || {};
com.worksap.elasticsearch = com.worksap.elasticsearch || {};

com.worksap.elasticsearch.DocumentTableRenderer = function () {
};

com.worksap.elasticsearch.DocumentTableRenderer.prototype.render = function ($parent, fields, docs) {
	var table = $("<table />");
	var header = $("<thead />");
	var body = $("<tbody />");
	table.append(header);
	table.append(body);
	
	header.append(com.worksap.elasticsearch.DocumentTableRenderer._createHeader(fields));
	
	var rows = com.worksap.elasticsearch.DocumentTableRenderer._collectRows(fields, docs);
	
	for (var i = 0; i < rows.length; i++) {
		var row = rows[i];
		body.append(com.worksap.elasticsearch.DocumentTableRenderer._createRow(row));
	}
	
	$parent.empty();
	$parent.append(table);
};

com.worksap.elasticsearch.DocumentTableRenderer._createHeader = function(data) {
	var row = $("<tr />");
	for (var i = 0; i < com.worksap.elasticsearch.DocumentTableRenderer.commonField_.length; i++) {
		var common = com.worksap.elasticsearch.DocumentTableRenderer.commonField_[i];
		var cell = $("<th />");
		cell.text(common);
		row.append(cell);
	}
	for (var i = 0; i < data.length; i++) {
		var d = data[i];
		var cell = $("<th />");
		cell.text(d ? d : "");
		row.append(cell);
	}
	return row;
};

com.worksap.elasticsearch.DocumentTableRenderer.commonField_ = [ "_index", "_type", "_id" ];

com.worksap.elasticsearch.DocumentTableRenderer._createRow = function(data) {
	var row = $("<tr />");
	var tag = "td";
	for (var i = 0; i < data.length; i++) {
		var d = data[i];
		var attr = {"id" : d.id,
				    "data-field": d.field};
		if (i >= com.worksap.elasticsearch.DocumentTableRenderer.commonField_.length) {
			attr["class"] = "result-field-cell" + (d.value ? "" : " result-field-undefined");
		}
		
		var cell = $("<td />", attr);
		cell.text(d.value);
		row.append(cell);
	}
	return row;
};

com.worksap.elasticsearch.DocumentTableRenderer._collectRows = function (fields, docs) {
	// TODO: support for nested objects and arrays
	var ret = [];
	var commonFiledLen = com.worksap.elasticsearch.DocumentTableRenderer.commonField_.length;
	var totalFieldLen = commonFiledLen + fields.length;
	for (var i = 0; i < docs.length; i++) {
		var doc = docs[i];
			var row = new Array(totalFieldLen);
			for (var j = 0; j < commonFiledLen; j++) {
				row[j] = {"value": doc[com.worksap.elasticsearch.DocumentTableRenderer.commonField_[j]]};
			}
			var src = doc['_source'];
			if (src) {
				for (var j = 0; j < fields.length; j++) {
					row[j + commonFiledLen] = {"value": src[fields[j]],
							"id": doc[com.worksap.elasticsearch.Constants.ID_KEY],
							"field": fields[j]};
				}
			}
			ret.push(row);
	}
	return ret;
};