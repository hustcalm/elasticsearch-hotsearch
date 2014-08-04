/**
 * Page Initializer
 */
(function($) {
	// register global
	$_IndexManager = new com.worksap.elasticsearch.IndexManager();
	
	 $(document).ready(function() {
		 var analyzedResultRenderer = new com.worksap.elasticsearch.AnalyzedResultTableRenderer();
		 
		// Indexes-Types Menu
		var indexesTypesRenderer = new com.worksap.elasticsearch.IndexesTypesTreeRenderer();
		var indexesTypesSelector = new com.worksap.elasticsearch.IndexesTypesSelector(analyzedResultRenderer);
		var $indexSelectList = $("#index-select-list");
		$_IndexManager.setMappingUpdateCallback(function (mappingInfo) {
			indexesTypesRenderer.render($indexSelectList, mappingInfo);
			$("#index-select-list-tree").on("changed.jstree", function (e, data) {
				indexesTypesSelector.select();
			});
		});
		
		// Document search results pane
		var $resultTableContainer = $("#result-table-container");
		
		var fieldDataSelector = new com.worksap.elasticsearch.FieldDataSelector();
		$resultTableContainer.on("click", function (e) {
			fieldDataSelector.select(e.target);
		});
		
		var documentsRenderer = new com.worksap.elasticsearch.DocumentTableRenderer();
		$_IndexManager.setDocumentsUpdateCallback(function (fields, docs) {
			documentsRenderer.render($resultTableContainer, fields, docs);
		});
		
		$_IndexManager.setAnalyzedResultUpdateCallback(function (standard, detail) {
			analyzedResultRenderer.render(standard, detail);
		});
		
		$("#verbose-checkbox").on("change", com.worksap.elasticsearch.AnalyzedResultTableRenderer.showAnalyzedResults);
		 
		 $_IndexManager.load(null, true);
	 });
})(jQuery);