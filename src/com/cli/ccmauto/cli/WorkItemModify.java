package com.cli.ccmauto.cli;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;

@Parameters(commandDescription = "Modyfing existing work item based on provided parameters. Example: create-wi -id 15 -t defect -s \"New Summary Value\"")
public class WorkItemModify {
	
	public String modififcationStatus="";
	
	public String status;
	
	@Parameter(names = { "-id", "--identifier" }, description = "Id of the work item to modify", order=1, required = true)
	public int id;
	
	@Parameter(names = { "-s", "--summary" }, description = "New value of summary for existing work item", order=2)
	public String summary;

	@Parameter(names = { "-d", "--description" }, description = "New value of description for existing work item", order=3)
	public String description;
	
	@Parameter(names = { "-c", "--categoryPath" }, description = "Path to category that will be associated with existing work item (Filed against). Example: \"category\" or \"category/subcategory\".", order=4)
	public String categoryPath;
	
	@Parameter(names = { "-o", "--owner" }, description = "New value of owner (User Id) for existing work item", order=5)
	public String owner;
	
	@Parameter(names = { "-pri", "--priority" }, description = "New value of priority for existing work item (High, Medium, Low)", order=6)
	public String priority;
	
	@Parameter(names = { "-sev", "--severity" }, description = "New value of severity for existing work item (Critical, Major, Blocker, ...)", order=7)
	public String severity;
	
	@Parameter(names = { "-ta", "--tags" }, description = "New value of tags for existing work item. Tags should be enter in quetes with space as separator. Example \"tag1 tag2 tag3 tag4\" ", order=8)
	public String tags;
	
	@Parameter(names = { "-dur", "--duration" }, description = "New value of duration for existing work item. Duration is set in ms, 6000 = 6s.", order=9)
	public Long duration;
	
	@Parameter(names = { "-due", "--dueDate" }, description = "New value of due date for existing work item. Due Date is using dd/MM/yyyy as input format", order=10)
	public String dueDate;
	
	@Parameter(names = { "-pfor", "--plannedFor" }, description = "New value of planned for (Iteration) for existing work item. User should sepcify complete path to iteration including development line and relese. Example: \"Main Development/Release 1.0/Sprint 1 (1.0)\"", order=11)
	public String plannedFor;

}
