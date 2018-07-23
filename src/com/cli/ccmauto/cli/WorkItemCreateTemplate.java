package com.cli.ccmauto.cli;

import java.util.List;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;

//Poprawic example

@Parameters(commandDescription = "Creating new work items based on provided work item template. Example: create-workitem-template -a projectAreaName -tn templateName -cp \"Category1/Category1.1\" -pfor \"Main Development/Iteration1\" -k keyword1=value1 -k keyword2=value2 -k keyword3=value3")
public class WorkItemCreateTemplate {
	
	@Parameter(names = { "-a", "--projectArea" }, description = "The project area to create work item(s) based on provided template", order=1, required = true)
	public String projectArea;
	
	@Parameter(names = { "-tn", "--templateName" }, description = "..", order=2, required = true)
	public String templateName;
	
	@Parameter(names = { "-cp", "--categoryPath" }, description = "Path to category for newly created work items. Example: \"Category1/Category1.1\"", order=3)
	public String categoryPath;
	
	@Parameter(names = { "-pfor", "--plannedFor" }, description = "Iteration (planned for) for newly created work item. User should sepcify complete path to iteration including development line and relese. Example: \"Main Development/Release 1.0/Sprint 1 (1.0)\"", order=4)
	public String plannedFor;
	
	@Parameter(names = { "-k", "--keyword" }, description = "Keyword that will be replaced in template during creating work items. Parameter can be used multiple times exmaple: -k key1=value1 -k key2=value2 -k key3=value3", order=5)
	public List<String> keywords;
}
