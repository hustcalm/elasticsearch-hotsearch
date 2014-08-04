var com = com || {};
com.worksap = com.worksap || {};
com.worksap.elasticsearch = com.worksap.elasticsearch || {};

com.worksap.elasticsearch.AnalyzedResultTableRenderer = function() {
};

com.worksap.elasticsearch.AnalyzedResultTableRenderer.prototype.render = function(standardResult, detailResult) {
	this.clear();
	
	var standardContainer = $("#result-analyze-table-container");
	this.setAnalyzedResults_(standardContainer, standardResult);
	
	var detailContainer = $("#result-detail-analyze-table-container");
	this.setDetailAnalyzedResults_(detailContainer, detailResult);

	this.showAnalyzedResults_();
};

com.worksap.elasticsearch.AnalyzedResultTableRenderer.prototype.clear = function () {
	$("#result-analyze-table-container").empty();
	$("#result-detail-analyze-table-container").empty();
	$("#result-analyze-container").hide();
}

com.worksap.elasticsearch.AnalyzedResultTableRenderer.commonAnalyzedViewField_ = ["Position", "Token", "Offset", "Type"];

com.worksap.elasticsearch.AnalyzedResultTableRenderer.prototype.setAnalyzedResults_ = function (container, analyzedResult) {
	var tokens = analyzedResult.tokens || [];
	
	this.setTokens(tokens, container);
	
	this.showAnalyzedResults_();
};

com.worksap.elasticsearch.AnalyzedResultTableRenderer.prototype.setTokens = function(tokens, containerElement, info) {
	var header = this.createInfoTitle_(info);
	var $container = $(containerElement);
	if (header) {
		$container.append(header);
	}
	var data = this.formatTokens_(tokens);
	var table = this.createAnalyzedResultTable_(data);
	$container.append(table);
};

com.worksap.elasticsearch.AnalyzedResultTableRenderer.prototype.setCharFilterResult_ = function(text, containerElement, info) {
	var header = this.createInfoTitle_(info);
	var $container = $(containerElement);
	if (header) {
		$container.append(header);
	}
	
	var attr = {"class": "result-table-vertical single"};
	var table = $("<table />", attr);
	var field = "Result";
	var data = {};
	data[field] = text ? text : "";
	var tableRow = this.createAnalyzedResultRow_(field, [data]);
	table.append(tableRow);
	$container.append(table);
};

com.worksap.elasticsearch.AnalyzedResultTableRenderer.prototype.createInfoTitle_ = function(info) {
	if (!info) {
		return null;
	}
	var title = $("<h3 />", {"class": "analyzer-part-header"});
	title.text(info);
	return title;
};

com.worksap.elasticsearch.AnalyzedResultTableRenderer.prototype.setDetailAnalyzedResults_ = function (container, analyzeDetailResult) {
	if (!analyzeDetailResult) {
		return;
	}
	
	// charfilters
	var charfilters = analyzeDetailResult["charfilters"];
	var self = this;
	if (charfilters) {
		for (var i = 0; i < charfilters.length; i++) {
			var charfilter = charfilters[i];
			var info = com.worksap.elasticsearch.AnalyzedResultTableRenderer.makeSafe(charfilter["name"]) + " (charfilter)"
			self.setCharFilterResult_(charfilter["filterd_text"],
					container, info);
		}
	}
	
	// tokenizer
	var tokenizer = analyzeDetailResult["tokenizer"];
	if (tokenizer) {
		$.each(tokenizer, function (name, tokens) {
			var info = (name ? name : "unknown") + "(tokenizer)";
			self.setTokens(tokens, container, info);
		});
	}
	
	// tokenfilters
	var tokenfilters = analyzeDetailResult["tokenfilters"];
	if (tokenfilters) {
		for (var i = 0; i < tokenfilters.length; i++) {
			var tokenfilter = tokenfilters[i];
			$.each(tokenfilter, function (name, tokens) {
				var info = (name ? name : "unknown")+ "(tokenfilter)";
				self.setTokens(tokens, container, info);
			});
		}
	}
	
	this.showAnalyzedResults_();
};

com.worksap.elasticsearch.AnalyzedResultTableRenderer.prototype.showAnalyzedResults_ = function () {
	com.worksap.elasticsearch.AnalyzedResultTableRenderer.showAnalyzedResults();
};

com.worksap.elasticsearch.AnalyzedResultTableRenderer.showAnalyzedResults = function () {
	var verbose = $("#verbose-checkbox").prop('checked');
	var simpleResult = $("#result-analyze-table-container");
	var detailResult = $("#result-detail-analyze-table-container");
	if (verbose) {
		simpleResult.hide();
		detailResult.show();
	} else {
		detailResult.hide();
		simpleResult.show();
	}
	$("#result-analyze-container").show();
};

com.worksap.elasticsearch.AnalyzedResultTableRenderer.prototype.createAnalyzedResultTable_ = function (data) {
	var table = $("<table />", {"class": "result-table-vertical"});
	
	for (var i = 0; i < com.worksap.elasticsearch.AnalyzedResultTableRenderer.commonAnalyzedViewField_.length; i++) {
		var field = com.worksap.elasticsearch.AnalyzedResultTableRenderer.commonAnalyzedViewField_[i];
		var tableRow = this.createAnalyzedResultRow_(field, data);
		table.append(tableRow);
	}
	
	var extendAttrFields = this.collectExtendAttrFields_(data);
	for (var i = 0; i < extendAttrFields.length; i++) {
		var field = extendAttrFields[i];
		var tableRow = this.createAnalyzedResultRow_(field, data);
		table.append(tableRow);
	}
	
	return table;
};

com.worksap.elasticsearch.AnalyzedResultTableRenderer.prototype.collectExtendAttrFields_ = function(data){
	var ret = [];
	for (var i = 0; i < data.length; i++) {
		var d = data[i];
		var fields = [];
		$.each(d, function (key, val) {
			if ($.inArray(key, com.worksap.elasticsearch.AnalyzedResultTableRenderer.commonAnalyzedViewField_) < 0) {
				fields.push(key);
			}
		});
		ret = ret.concat(fields);
	}
	return $.unique(ret).sort();
};

com.worksap.elasticsearch.AnalyzedResultTableRenderer.prototype.createAnalyzedResultRow_ = function (field, data) {
	var tr = $("<tr />");
	var th = $("<th />");
	th.text(field);
	tr.append(th);
	for (var i = 0; i < data.length; i++) {
		var value = data[i][field];
		value = value || "";
		var td = $("<td />");
		td.text(value);
		tr.append(td);
	}
	return tr;
};

com.worksap.elasticsearch.AnalyzedResultTableRenderer.prototype.formatTokens_ = function (tokens) {
	var ret = [];
	for (var i = 0; i < tokens.length; i++) {
		var token = tokens[i];
		var formatted = {
				"Position": token.position,
				"Token": token.token,
				"Offset": com.worksap.elasticsearch.AnalyzedResultTableRenderer.makeSafe(token.start_offset)
				+ "-" + com.worksap.elasticsearch.AnalyzedResultTableRenderer.makeSafe(token.end_offset),
				"Type": token.type,
		};
		var extended = token["extended_attributes"];
		if (extended) {
			$.each(extended, function (attrKlass, attrValues){
				var splits = attrKlass.split(".");
				var simpleKlassName = splits.length < 1 ? "" : splits[splits.length - 1];
				$.each(attrValues, function (k, v){
					formatted[simpleKlassName + " " + k] = v;
				});
			});
		}
		ret.push(formatted);
	}
	return ret;
};
com.worksap.elasticsearch.AnalyzedResultTableRenderer.makeSafe = function (s) {
	if (s == null) {
		return ""
	}
	return new String(s);
}