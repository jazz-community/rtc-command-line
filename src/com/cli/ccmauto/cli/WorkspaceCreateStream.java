package com.cli.ccmauto.cli;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;

@Parameters(commandDescription = "Creating new SCM Workspace from existing stream or stream snapshot. Example: create-workspace-from-stream -a projectArea -w NowaWorkspaace -w user -s streamName -sp snapshotName")
public class WorkspaceCreateStream {
	
	@Parameter(names = { "-a", "--projectArea" }, description = "The project area that contains stream", order=1, required = true)
	public String projectAreaName;
	
	@Parameter(names = { "-w", "--workspaceName" }, description = "Name for newly created workspace", order=2, required = true)
	public String workspaceName;
	
	@Parameter(names = { "-wo", "--workspaceOwnerName" }, description = "User id for the user that will own newly created workspace", order=3)
	public String ownerName;
	
	@Parameter(names = { "-d", "--workspaceDescription" }, description = "Description of newly created SCM workspace", order=4)
	public String workspaceDescription;
	
	@Parameter(names = { "-s", "--streamName" }, description = "Name of stream that will be used to create new workspace", order=5, required = true)
	public String streamName;
	
	@Parameter(names = { "-sp", "--snapshotName" }, description = "Name of stream that will be used to create new workspace", order=6)
	public String snapshotName;
	
	@Parameter(names = { "-ag", "--accessGroupName" }, description = "Name of IBM Rational Team Concert access group that will be as permissions level. Should be used together with --visibilityScopeName.", order=7)
	public String accessGroupName;
	
	@Parameter(names = { "-vs", "--visibilityScopeName" }, description = "Read access permissions for newly created workspace. Visibility scope should be defined as PrivateScope or PublicScope or ProjectAreaScope or AccessGroupScope.", order=8)
	public String visibilityScopeName;
	
	
	//String ownerName, String accessGroupName,String visibilityScopeName - wszystko optional
}
