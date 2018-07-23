package com.cli.ccmauto.cli;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;


@Parameters(commandDescription = "Archive existing iteration based on provided path to iteration. Example: archive-iteration -a projectAreaName -i  \"Main Development/Example\"")
public class IterationArchive {
	
	@Parameter(names = { "-a", "--projectAreaName" }, description = "The project area to archive development line", order=1, required = true)
	public String projectAreaName;
	
	@Parameter(names = { "-i", "--iterationPath" }, description = "Path to iteration that will be archived. Example: Main Development/Iteration 1/Iteration 1.1", order=2, required = true)
	public String iterationPath;
}
