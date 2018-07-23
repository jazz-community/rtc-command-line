package com.cli.ccmauto.cli;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;


@Parameters(commandDescription = "Create new iteration based on provided path to iteration. Example: create-iteration -a projectAreaName -i  \"Main Development\" -n newIterationName -id newIterarationId -start 20/04/2017 -end 20/05/2017 -pr")
public class IterationCreate {
	
	@Parameter(names = { "-a", "--projectAreaName" }, description = "The project area to create development line", order=1, required = true)
	public String projectAreaName;
	
	@Parameter(names = { "-i", "--iterationPath" }, description = "Path to parent iteration under which new integration will be created. Example: Main Development/Iteration 1/Iteration 1.1", order=2, required=true)
	public String iterationPath;
	
	@Parameter(names = { "-n", "--iterationName" }, description = "Name for newly created iteration", order=3, required = true)
	public String iterationName;
	
	@Parameter(names = { "-id", "--iterationId" }, description = "Identifier of newly created iteration", order=4, required = true)
	public String iterationId;

	@Parameter(names = { "-start", "--startDate" }, description = "Start date for newly created iteration. Start date is using dd/MM/yyyy as input format", order=5)
	public String startDate;
	
	@Parameter(names = { "-end", "--endDate" }, description = "Due date for newly created work item. End date is using dd/MM/yyyy as input format", order=6)
	public String endDate;
	
	@Parameter(names = { "-pr", "--plannedRelease" }, description = "Flag defines if for new iteration is planned release", order=7)
	public boolean hasDeliverable;
	
}