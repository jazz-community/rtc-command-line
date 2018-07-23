package com.cli.ccmauto.cli;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;


@Parameters(commandDescription = "Adding comment to existing work item. Example: add-comment -id 49 -c \"New comment\"")
public class WICommentAdd {
	
	@Parameter(names = { "-id", "--workItemId" }, description = "Id of work item to which comment will be added", order=1, required = true)
	public int id;

	@Parameter(names = { "-c", "--comment" }, description = "Comment text", order=2, required = true)
	public String comment;
	
	
}
