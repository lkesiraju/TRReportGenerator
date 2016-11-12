package com.sony.test.report;

import java.net.URI;

public class UrlBuilder {
	
	private static final String JSON_API_STRING = "/api/json?pretty=true";
	private static final String VIEW_STRING = "/view/%s";
	private static final String JOB_STRING = "/job/%s/lastSuccessfulBuild";
	
	private String host;
	private String view;
	private String viewText;

	public UrlBuilder(String host, String view) {
		this.host = host;
		this.view = view;
	}
	
	public URI getViewUri() {
		viewText = String.format(VIEW_STRING, view);
		return URI.create(new StringBuilder().append(host)
				.append(viewText)
				.append(JSON_API_STRING).toString());
	}
	
	public URI getJobUri(String job) {
		return URI.create(new StringBuilder().append(host)
				.append(viewText)
				.append(String.format(JOB_STRING, job))
				.append(JSON_API_STRING).toString());
	}
}
