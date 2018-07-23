package com.cli.ccmauto.cli;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;


@Parameters(commandDescription = "Creating new development line based on provided parameters. Example: create-devline -a \"test\" -dname \"New Development Line\" -did \"DevLineUnqueId\"")
public class DevelopmentLineCreate {
	
	@Parameter(names = { "-a", "--projectAreaName" }, description = "The project area to create development line", order=1, required = true)
	public String projectAreaName;
	
	@Parameter(names = { "-dname", "--developmentLineName" }, description = "Unique name for newly created development line", order=2, required = true)
	public String developmentLineName;
	
	@Parameter(names = { "-did", "--developmentLineId" }, description = "Unique id for newly created development line", order=3, required = true)
	public String developmentLineId;

}
