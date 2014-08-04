package com.worksap.elasticsearch.action.admin.indices.detail.analyze;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.elasticsearch.action.ActionResponse;
import org.elasticsearch.common.io.stream.StreamInput;
import org.elasticsearch.common.io.stream.StreamOutput;
import org.elasticsearch.common.io.stream.Streamable;
import org.elasticsearch.common.xcontent.ToXContent;
import org.elasticsearch.common.xcontent.XContentBuilder;

public class DetailAnalyzeResponse extends ActionResponse implements ToXContent {

	private DetailAnalyzeTokenList analyzer;
	private List<CharFilteredText> charfilters;
	private DetailAnalyzeTokenList tokenizer;
	private List<DetailAnalyzeTokenList> tokenfilters;

	DetailAnalyzeResponse() {
	}

	public DetailAnalyzeResponse(DetailAnalyzeTokenList analyzer,
			DetailAnalyzeTokenList tokenizer,
			List<DetailAnalyzeTokenList> tokenfilters,
			List<CharFilteredText> charfilters) {
		this.analyzer = analyzer;
		this.tokenizer = tokenizer;
		this.tokenfilters = tokenfilters;
		this.charfilters = charfilters;
	}

	@Override
	public XContentBuilder toXContent(XContentBuilder builder, Params params)
			throws IOException {
		if (analyzer != null) {
			builder.startObject("analyzer");
			toXContentDetailAnalyzeTokenList(builder, analyzer);
			builder.endObject();
		}

		if (charfilters != null && !charfilters.isEmpty()) {
			builder.startArray("charfilters");
			for (CharFilteredText charfilter : charfilters) {
				builder.startObject();
				builder.field("name", charfilter.getName());
				builder.field("filterd_text", charfilter.getText());
				builder.endObject();
			}
			builder.endArray();
		}

		if (tokenizer != null) {
			builder.startObject("tokenizer");
			toXContentDetailAnalyzeTokenList(builder, tokenizer);
			builder.endObject();
		}

		if (tokenfilters != null && !tokenfilters.isEmpty()) {
			builder.startArray("tokenfilters");
			for (DetailAnalyzeTokenList tokenfilter : tokenfilters) {
				builder.startObject();
				toXContentDetailAnalyzeTokenList(builder, tokenfilter);
				builder.endObject();
			}
			builder.endArray();
		}

		return builder;
	}

	private XContentBuilder toXContentDetailAnalyzeTokenList(
			XContentBuilder builder, DetailAnalyzeTokenList list)
			throws IOException {
		builder.startArray(list.name);
		for (DetailAnalyzeToken token : list.getTokens()) {
			builder.startObject();
			builder.field("token", token.getTerm());
			builder.field("start_offset", token.getStartOffset());
			builder.field("end_offset", token.getEndOffset());
			builder.field("type", token.getType());
			builder.field("position", token.getPosition());
			builder.field("extended_attributes", token.getExtendedAttrbutes());
			builder.endObject();
		}
		builder.endArray();
		return builder;
	}

	public static class DetailAnalyzeTokenList implements Streamable {
		private List<DetailAnalyzeToken> tokens;
		private String name;

		DetailAnalyzeTokenList() {
		}

		public DetailAnalyzeTokenList(String name,
				List<DetailAnalyzeToken> tokens) {
			this.name = name;
			this.tokens = tokens;
		}

		public static DetailAnalyzeTokenList readDetailAnalyzeTokenList(
				StreamInput in) throws IOException {
			DetailAnalyzeTokenList list = new DetailAnalyzeTokenList();
			list.readFrom(in);
			return list;
		}

		public String getName() {
			return this.name;
		}

		public List<DetailAnalyzeToken> getTokens() {
			return tokens;
		}

		@Override
		public void readFrom(StreamInput in) throws IOException {
			name = in.readString();
			int size = in.readVInt();
			tokens = new ArrayList<DetailAnalyzeToken>(size);
			for (int i = 0; i < size; i++) {
				tokens.add(DetailAnalyzeToken.readDetailAnalyzeToken(in));
			}
		}

		@Override
		public void writeTo(StreamOutput out) throws IOException {
			out.writeString(name);
			out.writeVInt(tokens.size());
			for (DetailAnalyzeToken token : tokens) {
				token.writeTo(out);
			}
		}
	}

	public static class DetailAnalyzeToken implements Streamable {
		private String term;
		private int startOffset;
		private int endOffset;
		private int position;
		private String type;
		private Map<String, Map<String, Object>> extendedAttributes;

		DetailAnalyzeToken() {
		}

		public DetailAnalyzeToken(String term, int position, int startOffset,
				int endOffset, String type,
				Map<String, Map<String, Object>> extendedAttributes) {
			this.term = term;
			this.position = position;
			this.startOffset = startOffset;
			this.endOffset = endOffset;
			this.type = type;
			this.extendedAttributes = extendedAttributes;
		}

		public static DetailAnalyzeToken readDetailAnalyzeToken(StreamInput in)
				throws IOException {
			DetailAnalyzeToken analyzeToken = new DetailAnalyzeToken();
			analyzeToken.readFrom(in);
			return analyzeToken;
		}

		public String getTerm() {
			return this.term;
		}

		public int getStartOffset() {
			return this.startOffset;
		}

		public int getEndOffset() {
			return this.endOffset;
		}

		public int getPosition() {
			return this.position;
		}

		public String getType() {
			return this.type;
		}

		public Map<String, Map<String, Object>> getExtendedAttrbutes() {
			return this.extendedAttributes;
		}

		@SuppressWarnings("unchecked")
		@Override
		public void readFrom(StreamInput in) throws IOException {
			term = in.readString();
			startOffset = in.readInt();
			endOffset = in.readInt();
			position = in.readVInt();
			type = in.readOptionalString();
			extendedAttributes = (Map<String, Map<String, Object>>) in
					.readGenericValue();
		}

		@Override
		public void writeTo(StreamOutput out) throws IOException {
			out.writeString(term);
			out.writeInt(startOffset);
			out.writeInt(endOffset);
			out.writeVInt(position);
			out.writeOptionalString(type);
			out.writeGenericValue(extendedAttributes);
		}
	}

	public static class CharFilteredText implements Streamable {
		private String name;
		private String text;

		CharFilteredText() {
		}

		public CharFilteredText(String name, String text) {
			this.name = name;
			this.text = text;
		}

		public String getName() {
			return name;
		}

		public String getText() {
			return text;
		}

		@Override
		public void readFrom(StreamInput in) throws IOException {
			name = in.readString();
			text = in.readString();
		}

		@Override
		public void writeTo(StreamOutput out) throws IOException {
			out.writeString(name);
			out.writeString(text);
		}
	}

	public void addCharfilter(CharFilteredText charfilter) {
        if (charfilters == null) {
        	List<CharFilteredText> cs = new ArrayList<>();
        	cs.add(charfilter);
            charfilters = cs;
        } else {
            this.charfilters.add(charfilter);
        }
	}

	public void setTokenizer(DetailAnalyzeTokenList tokenizer) {
		  this.tokenizer = tokenizer;
	}

	public void addTokenfilter(DetailAnalyzeTokenList tokenfilter) {
        if (tokenfilters == null) {
        	List<DetailAnalyzeTokenList> tfs = new ArrayList<>();
        	tfs.add(tokenfilter);
            tokenfilters = tfs;
        } else {
            tokenfilters.add(tokenfilter);
        }
	}

	public void setAnalyzer(DetailAnalyzeTokenList analyzer) {
		this.analyzer = analyzer;
	}
}
