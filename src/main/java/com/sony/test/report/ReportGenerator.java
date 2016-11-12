package com.sony.test.report;

public class ReportGenerator {

	private static final String HOST = "https://lvn-snp-prhdsn02.sonynei.net:9080";
	private static final String VIEW = "TR-q1-line";
	private static final String FILE_PATH = new StringBuilder().append(System.getProperty("user.home")).append("\\NPTestRunner_Execution_Summary.xls").toString();
	private static JenkinsClient client;
	
	public static void main(String[] args) {
		UrlBuilder urlBuilder = new UrlBuilder(HOST, VIEW);
		getJenkinsClient();
		ResultAggregator resultAggregator = new ResultAggregator(client,urlBuilder);
		ExcelReportWriter reportWriter = new ExcelReportWriter(FILE_PATH);
		reportWriter.write(resultAggregator.getTestResults());
		client.closeConnection();
	}
	
	private static JenkinsClient getJenkinsClient()
	{
		client = new JenkinsClient();
		return client;
	}
}
