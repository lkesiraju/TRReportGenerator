package com.sony.test.report;

import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class ResultAggregator {
	
	private static Log LOGGER = LogFactory.getLog(ResultAggregator.class);
	
	private Map<String, BuildResult> result;
	
	private JenkinsClient client;
	
	private ResponseParser parser;
	
	private UrlBuilder urlBuilder;
	
	public ResultAggregator(JenkinsClient client, UrlBuilder urlBuilder) {
		this.urlBuilder = urlBuilder;
		this.client = client;
		this.parser = new ResponseParser();
	}

	private List<String> getJobList(URI viewUri) {
		LOGGER.info("Getting job list...");
		return parser.getAllJobsinView(client.getDataFromJenkins(viewUri));
	}
	
	private BuildResult getBuildResult(URI jobUri) {
		return parser.getBuildResult(client.getDataFromJenkins(jobUri));
	}
	
	public Map<String, BuildResult> getTestResults() {
		result = new HashMap<String, BuildResult>();
		List<String> jobs = getJobList(urlBuilder.getViewUri());
		LOGGER.info("Getting Test result...");
		if (!jobs.isEmpty()) {
			for (String job : jobs){
				result.put(job, getBuildResult(urlBuilder.getJobUri(job)));
			}
		}
		return result;
	}
}
