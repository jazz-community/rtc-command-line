package com.cli.ccmauto.cli;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;

@Parameters(commandDescription = "Creating link betweem different work items. Example: create-wi-link -lt \"com.ibm.team.workitem.linktype.parentworkitem\" -f 48 -t 49")
public class WorkItemLink {
	
	@Parameter(names = { "-t", "--toId" }, description = "Id of target work item", order=1, required = true)
	public int toId;

	@Parameter(names = { "-f", "--fromId" }, description = "Id of source work item", order=2, required = true)
	public int fromId;

	@Parameter(names = { "-lt", "--linkType" }, description = "Relation type name. Example com.ibm.team.workitem.linktype.parentworkitem", order=3, required = true)
	public String linkType;

}
