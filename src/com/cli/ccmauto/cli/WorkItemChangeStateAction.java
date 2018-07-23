package com.cli.ccmauto.cli;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;

@Parameters(commandDescription = "Modyfing state of existing work item based on action name. Example: change-state-wi-action -id 633 -an Resolve")
public class WorkItemChangeStateAction {
		
	@Parameter(names = { "-id", "--identifier" }, description = "Id of the work item to modify", order=1, required = true)
	public String id;
	
	@Parameter(names = { "-an", "--actionName" }, description = "Name of the action that will be used to change the state of work item", order=2, required = true)
	public String actionName;
}
