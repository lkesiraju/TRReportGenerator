package com.sony.test.report;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class BuildResult {
	
	private int number = 0;
	
	private String timestamp = "";
	
	private String result = "";
	
	private List<Action> actions;

	public int getBuildNumber() {
		return number;
	}
	public void setNumber(int number) {
		this.number = number;
	}
	public String getTimestamp() {
	    Date parsedDate = new Date(Long.parseLong(timestamp));
		DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		format.setTimeZone(TimeZone.getTimeZone("Etc/UTC"));
		return format.format(parsedDate);
		
	}
	public void setTimestamp(String timestamp) {
		this.timestamp = timestamp;
	}
	public String getBuildResult() {
		return result;
	}
	public void setResult(String result) {
		this.result = result;
	}
	public List<Action> getActions() {
		return actions;
	}
	public void setActions(List<Action> actions) {
		this.actions = actions;
	}
}
