package com.sony.test.report;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class ResponseParser
{
	private static String ERROR_RESPONSE = "PARSER_ERROR";
	private static Log LOGGER = LogFactory.getLog(ResponseParser.class); 
	List<String> excludeJobsList = Arrays.asList("TR_Smoke_All_q1-np","TR_Commerce_PGW_Adyen_All_q1-np","testrunner-release-major-builder","TR_Commerce_PGW_PayPalDI_All_q1-np","TR_Commerce_PGW_PayPalDI_All_q1-np","TR_Commerce_Reg_All_q1-np");
	
	public BuildResult getBuildResult(String jsonText){
		BuildResult resultObj = null;
		if (jsonText != null && !jsonText.isEmpty()) {
			ObjectMapper mapper = new ObjectMapper();
			try {
				resultObj = mapper.readValue(jsonText, BuildResult.class);
			} catch (JsonParseException ex) {
				LOGGER.error(ERROR_RESPONSE , ex);
			} catch (JsonMappingException ex) {
				LOGGER.error(ERROR_RESPONSE , ex);
			} catch (IOException ex) {
				LOGGER.error(ERROR_RESPONSE , ex);
			}
		}
		return resultObj;
	}
	
	public List<String> getAllJobsinView(String jsonText){
		List<String> jobNames = new LinkedList<String>();
		List<String> groupedJobs = new LinkedList<String>();
		List<String> filteredList = new LinkedList<String>();
		if (jsonText != null && !jsonText.isEmpty()){
			ObjectMapper mapper = new ObjectMapper();
			try {
				JobList jobList = mapper.readValue(jsonText, JobList.class);
				List<Job> jobs = jobList.getJobs();
				for (Job job : jobs) {
					jobNames.add(job.getName());
				}
				filteredList = 
						jobNames.stream()
							.filter(x -> !excludeJobsList.contains(x))
								.collect(Collectors.toList());
				} catch (JsonParseException ex) {
				LOGGER.error(ERROR_RESPONSE , ex);
				return Collections.emptyList();
				
			} catch (IOException ex) {
				LOGGER.error(ERROR_RESPONSE , ex);
				return Collections.emptyList();
			}
		}
		return filteredList;
	}
}
