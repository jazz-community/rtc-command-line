package com.cli.ccmauto.cli;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;


@Parameters(commandDescription = "Creating new project area based on provided parameters. Example: create-project -n \"New project\" -id \"scrum2.process.ibm.com\"")
public class ProjectAreaCreate {
	
	@Parameter(names = { "-n", "--projectAreaName" }, description = "The name of a project area that will be created", order=1, required = true)
	public String projectAreaName;
	
	@Parameter(names = { "-id", "--processId" }, description = "The id of process that will be used to create a project", order=2, required = true)
	public String processId;

}
