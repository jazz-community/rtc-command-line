package com.cli.ccmauto.cli;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;

@Parameters(commandDescription = "Creating new SCM Component in existing stream. Example: create-component-in-stream -a projectAreaName -s streamName -c newComponentName")
public class ComponentCreateStream {
	
	@Parameter(names = { "-a", "--projectArea" }, description = "The project area that contains the stream for which new component will be created. The project area can be set as component owner or as read access permissions level for newly created component. Should be used together with --ownershipType and --visibilityScopeName.", order=1, required = true)
	public String projectAreaName;
	
	@Parameter(names = { "-s", "--streamName" }, description = "Name of stream in which new component will be create", order=2, required = true)
	public String streamName;
		
	@Parameter(names = { "-c", "--componentName" }, description = "Name for newly created component", order=5, required = true)
	public String componentName;
	
	@Parameter(names = { "-cn", "--componentContributor" }, description = "User id of owner for newly created component. Should be used together with --ownershipType and --visibilityScopeName.", order=6)
	public String componentContributorName;
	
	@Parameter(names = { "-t", "--teamAreaPath" }, description = "Path to team area that can be set as component owner or as read access permissions level for newly created component. Example: \"team\" or \"team/subteam\". Should be used together with --ownershipType and --visibilityScopeName.", order=6)
	public String teamAreaPath;
	
	@Parameter(names = { "-ag", "--accessGroupName" }, description = "Name of IBM Rational Team Concert access group that will be used as permissions level. Should be used together with --visibilityScopeName.", order=7)
	public String accessGroupName;
	
	@Parameter(names = { "-vs", "--visibilityScopeName" }, description = "Read access permissions for newly created component. Visibility scope should be defined as PrivateScope or PublicScope or ProjectAreaScope or TeamAreaScope or AccessGroupScope.", order=8)
	public String visibilityScopeName;
	
	@Parameter(names = { "-o", "--ownershipType" }, description = "Ownership type for newly created component. Ownership type should be defined as Contributor or ProjectArea or TeamArea.", order=9)
	public String ownershipType;
	
	}
