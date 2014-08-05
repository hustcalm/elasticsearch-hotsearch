package com.cgroups.elasticsearch.action.admin.indices.detail.analyze;

import static org.elasticsearch.action.ValidateActions.addValidationError;

import java.io.IOException;

import org.elasticsearch.action.ActionRequestValidationException;
import org.elasticsearch.action.support.single.custom.SingleCustomOperationRequest;
import org.elasticsearch.common.io.stream.StreamInput;
import org.elasticsearch.common.io.stream.StreamOutput;

public class DetailAnalyzeRequest extends
		SingleCustomOperationRequest<DetailAnalyzeRequest> {
	private String index;
	private String text;
	private String analyzer;

	
	DetailAnalyzeRequest() {
	}
	
	public DetailAnalyzeRequest(String index, String text, String analyzer) {
		this.index = index;
		this.text = text;
		this.analyzer = analyzer;
	}

	public String getIndex() {
		return index;
	}

	public void setIndex(String index) {
		this.index = index;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public String getAnalyzer() {
		return analyzer;
	}

	public void setAnalyzer(String analyzer) {
		this.analyzer = analyzer;
	}

	@Override
	public ActionRequestValidationException validate() {
		ActionRequestValidationException validationException = super.validate();
		if (index == null) {
			validationException = addValidationError("index is missing",
					validationException);
		}
		if (text == null) {
			validationException = addValidationError("text is missing",
					validationException);
		}
		return validationException;
	}

	@Override
	public void readFrom(StreamInput in) throws IOException {
		super.readFrom(in);
		index = in.readOptionalString();
		text = in.readString();
		analyzer = in.readOptionalString();
	}

	@Override
	public void writeTo(StreamOutput out) throws IOException {
		super.writeTo(out);
		out.writeOptionalString(index);
		out.writeString(text);
		out.writeOptionalString(analyzer);
	}

}
