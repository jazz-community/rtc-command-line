package com.cli.ccmauto.cli;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;

@Parameters(commandDescription = "Modyfing state of existing work item based on target state name. Example: change-state-wi -id 633 -ts Resolved")
public class WorkItemChangeState {
	
	@Parameter(names = { "-id", "--identifier" }, description = "Id of the work item to modify", order=1, required = true)
	public String id;
	
	@Parameter(names = { "-ts", "--targetState" }, description = "Name of the target state that will be set to the work item", order=2, required = true)
	public String targetState;
}
