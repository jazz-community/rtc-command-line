package com.cli.ccmauto.cli;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;


@Parameters(commandDescription = "Archive existing development line based on provided name. Example: archive-devline -a projectAreaName -dname  exampleID")
public class DevelopmentLineArchive {
	@Parameter(names = { "-a", "--projectAreaName" }, description = "The project area to archive development line", order=1, required = true)
	public String projectAreaName;
	
	@Parameter(names = { "-dname", "--developmentLineName" }, description = "Name/label/id of development line to be archived", order=2, required = true)
	public String developmentLineName;
}
