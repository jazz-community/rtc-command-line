package com.cli.ccmauto.cli;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;

// poprawic example
@Parameters(commandDescription = "Instanciate existing work item template on provided parameters. Example: create-category -a test -c NewCategoryName -cp \"ParentCategory/ParentCategory2\" -t \"TeamArea1/TeamArea1Child1\"")
public class WorkItemtemplateInstantiate {
	
	@Parameter(names = { "-a", "--projectArea" }, description = "The project area to create work item(s)", order=1, required = true)
	public String projectArea;
	
	@Parameter(names = { "-t", "--templateName" }, description = "Name of work item template that will be instanciated", order=2, required = true)
	public String categoryName;
	
	@Parameter(names = { "-c", "--categoryPath" }, description = "Path to category for work items created by instanciated template.", order=3)
	public String parentCategorypath;

	@Parameter(names = { "-pfor", "--plannedFor" }, description = "Path to iteration (planned for) for work items created by instanciated template.", order=4)
	public String iterationPath;

}
