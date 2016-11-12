package com.sony.test.report;
/**
 * Contains all the variable names/keys for JSON message 
 * 
 * @author lkesiraju
 * 
 */
public enum ColumnHeading {
	S_NO("S NO."),
	TEST_RUNNER_JOB("Test Runner Job"),
	BUILDNUMBER("Build #"),
	RESULT("Result"),
	EXECUTIONDATETIME("Execution Time"),
	TOTAL_TESTS("Total Tests"),
	TESTS_PASSED("Tests Passed(jenkins)"),
	FAILED_TESTS("Tests Originally Failed(jenkins)"),
	TESTS_PASSED_LOCALLY("Tests Passed (Locally)"),
	TESTS_FAILED_LOCALLY("Tests Failed (Locally)"),
	TESTS_NOT_EXECUTED("Tests Not Executed"),
	TESTS_ANALYSED("Tests Analysed"),
	PERCENT_INVESTIGATED("Percent Investigated(%)"),
	REASON_FOR_ERROR("Reason for Error"),
	NOTES("Notes");
			
	private String name;

	private ColumnHeading(String name) {
		this.name = name;
	}

	public String getName() {
		return this.name;
	}
}
