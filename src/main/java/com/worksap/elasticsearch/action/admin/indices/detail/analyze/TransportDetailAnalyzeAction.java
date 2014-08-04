package com.worksap.elasticsearch.action.admin.indices.detail.analyze;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.OffsetAttribute;
import org.apache.lucene.analysis.tokenattributes.PositionIncrementAttribute;
import org.apache.lucene.analysis.tokenattributes.TypeAttribute;
import org.apache.lucene.util.Attribute;
import org.apache.lucene.util.AttributeReflector;
import org.apache.lucene.util.BytesRef;
import org.elasticsearch.ElasticsearchException;
import org.elasticsearch.ElasticsearchIllegalArgumentException;
import org.elasticsearch.action.support.single.custom.TransportSingleCustomOperationAction;
import org.elasticsearch.cluster.ClusterService;
import org.elasticsearch.cluster.ClusterState;
import org.elasticsearch.cluster.block.ClusterBlockException;
import org.elasticsearch.cluster.block.ClusterBlockLevel;
import org.elasticsearch.cluster.routing.ShardsIterator;
import org.elasticsearch.common.inject.Inject;
import org.elasticsearch.common.lucene.Lucene;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.index.analysis.CharFilterFactory;
import org.elasticsearch.index.analysis.CustomAnalyzer;
import org.elasticsearch.index.analysis.NamedAnalyzer;
import org.elasticsearch.index.analysis.TokenFilterFactory;
import org.elasticsearch.index.analysis.TokenizerFactory;
import org.elasticsearch.index.mapper.internal.AllFieldMapper;
import org.elasticsearch.index.service.IndexService;
import org.elasticsearch.indices.IndicesService;
import org.elasticsearch.indices.analysis.IndicesAnalysisService;
import org.elasticsearch.threadpool.ThreadPool;
import org.elasticsearch.transport.TransportService;

public class TransportDetailAnalyzeAction
		extends
		TransportSingleCustomOperationAction<DetailAnalyzeRequest, DetailAnalyzeResponse> {

	private final IndicesService indicesService;
	private final IndicesAnalysisService indicesAnalysisService;

	@Inject
	public TransportDetailAnalyzeAction(Settings settings,
			ThreadPool threadPool, ClusterService clusterService,
			TransportService transportService, IndicesService indicesService,
			IndicesAnalysisService indicesAnalysisService) {
		super(settings, DetailAnalyzeAction.NAME, threadPool, clusterService,
				transportService);
		this.indicesService = indicesService;
		this.indicesAnalysisService = indicesAnalysisService;
	}

	@Override
	protected String executor() {
		return ThreadPool.Names.INDEX;
	}

	@Override
	protected ShardsIterator shards(ClusterState state,
			DetailAnalyzeRequest request) {
		if (request.getIndex() == null) {
			return null;
		}
		return state.routingTable().index(request.getIndex())
				.randomAllActiveShardsIt();
	}

	@Override
	protected DetailAnalyzeRequest newRequest() {
		return new DetailAnalyzeRequest();
	}

	@Override
	protected DetailAnalyzeResponse newResponse() {
		return new DetailAnalyzeResponse();
	}

	@Override
	protected ClusterBlockException checkGlobalBlock(ClusterState state,
			DetailAnalyzeRequest request) {
		return state.blocks().globalBlockedException(ClusterBlockLevel.READ);
	}

	@Override
	protected ClusterBlockException checkRequestBlock(ClusterState state,
			DetailAnalyzeRequest request) {
		if (request.getIndex() != null) {
			request.setIndex(state.metaData().concreteSingleIndex(
					request.getIndex()));
			return state.blocks().indexBlockedException(ClusterBlockLevel.READ,
					request.getIndex());
		}
		return null;
	}

	@Override
	protected DetailAnalyzeResponse shardOperation(
			DetailAnalyzeRequest request, int shardId)
			throws ElasticsearchException {
		IndexService indexService = getIndexService(request.getIndex());
		Analyzer analyzer = findAnalyzer(request.getAnalyzer(), indexService);
		if (analyzer == null) {
			throw new ElasticsearchIllegalArgumentException(
					"failed to find analyzer");
		}

		DetailAnalyzeResponse response = buildResponse(request, analyzer,
				getDefaultFieldName(indexService));

		return response;
	}

	private IndexService getIndexService(String indexName) {
		IndexService indexService = null;
		if (indexName != null) {
			indexService = indicesService.indexServiceSafe(indexName);
		}
		return indexService;
	}

	private String getDefaultFieldName(IndexService indexService) {
		return indexService != null ? indexService.queryParserService()
				.defaultField() : AllFieldMapper.NAME;
	}

	private Analyzer findAnalyzer(String analyzerName, IndexService indexService) {
		Analyzer analyzer = null;
		if (analyzerName != null) {
			if (indexService == null) {
				analyzer = indicesAnalysisService.analyzer(analyzerName);
			} else {
				analyzer = indexService.analysisService()
						.analyzer(analyzerName);
			}
			if (analyzer == null) {
				throw new ElasticsearchIllegalArgumentException(
						"failed to find analyzer [" + analyzerName + "]");
			}
		} else {
			if (indexService == null) {
				analyzer = Lucene.STANDARD_ANALYZER;
			} else {
				analyzer = indexService.analysisService()
						.defaultIndexAnalyzer();
			}
		}
		return analyzer;
	}

	private DetailAnalyzeResponse buildResponse(DetailAnalyzeRequest request,
			Analyzer analyzer, String field) {
		CustomAnalyzer customAnalyzer = findCustomAnalyzer(analyzer);
		if (customAnalyzer != null) {
			return buildResponseForCustomAnalyzer(request, customAnalyzer,
					field);
		}
		return buildResponseForNonCustomAnalyzer(request, analyzer, field);
	}

	private DetailAnalyzeResponse buildResponseForCustomAnalyzer(
			DetailAnalyzeRequest request, CustomAnalyzer customAnalyzer,
			String field) {

		try {
			DetailAnalyzeResponse response = new DetailAnalyzeResponse();

			// customAnalyzer = divide chafilter, tokenizer tokenfilters
			CharFilterFactory[] charfilters = customAnalyzer.charFilters();
			TokenizerFactory tokenizer = customAnalyzer.tokenizerFactory();
			TokenFilterFactory[] tokenfilters = customAnalyzer.tokenFilters();

			String source = request.getText();
			if (charfilters != null) {
				for (CharFilterFactory charfilter : charfilters) {
					source = applyCharfilter(source, charfilter);
					response.addCharfilter(new DetailAnalyzeResponse.CharFilteredText(
							charfilter.name(), source));
				}
			}

			try (TokenStream stream = tokenizer.create(new StringReader(source))) {
				response.setTokenizer(new DetailAnalyzeResponse.DetailAnalyzeTokenList(
						tokenizer.name(), processAnalysis(stream)));
			}

			if (tokenfilters != null) {
				for (int i = 0; i < tokenfilters.length; i++) {
					List<DetailAnalyzeResponse.DetailAnalyzeToken> analyzed = applyTokenizerAndTokenFiltersUntil(
							source, tokenizer, tokenfilters, i + 1);

					response.addTokenfilter(new DetailAnalyzeResponse.DetailAnalyzeTokenList(
							tokenfilters[i].name(), analyzed));

				}
			}

			return response;
		} catch (IOException e) {
			throw new ElasticsearchException("failed to analyze", e);
		}
	}

	private String applyCharfilter(String source, CharFilterFactory charfilter) throws IOException {
		try (Reader reader = charfilter.create(new StringReader(
				source));) {
			return writeCharStream(reader);
		}
	}

	private List<DetailAnalyzeResponse.DetailAnalyzeToken> applyTokenizerAndTokenFiltersUntil(
			String charFilteredSource, TokenizerFactory tokenizer,
			TokenFilterFactory[] tokenfilters, int until) throws IOException {
		try (TokenStream stream = createStackedTokenStream(charFilteredSource, tokenizer,
				tokenfilters, until)) {
			List<DetailAnalyzeResponse.DetailAnalyzeToken> analyzed = processAnalysis(stream);
			return analyzed;
		}
	}

	private DetailAnalyzeResponse buildResponseForNonCustomAnalyzer(
			DetailAnalyzeRequest request, Analyzer analyzer, String field) {
		try (TokenStream stream = analyzer
				.tokenStream(field, request.getText())) {
			List<DetailAnalyzeResponse.DetailAnalyzeToken> analyzed = processAnalysis(stream);

			String name = getAnalyzerName(analyzer);

			DetailAnalyzeResponse response = new DetailAnalyzeResponse();
			response.setAnalyzer(new DetailAnalyzeResponse.DetailAnalyzeTokenList(
					name, analyzed));
			return response;
		} catch (IOException e) {
			throw new ElasticsearchException("failed to analyze", e);
		}
	}

	private String getAnalyzerName(Analyzer analyzer) {
		String name = null;
		if (analyzer instanceof NamedAnalyzer) {
			name = ((NamedAnalyzer) analyzer).name();
		} else {
			name = analyzer.getClass().getName();
		}
		return name;
	}

	private CustomAnalyzer findCustomAnalyzer(Analyzer analyzer) {
		CustomAnalyzer customAnalyzer = null;
		if (analyzer instanceof CustomAnalyzer) {
			customAnalyzer = (CustomAnalyzer) analyzer;
		} else if (analyzer instanceof NamedAnalyzer
				&& ((NamedAnalyzer) analyzer).analyzer() instanceof CustomAnalyzer) {
			customAnalyzer = (CustomAnalyzer) ((NamedAnalyzer) analyzer)
					.analyzer();
		}
		return customAnalyzer;
	}

	private TokenStream createStackedTokenStream(String charFilteredSource,
			TokenizerFactory tokenizer, TokenFilterFactory[] tokenfilters,
			int until) {
		TokenStream tokenStream = tokenizer.create(new StringReader(
				charFilteredSource));
		for (int i = 0; i < until; i++) {
			tokenStream = tokenfilters[i].create(tokenStream);
		}

		return tokenStream;
	}

	private String writeCharStream(Reader input) {
		final int BUFFER_SIZE = 1024;
		char[] buf = new char[BUFFER_SIZE];
		int len = 0;
		StringBuilder sb = new StringBuilder();
		do {
			try {
				len = input.read(buf, 0, BUFFER_SIZE);
			} catch (IOException e) {
				throw new ElasticsearchException(
						"failed to analyze (charfiltering)", e);
			}
			if (len > 0)
				sb.append(buf, 0, len);
		} while (len == BUFFER_SIZE);
		return sb.toString();
	}

	private List<DetailAnalyzeResponse.DetailAnalyzeToken> processAnalysis(
			TokenStream stream) throws IOException {
		List<DetailAnalyzeResponse.DetailAnalyzeToken> tokens = new ArrayList<>();
		stream.reset();

		// and each tokens output
		CharTermAttribute term = stream.addAttribute(CharTermAttribute.class);
		PositionIncrementAttribute posIncr = stream
				.addAttribute(PositionIncrementAttribute.class);
		OffsetAttribute offset = stream.addAttribute(OffsetAttribute.class);
		TypeAttribute type = stream.addAttribute(TypeAttribute.class);

		int position = 0;
		while (stream.incrementToken()) {
			int increment = posIncr.getPositionIncrement();
			if (increment > 0) {
				position = position + increment;
			}

			tokens.add(new DetailAnalyzeResponse.DetailAnalyzeToken(term
					.toString(), position, offset.startOffset(), offset
					.endOffset(), type.type(),
					extractExtendedAttributes(stream)));
		}
		stream.end();
		return tokens;
	}

	private Map<String, Map<String, Object>> extractExtendedAttributes(
			TokenStream stream) {
		final Map<String, Map<String, Object>> extendedAttributes = new TreeMap<>();

		stream.reflectWith(new AttributeReflector() {
			@Override
			public void reflect(Class<? extends Attribute> attClass,
					String key, Object value) {
				if (CharTermAttribute.class.isAssignableFrom(attClass))
					return;
				if (PositionIncrementAttribute.class.isAssignableFrom(attClass))
					return;
				if (OffsetAttribute.class.isAssignableFrom(attClass))
					return;
				if (TypeAttribute.class.isAssignableFrom(attClass))
					return;
				Map<String, Object> currentAttributes = extendedAttributes
						.get(attClass.getName());
				if (currentAttributes == null) {
					currentAttributes = new HashMap<>();
				}

				if (value instanceof BytesRef) {
					final BytesRef p = (BytesRef) value;
					value = p.toString();
				}
				currentAttributes.put(key, value);
				extendedAttributes.put(attClass.getName(), currentAttributes);
			}
		});

		return extendedAttributes;
	}
}
