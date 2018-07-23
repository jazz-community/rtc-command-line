package com.cli.ccmauto.cli;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;

@Parameters(commandDescription = "Creating new Category on provided parameters. Example: create-category -a test -c NewCategoryName -cp \"ParentCategory/ParentCategory2\" -t \"TeamArea1/TeamArea1Child1\"")
public class CategoryCreate {
	@Parameter(names = { "-a", "--projectArea" }, description = "The project area to create new category", order=1, required = true)
	public String projectArea;
	
	@Parameter(names = { "-c", "--categoryName" }, description = "Name for newly created category", order=2, required = true)
	public String categoryName;
	
	@Parameter(names = { "-cp", "--parentCategorypath" }, description = "Path to parent category for newly created category.", order=3)
	public String parentCategorypath;

	@Parameter(names = { "-t", "--teamAreaPath" }, description = "Path to team area that will be associated with newly created category. Example: \"team\" or \"team/subteam\"", order=4)
	public String teamAreaPath;

}
