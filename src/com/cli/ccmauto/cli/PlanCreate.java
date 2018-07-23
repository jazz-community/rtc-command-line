package com.cli.ccmauto.cli;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;

//poprawic example
@Parameters(commandDescription = "Create new plan based on provided parameters. Example: create-plan -u username -a projectArea -n planName -pt com.ibm.team.apt.plantype.kanbanBoard -i \"Main Development/Backlog\" -t \"Team1/Team1Dev\"")
public class PlanCreate {
	
	@Parameter(names = { "-a", "--projectAreaName" }, description = "The project area to create plan", order=1, required = true)
	public String projectAreaName;
	
	@Parameter(names = { "-n", "--planName" }, description = "Name for newly created plan", order=2, required = true)
	public String planName;
	
	@Parameter(names = { "-pt", "--planType" }, description = "Type of newly created plan. Example: com.ibm.team.apt.plantype.kanbanBoard", order=3, required = true)
	public String planType;
	
	@Parameter(names = { "-i", "--iterationPath" }, description = "Path to iteration for which plan will be created. Example: Main Development/Iteration 1/Iteration 1.1", order=4, required=true)
	public String iterationPath;
	
	@Parameter(names = { "-t", "--teamAreaPath" }, description = "Path to team area that will be associated with newly created plan. Example: \"team\" or \"team/subteam\".", order=5)
	public String teamAreaPath;
	// wymagana iteracja
	
	// niewymagany team
}