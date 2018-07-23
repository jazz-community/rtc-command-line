package com.cli.ccm;

import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;

import com.cli.ccmauto.cli.ComponentCreateStream;
import com.cli.ccmauto.cli.ComponentCreateWorkspace;
import com.cli.ccmauto.cli.StreamCreate;
import com.cli.ccmauto.cli.WorkspaceCreateStream;
import com.ibm.team.process.client.IProcessClientService;
import com.ibm.team.process.common.IAccessGroup;
import com.ibm.team.process.common.IProjectArea;
import com.ibm.team.process.common.ITeamArea;
import com.ibm.team.repository.client.IItemManager;
import com.ibm.team.repository.client.ITeamRepository;
import com.ibm.team.repository.client.internal.ItemManager;
import com.ibm.team.repository.common.IAuditableHandle;
import com.ibm.team.repository.common.IContributor;
import com.ibm.team.repository.common.TeamRepositoryException;
import com.ibm.team.scm.client.IFlowNodeConnection.IComponentOp;
import com.ibm.team.scm.client.IFlowNodeConnection.IComponentOpFactory;
import com.ibm.team.scm.client.IWorkspaceConnection;
import com.ibm.team.scm.client.IWorkspaceManager;
import com.ibm.team.scm.client.SCMPlatform;
import com.ibm.team.scm.common.AcceptFlags;
import com.ibm.team.scm.common.IBaseline;
import com.ibm.team.scm.common.IBaselineHandle;
import com.ibm.team.scm.common.IBaselineSet;
import com.ibm.team.scm.common.IBaselineSetHandle;
import com.ibm.team.scm.common.IComponentHandle;
import com.ibm.team.scm.common.IFlowEntry;
import com.ibm.team.scm.common.IFlowTable;
import com.ibm.team.scm.common.IWorkspaceHandle;
import com.ibm.team.scm.common.WorkspaceComparisonFlags;
import com.ibm.team.scm.common.dto.IBaselineSetSearchCriteria;
import com.ibm.team.scm.common.dto.IChangeHistorySyncReport;
import com.ibm.team.scm.common.dto.IReadScope;
import com.ibm.team.scm.common.dto.IWorkspaceSearchCriteria;
import com.ibm.team.scm.common.internal.dto.WorkspaceSearchCriteria;
import com.ibm.team.workitem.common.IAuditableCommon;
import com.ibm.team.workitem.common.model.IWorkItem;

public class SCMManager {

	/**
	 * @param args
	 */
	

	
	 
	public static void createStream(ITeamRepository repo, IProgressMonitor monitor, StreamCreate streamCreate) throws TeamRepositoryException {
		IProcessClientService processClient = (IProcessClientService) repo.getClientLibrary(IProcessClientService.class);
		IAuditableCommon auditableCommon = (IAuditableCommon) repo.getClientLibrary(IAuditableCommon.class);
		IAccessGroup[] groups;
		IAccessGroup group = null;
		IReadScope scope = null;
		ITeamArea teamArea = null;
		IAuditableHandle ownership = null;
		
		
		URI uri = URI.create(streamCreate.projectArea.replaceAll(" ", "%20"));
		IProjectArea projectArea = (IProjectArea) processClient.findProcessArea(uri, null, monitor);
		if (projectArea == null) {
			throw new TeamRepositoryException("Project area "+ streamCreate.projectArea + " does not exist.");
		}
		ownership = projectArea;
			
		// Dodajemy access groupy
		if (streamCreate.accessGroupName != null) {
			groups = auditableCommon.getAccessGroups(streamCreate.accessGroupName, Integer.MAX_VALUE, monitor);
			if (groups.length == 0) {
				throw new TeamRepositoryException("Access group "+ streamCreate.accessGroupName + " does not exist.");
			} else {
				group = groups[0];
			}
		}
			
			
		// dodajemy team area
		if (streamCreate.teamAreaPath != null) {

			List<String> path = Arrays.asList(streamCreate.teamAreaPath.split("/"));

			TeamAreaHelper teamUtil = new TeamAreaHelper(repo, monitor);
			ITeamArea team = teamUtil.findTeamArea(projectArea, path);
			if (team == null) {
				throw new TeamRepositoryException("Team Area value "+ streamCreate.teamAreaPath + " does not exist.");
			}
			teamArea = team;
		}
		    
		// dodajemy scope
		if (streamCreate.visibilityScopeName != null) {
			if (!(streamCreate.visibilityScopeName.equals("AccessGroupScope")|| streamCreate.visibilityScopeName.equals("ProjectAreaScope") || streamCreate.visibilityScopeName.equals("TeamAreaScope"))) {
				throw new TeamRepositoryException("Visibility scope should be defined as ProjectAreaScope or TeamAreaScope or AccessGroupScope.\nProvided value of "+ streamCreate.visibilityScopeName + " is not supported");
			} else {
				switch (streamCreate.visibilityScopeName) {
				case "AccessGroupScope":{
					if (group == null) {
						throw new TeamRepositoryException(
								"Access group name is required when using AccessGroupScope scope");
					}
					scope = IReadScope.FACTORY.createAccessGroupScope(group);
					break;}
				case "ProjectAreaScope":{
					if (streamCreate.projectArea == null) {
						throw new TeamRepositoryException(
								"Project area is required when using ProjectAreaScope scope");
					}
					scope = IReadScope.FACTORY.createProcessAreaScope();
					break;
				}
				case "TeamAreaScope": {
					if (streamCreate.teamAreaPath == null) {
						throw new TeamRepositoryException(
								"Path to Team Area is required when using TeamAreaScope scope");
					}
					scope = IReadScope.FACTORY.createTeamAreaPrivateScope(teamArea);
					break;
				}
				}
			}
		}
		
		// dodajemy ownership
		if (streamCreate.ownershipType != null) {
			if (!(streamCreate.ownershipType.equals("ProjectArea") || streamCreate.ownershipType.equals("TeamArea"))) {
				throw new TeamRepositoryException("Ownership type should be defined as Contributor or ProjectArea or TeamArea.\nProvided value of "+ streamCreate.ownershipType + " is not supported");
			} else {
				switch (streamCreate.ownershipType) {
				case "ProjectArea":
				{
					if (streamCreate.projectArea == null) {
						throw new TeamRepositoryException("Project Area is required when using ProjectArea as ownership type");
					}
					ownership = projectArea;
					break;
				
				}
				case "TeamArea":
					if (streamCreate.teamAreaPath == null) {
						throw new TeamRepositoryException(
								"Path to Team Area is required when using TeamArea as ownership type");
					}
					ownership = teamArea;
					break;

				}
			}
		}
			
		IWorkspaceManager wm = SCMPlatform.getWorkspaceManager(repo);
		IWorkspaceConnection stream = wm.createStream(projectArea,streamCreate.streamName, streamCreate.streamDescription,monitor);
		
		stream.setOwnerAndVisibility(ownership, scope, monitor);

		monitor.subTask("Created SCM stream " + streamCreate.streamName+ " in project " + streamCreate.projectArea);
	  }
	
	
	  public static void createComponentInStream(ITeamRepository repo, IProgressMonitor monitor, ComponentCreateStream componentCreateStream) throws TeamRepositoryException
	  {
		  IAuditableCommon auditableCommon = (IAuditableCommon) repo.getClientLibrary(IAuditableCommon.class);
		  IProcessClientService processClient = (IProcessClientService) repo.getClientLibrary(IProcessClientService.class);
		  IAccessGroup[] groups;
		  IAccessGroup group = null;
		  IReadScope scope = null;
		  IContributor contributor = repo.loggedInContributor();
		  ITeamArea teamArea = null;
		  IAuditableHandle ownership = null;

		  
		  //Sprwadzamy project area 
		  URI uri = URI.create(componentCreateStream.projectAreaName.replaceAll(" ", "%20"));
		  IProjectArea projectArea = (IProjectArea) processClient.findProcessArea(uri, null, monitor);
		  if (projectArea == null) {
			throw new TeamRepositoryException("Project area " + componentCreateStream.projectAreaName+ " does not exist.");
		  }
		  
		  // Dodajemy contributora
		  if (componentCreateStream.componentContributorName!=null)
		  {
        	contributor = repo.contributorManager().fetchContributorByUserId(componentCreateStream.componentContributorName, monitor);
    		if (contributor == null) {
    			throw new TeamRepositoryException("Owner user id " + componentCreateStream.componentContributorName+ " does not exist.");
    		}
		  }
		  		  
		//Dodajemy access groupy
		if (componentCreateStream.accessGroupName != null) {
			groups = auditableCommon.getAccessGroups(componentCreateStream.accessGroupName,Integer.MAX_VALUE, monitor);
			if (groups.length == 0) {
				throw new TeamRepositoryException("Access group "+ componentCreateStream.accessGroupName + " does not exist.");
			} else {
				group = groups[0];
			}
		}
		
		//dodajemy team area
		if (componentCreateStream.teamAreaPath != null) {

			List<String> path = Arrays.asList(componentCreateStream.teamAreaPath.split("/"));

			TeamAreaHelper teamUtil = new TeamAreaHelper(repo, monitor);
			ITeamArea team = teamUtil.findTeamArea(projectArea, path);
			if (team == null) {
				throw new TeamRepositoryException("Team Area value "+ componentCreateStream.teamAreaPath + " does not exist.");
			}
			teamArea = team;
		}
		
		// dodajemy scope
		if (componentCreateStream.visibilityScopeName != null) {
			if (!(componentCreateStream.visibilityScopeName.equals("AccessGroupScope")
					|| componentCreateStream.visibilityScopeName.equals("PrivateScope")
					|| componentCreateStream.visibilityScopeName.equals("PublicScope")
					|| componentCreateStream.visibilityScopeName.equals("ProjectAreaScope") || componentCreateStream.visibilityScopeName
						.equals("TeamAreaScope"))) {
				throw new TeamRepositoryException(
						"Visibility scope should be defined as PrivateScope or PublicScope or ProjectAreaScope or TeamAreaScope or AccessGroupScope.\nProvided value of "
								+ componentCreateStream.visibilityScopeName + " is not supported");
			} else {
				switch (componentCreateStream.visibilityScopeName) {
				case "AccessGroupScope":{
					if (group == null) {
						throw new TeamRepositoryException(
								"Access group name is required when using AccessGroupScope scope");
					}
					scope = IReadScope.FACTORY.createAccessGroupScope(group);
					break;}
				case "PrivateScope":
					scope = IReadScope.FACTORY.createPrivateScope();
					break;
				case "PublicScope":
					scope = IReadScope.FACTORY.createPublicScope();
					break;
				case "ProjectAreaScope":
				{
					scope = IReadScope.FACTORY.createProcessAreaScope();
					break;
				}
				case "TeamAreaScope": {
					if (componentCreateStream.teamAreaPath == null) {
						throw new TeamRepositoryException(
								"Path to Team Area is required when using TeamAreaScope scope");
					}
					scope = IReadScope.FACTORY
							.createTeamAreaPrivateScope(teamArea);
					break;
				}
				}
			}
		}
	    
		// dodajemy ownership
		if (componentCreateStream.ownershipType != null) {
			if (!(componentCreateStream.ownershipType.equals("Contributor")|| componentCreateStream.ownershipType.equals("ProjectArea") || componentCreateStream.ownershipType.equals("TeamArea"))) {
				throw new TeamRepositoryException("Ownership type should be defined as Contributor or ProjectArea or TeamArea.\nProvided value of "+ componentCreateStream.ownershipType + " is not supported");
			} else {
				switch (componentCreateStream.ownershipType) {
				case "Contributor":
					ownership = contributor;
					break;
				case "ProjectArea":
				{
					ownership = projectArea;
					break;
				
				}
				case "TeamArea":
					if (componentCreateStream.teamAreaPath == null) {
						throw new TeamRepositoryException(
								"Path to Team Area is required when using TeamArea as ownership type");
					}
					ownership = teamArea;
					break;

				}
			}
		}
		
		//Tworzymy component w streamie
		  IWorkspaceManager wm = SCMPlatform.getWorkspaceManager(repo);
		  IWorkspaceSearchCriteria wsSearchCriteria = WorkspaceSearchCriteria.FACTORY.newInstance();
		  wsSearchCriteria.setKind(IWorkspaceSearchCriteria.STREAMS);
		  wsSearchCriteria.setExactName(componentCreateStream.streamName);
		  
		  List <IWorkspaceHandle> workspaceHandles = wm.findWorkspaces(wsSearchCriteria, Integer.MAX_VALUE, monitor);

		  if (workspaceHandles.size()<1)
		  {
			  throw new TeamRepositoryException("Stream " + componentCreateStream.streamName + " does not exist in project "+componentCreateStream.projectAreaName+" or you do not have permissions to access.");
		  }
		  
		  IWorkspaceConnection workspaceConnection = wm.getWorkspaceConnection(workspaceHandles.get(0),monitor);
		  IComponentOpFactory componentOpFactory =  workspaceConnection.componentOpFactory();
		  IComponentHandle component;
		  	  
		  // tworzymy component
		  component = wm.createComponent(componentCreateStream.componentName, contributor, monitor);
		  		  
		  // ustawiamy uprawnienia
		  if (scope!=null)
		  {
  		  switch(componentCreateStream.visibilityScopeName)
  		  {
      		  case "AccessGroupScope":
      		  {
      			  
      			  wm.setComponentOwnerAndVisibility(component, ownership, scope, monitor);
      			  break;
      		  }
      		  case "PrivateScope":
      		  {
      			  if (componentCreateStream.ownershipType!=null)
      			  {
      				  if (!componentCreateStream.ownershipType.equals("Contributor"))
      				  {
      					  throw new TeamRepositoryException("Private visibility scope can bet set only to contributor as an owner");
      				  }
      			  }
      			  wm.setComponentOwnerAndVisibility(component, ownership, scope, monitor);
      			  break;
      		  }
      		  case "PublicScope":
      		  {
      			  if (componentCreateStream.ownershipType!=null)
      			  {
      				  if (!componentCreateStream.ownershipType.equals("Contributor"))
      				  {
      					  throw new TeamRepositoryException("Public visibility scope can bet set only to contributor as an owner");
      				  }
      			  }
      			  wm.setComponentOwnerAndVisibility(component, ownership, scope, monitor);
      			  break;
      		  }
      		  case "ProjectAreaScope": 
      		  {
      			  if (componentCreateStream.ownershipType!=null)
      			  {
      				  if (componentCreateStream.ownershipType.equals("Contributor"))
      				  {
      					  wm.setComponentOwnerAndVisibility(component, ownership, IReadScope.FACTORY.createContributorDeferringScope(projectArea), monitor);
      				  }
      				  else{
      					  wm.setComponentOwnerAndVisibility(component, ownership, scope, monitor);
      				  }
      			  }
      			  else{
      				  wm.setComponentOwnerAndVisibility(component, ownership, IReadScope.FACTORY.createContributorDeferringScope(projectArea), monitor);
      			  }
      			  
      			  break;
      		  }
      		  case "TeamAreaScope": 
      		  {
      			  if (componentCreateStream.ownershipType!=null)
      			  {
      				  if (!componentCreateStream.ownershipType.equals("TeamArea"))
      				  {
      					  throw new TeamRepositoryException("TeamAreaScope visibility scope can bet set only to TeamArea as an owner");
      				  }
      			  }
      			  wm.setComponentOwnerAndVisibility(component, ownership, scope, monitor);
      			  break;

      		  }
  		  }
		  }
		  else
		  {
			  if (componentCreateStream.ownershipType!=null)
			  {
				  switch(componentCreateStream.ownershipType)
		  		  {
		      		  case "Contributor":
		      		  {
		      			  wm.setComponentOwnerAndVisibility(component, ownership, IReadScope.FACTORY.createPrivateScope(), monitor);
		      			  break;
		      		  }
		      		  case "TeamArea":
		      		  {
		      			  wm.setComponentOwnerAndVisibility(component, ownership, IReadScope.FACTORY.createTeamAreaPrivateScope(teamArea), monitor);
		      			  break;
		      		  }
		      		  case "ProjectArea":
		      		  {
		      			  wm.setComponentOwnerAndVisibility(component, ownership, IReadScope.FACTORY.createProcessAreaScope(projectArea), monitor);
		      			  break;
		      		  }
		  		  }
			  }
		  }
		  
		  
		  //Koniec
		  IComponentOp addComponentOp = componentOpFactory.addComponent(component, false);
		  workspaceConnection.applyComponentOperations(Collections.singletonList(addComponentOp), false, monitor);
		  monitor.subTask("Component "+componentCreateStream.componentName+" was created in stream "+componentCreateStream.streamName);
		  		  	  
	  }
	
	private static List<IBaselineSet> getBaselineSets(ITeamRepository repo,IWorkspaceHandle workspaceHandle,String snapshotName,IProjectArea projectArea, IWorkspaceManager wm , IProgressMonitor monitor) throws TeamRepositoryException {
		IBaselineSetSearchCriteria searchCriteria = IBaselineSetSearchCriteria.FACTORY.newInstance();
		searchCriteria.setOwnerWorkspaceOptional(workspaceHandle);
		searchCriteria.setExactName(snapshotName);
		searchCriteria.setProcessArea(projectArea);
		List<IBaselineSetHandle> setsHandles = wm.findBaselineSets(searchCriteria,Integer.MAX_VALUE, monitor);
		List <IBaselineSet> returned = new ArrayList<IBaselineSet>();
		
		for (IBaselineSetHandle baseHandle : setsHandles)
		{
			IBaselineSet base = (IBaselineSet) repo.itemManager().fetchCompleteItem(baseHandle, ItemManager.DEFAULT, monitor); 
			returned.add(base);
		} 
		return returned;
	}

	
	 public static void createWorkspaceFromStream(ITeamRepository repo, IProgressMonitor monitor,WorkspaceCreateStream workStream) throws TeamRepositoryException
	  {
		  IProcessClientService processClient = (IProcessClientService) repo.getClientLibrary(IProcessClientService.class);
		  IAuditableCommon auditableCommon = (IAuditableCommon) repo.getClientLibrary(IAuditableCommon.class);
		  URI uri = URI.create(workStream.projectAreaName.replaceAll(" ", "%20"));
		  IContributor contributor = repo.loggedInContributor();
		  IAccessGroup[] groups;
		  IReadScope scope = null;
		  IAccessGroup group = null;
		  
		  IProjectArea projectArea = (IProjectArea) processClient.findProcessArea(uri, null, monitor);
		  
		  if (projectArea == null) {
			throw new TeamRepositoryException("Project area " + workStream.projectAreaName+ " does not exist.");
		  }
		  
		  IWorkspaceManager wm = SCMPlatform.getWorkspaceManager(repo);
		  IWorkspaceSearchCriteria wsSearchCriteria = WorkspaceSearchCriteria.FACTORY.newInstance();
		  wsSearchCriteria.setKind(IWorkspaceSearchCriteria.STREAMS);
		  wsSearchCriteria.setExactName(workStream.streamName);	  
		  wsSearchCriteria.setExactOwnerName(workStream.projectAreaName);
		  List <IWorkspaceHandle> workspaceHandles = wm.findWorkspaces(wsSearchCriteria, Integer.MAX_VALUE, monitor);
		  System.out.println(workspaceHandles.size());
		  
		  if (workspaceHandles.size()<1)
		  {
			  throw new TeamRepositoryException("Stream " + workStream.streamName + " does not exist in project "+workStream.projectAreaName+" or you do not have permissions to access.");
		  }
		  
		  if (workStream.workspaceDescription==null)
		  {
			  workStream.workspaceDescription=new String();
		  }
		  
		  // Dodajemy contributora
		  if (workStream.ownerName!=null)
		  {
       	contributor = repo.contributorManager().fetchContributorByUserId(workStream.ownerName, monitor);
   		if (contributor == null) {
   			throw new TeamRepositoryException("Owner user id " + workStream.ownerName+ " does not exist.");
   		}
		  }
		  
		// Dodajemy access groupy
		if (workStream.accessGroupName != null) {
			groups = auditableCommon.getAccessGroups(workStream.accessGroupName,Integer.MAX_VALUE, monitor);
			if (groups.length == 0) {
				throw new TeamRepositoryException("Access group "+ workStream.accessGroupName + " does not exist.");
			} else {
				group = groups[0];
			}
		}
		// dodajemy scope
				if (workStream.visibilityScopeName != null) {
					if (!(workStream.visibilityScopeName.equals("AccessGroupScope")|| workStream.visibilityScopeName.equals("PrivateScope")|| workStream.visibilityScopeName.equals("PublicScope")|| workStream.visibilityScopeName.equals("ProjectAreaScope")))  {
						throw new TeamRepositoryException(
								"Visibility scope should be defined as PrivateScope or PublicScope or ProjectAreaScope or AccessGroupScope.\nProvided value of "+ workStream.visibilityScopeName + " is not supported");
					} else {
						switch (workStream.visibilityScopeName) {
						case "AccessGroupScope":{
							if (group == null) {
								throw new TeamRepositoryException(
										"Access group name is required when using AccessGroupScope scope");
							}
							scope = IReadScope.FACTORY.createAccessGroupScope(group);
							break;}
						case "PrivateScope":
							scope = IReadScope.FACTORY.createPrivateScope();
							break;
						case "PublicScope":
							scope = IReadScope.FACTORY.createPublicScope();
							break;
						case "ProjectAreaScope":
						{
							scope = IReadScope.FACTORY.createContributorDeferringScope(projectArea);
							break;
						}
						}
					}
				}
		  
		  IWorkspaceConnection workspace = wm.createWorkspace(contributor, workStream.workspaceName, workStream.workspaceDescription, monitor);
		  
		  IWorkspaceConnection streamWorkspaceConnection=wm.getWorkspaceConnection(workspaceHandles.get(0), null);
		  
		  IFlowTable flowTable = workspace.getFlowTable().getWorkingCopy();
		  flowTable.addDeliverFlow(streamWorkspaceConnection.getResolvedWorkspace(), repo.getId(),repo.getRepositoryURI(), null, workspace.getDescription());
		  IFlowEntry flowNode = flowTable.getDeliverFlow(streamWorkspaceConnection.getResolvedWorkspace());
		  flowTable.setDefault(flowNode);
		  flowTable.setCurrent(flowNode);
		  workspace.setFlowTable(flowTable, null);

		  List<Object> componentsToBeAdded=new ArrayList<Object>();
		  
		  for (Object a : streamWorkspaceConnection.getComponents())
		  {
			  IComponentHandle comp = (IComponentHandle)a;
			  componentsToBeAdded.add(workspace.componentOpFactory().addComponent(comp, true));
		  }
		  workspace.applyComponentOperations(componentsToBeAdded, null);
		  
		  IChangeHistorySyncReport streamChangeHistorySyncReport=workspace.compareTo(streamWorkspaceConnection, WorkspaceComparisonFlags.INCLUDE_BASELINE_INFO, Collections.EMPTY_LIST, null);
		 
		  for (Object a : streamWorkspaceConnection.getComponents())
		  {
			  IComponentHandle comp = (IComponentHandle)a;
			  workspace.accept(AcceptFlags.DEFAULT, streamWorkspaceConnection, streamChangeHistorySyncReport, streamChangeHistorySyncReport.incomingBaselines(comp), streamChangeHistorySyncReport.incomingChangeSets(comp), null);
		  }
		  
		  if (scope!=null)
		  {
			  workspace.setOwnerAndVisibility(contributor, scope, monitor);
		  }
		  		  
		  monitor.subTask("Created new workspace "+workStream.workspaceName+" from stream "+workStream.streamName);
	  }
	  
	  public static void createWorkspaceFromSnapshot(ITeamRepository repo, IProgressMonitor monitor,WorkspaceCreateStream workSnap) throws TeamRepositoryException
	  {
		  
		  IContributor contributor = repo.loggedInContributor();
		  IAccessGroup[] groups;
		  IReadScope scope = null;
		  IAccessGroup group = null;
		  IProcessClientService processClient = (IProcessClientService) repo.getClientLibrary(IProcessClientService.class);
		  IAuditableCommon auditableCommon = (IAuditableCommon) repo.getClientLibrary(IAuditableCommon.class);
		  
		  URI uri = URI.create(workSnap.projectAreaName.replaceAll(" ", "%20"));
		  IProjectArea projectArea = (IProjectArea) processClient.findProcessArea(uri, null, monitor);
		  if (projectArea == null) {
			throw new TeamRepositoryException("Project area " + workSnap.projectAreaName+ " does not exist.");
		  }
		  
		  IWorkspaceManager wm = SCMPlatform.getWorkspaceManager(repo);
		  IWorkspaceSearchCriteria wsSearchCriteria = WorkspaceSearchCriteria.FACTORY.newInstance();
		  wsSearchCriteria.setKind(IWorkspaceSearchCriteria.STREAMS);
		  wsSearchCriteria.setExactName(workSnap.streamName);	  
		  wsSearchCriteria.setExactOwnerName(workSnap.projectAreaName);
		  List <IWorkspaceHandle> workspaceHandles = wm.findWorkspaces(wsSearchCriteria, Integer.MAX_VALUE, monitor);
		  if (workspaceHandles.size()<1)
		  {
			  throw new TeamRepositoryException("Stream " + workSnap.streamName + " does not exist in project "+workSnap.projectAreaName+" or you do not have permissions to access.");
		  }

		  List<IBaselineSet> baselines = SCMManager.getBaselineSets(repo, workspaceHandles.get(0), workSnap.snapshotName,projectArea, wm, monitor);
		  
		  IBaselineSet toCreate = null;
		  
		  for (IBaselineSet base : baselines)
		  {
			  if (wm.getWorkspaceConnection(base.getOwner(), monitor).getName().equals(workSnap.streamName) && base.getName().equals(workSnap.snapshotName))
			  {
				  toCreate = base;
				  break;
			  }

		  }
		  
		  if (toCreate==null)
		  {
			  throw new TeamRepositoryException("Snapshot " + workSnap.snapshotName + " does not exist for stream "+workSnap.streamName+" in project "+workSnap.projectAreaName+".");
		  }
		  
		  if (workSnap.workspaceDescription==null)
		  {
			  workSnap.workspaceDescription=new String();
		  }
		  
		// Dodajemy contributora
		  if (workSnap.ownerName!=null)
		  {
       	contributor = repo.contributorManager().fetchContributorByUserId(workSnap.ownerName, monitor);
   		if (contributor == null) {
   			throw new TeamRepositoryException("Owner user id " + workSnap.ownerName+ " does not exist.");
   		}
		  }
		  
		// Dodajemy access groupy
		if (workSnap.accessGroupName != null) {
			groups = auditableCommon.getAccessGroups(workSnap.accessGroupName,Integer.MAX_VALUE, monitor);
			if (groups.length == 0) {
				throw new TeamRepositoryException("Access group "+ workSnap.accessGroupName + " does not exist.");
			} else {
				group = groups[0];
			}
		}
		// dodajemy scope
				if (workSnap.visibilityScopeName != null) {
					if (!(workSnap.visibilityScopeName.equals("AccessGroupScope")|| workSnap.visibilityScopeName.equals("PrivateScope")|| workSnap.visibilityScopeName.equals("PublicScope")|| workSnap.visibilityScopeName.equals("ProjectAreaScope")))  {
						throw new TeamRepositoryException(
								"Visibility scope should be defined as PrivateScope or PublicScope or ProjectAreaScope or AccessGroupScope.\nProvided value of "+ workSnap.visibilityScopeName + " is not supported");
					} else {
						switch (workSnap.visibilityScopeName) {
						case "AccessGroupScope":{
							if (group == null) {
								throw new TeamRepositoryException(
										"Access group name is required when using AccessGroupScope scope");
							}
							scope = IReadScope.FACTORY.createAccessGroupScope(group);
							break;}
						case "PrivateScope":
							scope = IReadScope.FACTORY.createPrivateScope();
							break;
						case "PublicScope":
							scope = IReadScope.FACTORY.createPublicScope();
							break;
						case "ProjectAreaScope":
						{
							scope = IReadScope.FACTORY.createContributorDeferringScope(projectArea);
							break;
						}
						}
					}
				}
		  
		  IWorkspaceConnection workspace = wm.createWorkspace(contributor, workSnap.workspaceName, workSnap.workspaceDescription, monitor);
		  IWorkspaceConnection streamWorkspaceConnection=wm.getWorkspaceConnection(workspaceHandles.get(0), null);
		  		  
		  IFlowTable flowTable = workspace.getFlowTable().getWorkingCopy();
		  flowTable.addDeliverFlow(streamWorkspaceConnection.getResolvedWorkspace(), repo.getId(),repo.getRepositoryURI(), null, workspace.getDescription());
		  IFlowEntry flowNode = flowTable.getDeliverFlow(streamWorkspaceConnection.getResolvedWorkspace());
		  flowTable.setDefault(flowNode);
		  flowTable.setCurrent(flowNode);
		  workspace.setFlowTable(flowTable, null);
		  
		  List<Object> componentsToBeAdded=new ArrayList<Object>();
		  
		  for (Object a : toCreate.getBaselines())
		  {
			 IBaselineHandle baselineH = (IBaselineHandle)a;
			 IBaseline baseL = (IBaseline) repo.itemManager().fetchCompleteItem(baselineH, ItemManager.DEFAULT, monitor);
			 componentsToBeAdded.add(workspace.componentOpFactory().addComponent(baseL.getComponent(), true));
		  }
		  
		  workspace.applyComponentOperations(componentsToBeAdded, null);  

		  
		  for (Object a : toCreate.getBaselines())
		  {
			 IBaselineHandle baselineH = (IBaselineHandle)a;
			 IBaseline baseL = (IBaseline) repo.itemManager().fetchCompleteItem(baselineH, ItemManager.DEFAULT, monitor);
			 IChangeHistorySyncReport sync = workspace.compareTo (streamWorkspaceConnection,WorkspaceComparisonFlags.INCLUDE_BASELINE_INFO | WorkspaceComparisonFlags.INCLUDE_EMPTY_BASELINES, Collections.EMPTY_LIST, monitor); 
			 
			 workspace.accept(AcceptFlags.DEFAULT,streamWorkspaceConnection, sync, sync.incomingBaselines(baseL.getComponent()), sync.incomingChangeSets(baseL.getComponent()),monitor); 
		  }
		  
		  if (scope!=null)
		  {
			  workspace.setOwnerAndVisibility(contributor, scope, monitor);
		  }
		  
		  monitor.subTask("Created new workspace "+workSnap.workspaceName+" from snapshot "+workSnap.snapshotName+" in stream "+workSnap.streamName);
	  }
	  
	  public static void createComponentInWorkspace(ITeamRepository repo, IProgressMonitor monitor, ComponentCreateWorkspace componentCreateWorkspace) throws TeamRepositoryException
	  {
		  IAuditableCommon auditableCommon = (IAuditableCommon) repo.getClientLibrary(IAuditableCommon.class);
		  IProcessClientService processClient = (IProcessClientService) repo.getClientLibrary(IProcessClientService.class);
		  IAccessGroup[] groups;
		  IAccessGroup group = null;
		  IReadScope scope = null;
		  IContributor contributor = repo.loggedInContributor();
		  ITeamArea teamArea = null;
		  IAuditableHandle ownership = null;
		  IProjectArea projectArea = null;

		  //Sprwadzamy project area
		  if(componentCreateWorkspace.projectAreaName!=null)
		  {

		  URI uri = URI.create(componentCreateWorkspace.projectAreaName.replaceAll(" ", "%20"));
		   projectArea = (IProjectArea) processClient.findProcessArea(uri, null, monitor);
		  if (projectArea == null) {
			throw new TeamRepositoryException("Project area " + componentCreateWorkspace.projectAreaName+ " does not exist.");
		  }}
		  
		  // Sprawdzamy czy wlasciciel workspace istnieje
		  if (componentCreateWorkspace.workspaceOwnerName!=null)
		  {
   		if (repo.contributorManager().fetchContributorByUserId(componentCreateWorkspace.workspaceOwnerName, monitor) == null) {
   			throw new TeamRepositoryException("Workspace owner user id " + componentCreateWorkspace.componentContributorName+ " does not exist.");
   		}
		  }
		  
		  // Dodajemy contributora componentu
		  if (componentCreateWorkspace.componentContributorName!=null)
		  {
       	contributor = repo.contributorManager().fetchContributorByUserId(componentCreateWorkspace.componentContributorName, monitor);
   		if (contributor == null) {
   			throw new TeamRepositoryException("Component owner user id " + componentCreateWorkspace.componentContributorName+ " does not exist.");
   		}
		  }
		  		  
		//Dodajemy access groupy
		if (componentCreateWorkspace.accessGroupName != null) {
			groups = auditableCommon.getAccessGroups(componentCreateWorkspace.accessGroupName,Integer.MAX_VALUE, monitor);
			if (groups.length == 0) {
				throw new TeamRepositoryException("Access group "+ componentCreateWorkspace.accessGroupName + " does not exist.");
			} else {
				group = groups[0];
			}
		}
	    
		//dodajemy team area
		if (componentCreateWorkspace.teamAreaPath != null) {

			List<String> path = Arrays.asList(componentCreateWorkspace.teamAreaPath.split("/"));

			TeamAreaHelper teamUtil = new TeamAreaHelper(repo, monitor);
			ITeamArea team = teamUtil.findTeamArea(projectArea, path);
			if (team == null) {
				throw new TeamRepositoryException("Team Area value "+ componentCreateWorkspace.teamAreaPath + " does not exist.");
			}
			teamArea = team;
		}
	    
		
		// dodajemy scope
		if (componentCreateWorkspace.visibilityScopeName != null) {
			if (!(componentCreateWorkspace.visibilityScopeName.equals("AccessGroupScope")
					|| componentCreateWorkspace.visibilityScopeName.equals("PrivateScope")
					|| componentCreateWorkspace.visibilityScopeName.equals("PublicScope")
					|| componentCreateWorkspace.visibilityScopeName.equals("ProjectAreaScope") || componentCreateWorkspace.visibilityScopeName
						.equals("TeamAreaScope"))) {
				throw new TeamRepositoryException(
						"Visibility scope should be defined as PrivateScope or PublicScope or ProjectAreaScope or TeamAreaScope or AccessGroupScope.\nProvided value of "
								+ componentCreateWorkspace.visibilityScopeName + " is not supported");
			} else {
				switch (componentCreateWorkspace.visibilityScopeName) {
				case "AccessGroupScope":{
					if (group == null) {
						throw new TeamRepositoryException(
								"Access group name is required when using AccessGroupScope scope");
					}
					scope = IReadScope.FACTORY.createAccessGroupScope(group);
					break;}
				case "PrivateScope":
					scope = IReadScope.FACTORY.createPrivateScope();
					break;
				case "PublicScope":
					scope = IReadScope.FACTORY.createPublicScope();
					break;
				case "ProjectAreaScope":{
					if (componentCreateWorkspace.projectAreaName == null) {
						throw new TeamRepositoryException(
								"Project area is required when using ProjectAreaScope scope");
					}
					scope = IReadScope.FACTORY.createProcessAreaScope();
					break;
				}
				case "TeamAreaScope": {
					if (componentCreateWorkspace.teamAreaPath == null) {
						throw new TeamRepositoryException(
								"Path to Team Area is required when using TeamAreaScope scope");
					}
					scope = IReadScope.FACTORY.createTeamAreaPrivateScope(teamArea);
					break;
				}
				}
			}
		}
	    
		// dodajemy ownership
		if (componentCreateWorkspace.ownershipType != null) {
			if (!(componentCreateWorkspace.ownershipType.equals("Contributor")|| componentCreateWorkspace.ownershipType.equals("ProjectArea") || componentCreateWorkspace.ownershipType.equals("TeamArea"))) {
				throw new TeamRepositoryException("Ownership type should be defined as Contributor or ProjectArea or TeamArea.\nProvided value of "+ componentCreateWorkspace.ownershipType + " is not supported");
			} else {
				switch (componentCreateWorkspace.ownershipType) {
				case "Contributor":
					ownership = contributor;
					break;
				case "ProjectArea":
				{
					if (componentCreateWorkspace.projectAreaName == null) {
						throw new TeamRepositoryException("Project Area is required when using ProjectArea as ownership type");
					}
					ownership = projectArea;
					break;
				
				}
				case "TeamArea":
					if (componentCreateWorkspace.teamAreaPath == null) {
						throw new TeamRepositoryException(
								"Path to Team Area is required when using TeamArea as ownership type");
					}
					ownership = teamArea;
					break;

				}
			}
		}
		
		//Tworzymy component w streamie
		  IWorkspaceManager wm = SCMPlatform.getWorkspaceManager(repo);
		  IWorkspaceSearchCriteria wsSearchCriteria = WorkspaceSearchCriteria.FACTORY.newInstance();
		  wsSearchCriteria.setKind(IWorkspaceSearchCriteria.WORKSPACES);
		  wsSearchCriteria.setExactName(componentCreateWorkspace.workspaceName);	
		  if(componentCreateWorkspace.workspaceOwnerName!=null)
		  {
			  wsSearchCriteria.setExactOwnerName(componentCreateWorkspace.workspaceOwnerName);
		  }
		  List <IWorkspaceHandle> workspaceHandles = wm.findWorkspaces(wsSearchCriteria, Integer.MAX_VALUE, monitor);
		  if (workspaceHandles.size()<1)
		  {
			  if(componentCreateWorkspace.workspaceOwnerName!=null)
			  {
				  throw new TeamRepositoryException("Workspace " + componentCreateWorkspace.workspaceName + " owned by "+componentCreateWorkspace.workspaceOwnerName+" does not exist.");
			  }
			  throw new TeamRepositoryException("Workspace " + componentCreateWorkspace.workspaceName + " owned by "+contributor.getUserId()+" does not exist.");
		  }
		  IWorkspaceConnection workspaceConnection = wm.getWorkspaceConnection(workspaceHandles.get(0),monitor);
		  
		  IComponentOpFactory componentOpFactory =  workspaceConnection.componentOpFactory();
		  IComponentHandle component;
		  

		  
		  // tworzymy component
		  component = wm.createComponent(componentCreateWorkspace.componentName, contributor, monitor);
		  
		  
		  // ustawiamy uprawnienia
		  if (scope!=null)
		  {
 		  switch(componentCreateWorkspace.visibilityScopeName)
 		  {
     		  case "AccessGroupScope":
     		  {
     			  
     			  wm.setComponentOwnerAndVisibility(component, ownership, scope, monitor);
     			  break;
     		  }
     		  case "PrivateScope":
     		  {
     			  if (componentCreateWorkspace.ownershipType!=null)
     			  {
     				  if (!componentCreateWorkspace.ownershipType.equals("Contributor"))
     				  {
     					  throw new TeamRepositoryException("Private visibility scope can bet set only to contributor as an owner");
     				  }
     			  }
     			  wm.setComponentOwnerAndVisibility(component, ownership, scope, monitor);
     			  break;
     		  }
     		  case "PublicScope":
     		  {
     			  if (componentCreateWorkspace.ownershipType!=null)
     			  {
     				  if (!componentCreateWorkspace.ownershipType.equals("Contributor"))
     				  {
     					  throw new TeamRepositoryException("Public visibility scope can bet set only to contributor as an owner");
     				  }
     			  }
     			  wm.setComponentOwnerAndVisibility(component, ownership, scope, monitor);
     			  break;
     		  }
     		  case "ProjectAreaScope": 
     		  {
     			  if (componentCreateWorkspace.ownershipType!=null)
     			  {
     				  if (componentCreateWorkspace.ownershipType.equals("Contributor"))
     				  {
     					  System.out.println("a");
     					  wm.setComponentOwnerAndVisibility(component, ownership, IReadScope.FACTORY.createContributorDeferringScope(projectArea), monitor);
     				  }
     				  else{
     					  System.out.println("b");
     					  wm.setComponentOwnerAndVisibility(component, ownership, scope, monitor);
     				  }
     			  }
     			  else{
     				  System.out.println("c");
     				  wm.setComponentOwnerAndVisibility(component, ownership, IReadScope.FACTORY.createContributorDeferringScope(projectArea), monitor);
     			  }
     			  
     			  break;
     		  }
     		  case "TeamAreaScope": 
     		  {
     			  if (componentCreateWorkspace.ownershipType!=null)
     			  {
     				  if (!componentCreateWorkspace.ownershipType.equals("TeamArea"))
     				  {
     					  throw new TeamRepositoryException("TeamAreaScope visibility scope can bet set only to TeamArea as an owner");
     				  }
     			  }
     			  wm.setComponentOwnerAndVisibility(component, ownership, scope, monitor);
     			  break;

     		  }
 		  }
		  }
		  else
		  {
			  if (componentCreateWorkspace.ownershipType!=null)
			  {
				  switch(componentCreateWorkspace.ownershipType)
		 		  {
		     		  case "Contributor":
		     		  {
		     			  wm.setComponentOwnerAndVisibility(component, ownership, IReadScope.FACTORY.createPrivateScope(), monitor);
		     			  break;
		     		  }
		     		  case "TeamArea":
		     		  {
		     			  wm.setComponentOwnerAndVisibility(component, ownership, IReadScope.FACTORY.createTeamAreaPrivateScope(teamArea), monitor);
		     			  break;
		     		  }
		     		  case "ProjectArea":
		     		  {
		     			  wm.setComponentOwnerAndVisibility(component, ownership, IReadScope.FACTORY.createProcessAreaScope(projectArea), monitor);
		     			  break;
		     		  }
		 		  }
			  }
		  }
		  

		  //Koniec
		  IComponentOp addComponentOp = componentOpFactory.addComponent(component, false);
		  workspaceConnection.applyComponentOperations(Collections.singletonList(addComponentOp), false, monitor);
		  monitor.subTask("Component "+componentCreateWorkspace.componentName+" was created in workspace "+componentCreateWorkspace.workspaceName);
		  		  	  
	  }

	  
}
