var com = com || {};
com.worksap = com.worksap || {};
com.worksap.elasticsearch = com.worksap.elasticsearch || {};

com.worksap.elasticsearch.FieldDataSelector = function() {
};

com.worksap.elasticsearch.FieldDataSelector.prototype.select = function (selected) {
	var $selected = $(selected);
	if (!$selected.hasClass("result-field-cell")
			|| $selected.hasClass("result-field-undefined")) {
		return;
	}
	$("#result-table-container").find(".result-field-cell-selected").removeClass("result-field-cell-selected");
	$selected.addClass("result-field-cell-selected");
	var field = $selected.attr("data-field");
	var docId = $selected.attr("id");
	$_IndexManager.analyze(docId, field);
};

