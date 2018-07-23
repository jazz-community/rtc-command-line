package com.cli.ccmauto.cli;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;

@Parameters(commandDescription = "Creating new work item based on provided parameters. Example: create-wi -a \"Project Area\" -t defect -s \"New Work Item\"")
public class WorkItemCreate {
	
	public int id = 0;
	
	public String status;
	
	public String creationStatus="";
	
	@Parameter(names = { "-a", "--projectArea" }, description = "The project area to create work item(s)", order=1, required = true)
	public String projectArea;
	
	@Parameter(names = { "-s", "--summary" }, description = "Summary for newly created work item", order=2, required = true)
	public String summary;

	@Parameter(names = { "-t", "--type" }, description = "Type of newly created work item", order=3, required = true)
	public String type;
	
	@Parameter(names = { "-c", "--categoryPath" }, description = "Path to category that will be associated with newly work item (Filed against). Example: \"category\" or \"category/subcategory\".", order=5)
	public String categoryPath;
	
	@Parameter(names = { "-d", "--description" }, description = "Description for newly created work item", order=5)
	public String description;
	
	@Parameter(names = { "-o", "--owner" }, description = "User id for the user that will own newly created work item", order=6)
	public String owner;
	
	@Parameter(names = { "-pri", "--priority" }, description = "Priority for newly created work item (High, Medium, Low)", order=7)
	public String priority;
	
	@Parameter(names = { "-sev", "--severity" }, description = "Severity for newly created work item (Critical, Major, Blocker, ...)", order=8)
	public String severity;
	
	@Parameter(names = { "-ta", "--tags" }, description = "Tags for newly created work item. Tags should be enter in quetes with space as separator. Example \"tag1 tag2 tag3 tag4\" ", order=9)
	public String tags;
	
	@Parameter(names = { "-dur", "--duration" }, description = "Duration time for newly created work item. Duration is set in ms, 6000 = 6s.", order=10)
	public Long duration;
	
	@Parameter(names = { "-due", "--dueDate" }, description = "Due date for newly created work item. Due Date is using dd/MM/yyyy as input format", order=11)
	public String dueDate;
	
	@Parameter(names = { "-pfor", "--plannedFor" }, description = "Iteration (planned for) for newly created work item. User should sepcify complete path to iteration including development line and relese. Example: \"Main Development/Release 1.0/Sprint 1 (1.0)\"", order=12)
	public String plannedFor;
}
