package com.cli.ccmauto.cli;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;

@Parameters(commandDescription = "Creating new SCM Stream based on provided parameters. Example: create-stream -a test -s NewStream -d \"My new stream\"")
public class StreamCreate {
	
	@Parameter(names = { "-a", "--projectArea" }, description = "The project area to create new stream. The project area can be set as stream owner or as read access permissions level for newly created stream. Should be used together with --ownershipType and --visibilityScopeName.", order=1, required = true)
	public String projectArea;
	
	@Parameter(names = { "-s", "--streamName" }, description = "Name for newly created stream", order=2, required = true)
	public String streamName;
	
	@Parameter(names = { "-d", "--streamDescription" }, description = "Description of newly created SCM Stream", order=3)
	public String streamDescription;
	
	@Parameter(names = { "-t", "--teamAreaPath" }, description = "Path to team area that can be set as stream owner or as read access permissions level for newly created component. Example: \"team\" or \"team/subteam\". Should be used together with --ownershipType and --visibilityScopeName.", order=6)
	public String teamAreaPath;
	
	@Parameter(names = { "-ag", "--accessGroupName" }, description = "Name of IBM Rational Team Concert access group that will be used as permissions level. Should be used together with --visibilityScopeName.", order=7)
	public String accessGroupName;
	
	@Parameter(names = { "-vs", "--visibilityScopeName" }, description = "Read access permissions for newly created stream. Visibility scope should be defined as ProjectAreaScope or TeamAreaScope or AccessGroupScope.", order=8)
	public String visibilityScopeName;
	
	@Parameter(names = { "-o", "--ownershipType" }, description = "Ownership type for newly created stream. Ownership type should be defined as ProjectArea or TeamArea.", order=9)
	public String ownershipType;

}
