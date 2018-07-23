package com.cli.ccm;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.util.Arrays;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;

import com.cli.ccmauto.cli.CategoryCreate;
import com.cli.ccmauto.cli.DevelopmentLineArchive;
import com.cli.ccmauto.cli.DevelopmentLineCreate;
import com.cli.ccmauto.cli.IterationArchive;
import com.cli.ccmauto.cli.IterationCreate;
import com.cli.ccmauto.cli.PlanCreate;
import com.cli.ccmauto.cli.ProjectAreaCreate;
import com.ibm.team.apt.common.IIterationPlanRecord;
import com.ibm.team.apt.internal.common.nucleus.IterationPlanRecord;
import com.ibm.team.apt.internal.common.rcp.IIterationPlanService;
import com.ibm.team.apt.internal.common.rcp.dto.DTO_IterationPlanSaveResult;
import com.ibm.team.apt.internal.common.wiki.IWikiPage;
import com.ibm.team.process.client.IProcessClientService;
import com.ibm.team.process.client.IProcessItemService;
import com.ibm.team.process.common.IDevelopmentLine;
import com.ibm.team.process.common.IIteration;
import com.ibm.team.process.common.IIterationHandle;
import com.ibm.team.process.common.IProcessDefinition;
import com.ibm.team.process.common.IProcessItem;
import com.ibm.team.process.common.IProjectArea;
import com.ibm.team.process.common.IProjectAreaHandle;
import com.ibm.team.process.common.ITeamArea;
import com.ibm.team.repository.client.ITeamRepository;
import com.ibm.team.repository.client.util.IClientLibraryContext;
import com.ibm.team.repository.common.IContent;
import com.ibm.team.repository.common.TeamRepositoryException;
import com.ibm.team.workitem.client.IWorkItemClient;
import com.ibm.team.workitem.common.IAuditableCommon;
import com.ibm.team.workitem.common.internal.model.Category;
import com.ibm.team.workitem.common.model.ICategory;

public class ProjectManager {

	  public static IProjectAreaHandle createProject(ITeamRepository repo, IProgressMonitor monitor, ProjectAreaCreate pac) throws TeamRepositoryException {
         
	        IProcessItemService service = (IProcessItemService) repo.getClientLibrary(IProcessItemService.class);
	        String PROCESS = pac.processId;
	        IProcessDefinition[] definitions = service.deployPredefinedProcessDefinitions(new String[] { PROCESS }, monitor);
	        if (definitions.length == 0) {
	            throw new TeamRepositoryException("Process template " + PROCESS + " does not exist.");
	        }
	        IProjectArea area = service.createProjectArea();
	        area.setName(pac.projectAreaName);
	        area.setProcessDefinition(definitions[0]);
	        area = (IProjectArea) service.save(area, monitor);
	        area = (IProjectArea) service.getMutableCopy(area);
	        monitor.subTask("Created project " + area.getName());
	        service.initialize(area, monitor);
	        //area.removeD
	        return area; 
	    }
	  
	  public static void createDevelopmentLine(ITeamRepository repo, IProgressMonitor monitor, DevelopmentLineCreate devLineCreate) throws TeamRepositoryException {
		  	IProcessClientService processClient = (IProcessClientService) repo.getClientLibrary(IProcessClientService.class);
		  	 
			URI uri = URI.create(devLineCreate.projectAreaName.replaceAll(" ", "%20"));
			IProjectArea projectArea = (IProjectArea) processClient.findProcessArea(uri, null, monitor);
			if (projectArea == null) {
				throw new TeamRepositoryException("Project area " + devLineCreate.projectAreaName+ " does not exist.");
			}
			
			IProcessItemService service = (IProcessItemService) repo.getClientLibrary(IProcessItemService.class);
			projectArea = (IProjectArea) service.getMutableCopy(projectArea);
		
			
			IDevelopmentLine developmentLine = (IDevelopmentLine) IDevelopmentLine.ITEM_TYPE.createItem(); 
			developmentLine.setId(devLineCreate.developmentLineId);
			developmentLine.setName(devLineCreate.developmentLineName); 
			developmentLine.setProjectArea(projectArea); 

			projectArea.addDevelopmentLine(developmentLine);
			service.save(new IProcessItem[] { projectArea, developmentLine }, null); 
			monitor.subTask("Created development line "+devLineCreate.developmentLineName+" with id "+devLineCreate.developmentLineId +" in project area "+devLineCreate.projectAreaName);
	  }

	  public static void archiveIteration(ITeamRepository repo, IProgressMonitor monitor, IterationArchive itorArchive) throws TeamRepositoryException {
		  	IProcessClientService processClient = (IProcessClientService) repo.getClientLibrary(IProcessClientService.class);
		  	 
			URI uri = URI.create(itorArchive.projectAreaName.replaceAll(" ", "%20"));
			IProjectArea projectArea = (IProjectArea) processClient.findProcessArea(uri, null, monitor);
			if (projectArea == null) {
				throw new TeamRepositoryException("Project area " + itorArchive.projectAreaName+ " does not exist.");
			}
			
          List<String> path = Arrays.asList(itorArchive.iterationPath.split("/"));
          // map by ID
          
          DevelopmentLineHelper devLineUtil = new DevelopmentLineHelper(repo, monitor);
          IIteration found = devLineUtil.findIteration(projectArea, path , devLineUtil.BYLABEL);
          if (found == null) {
          	found = devLineUtil.findIteration(projectArea, path , devLineUtil.BYNAME);
          }
          
          if (found == null) {
            	found = devLineUtil.findIteration(projectArea, path , devLineUtil.BYID);
            }
          
      		if (found==null)
      		{
      			throw new TeamRepositoryException("Iteration value "+itorArchive.iterationPath+" does not exist.");
      		}
      	
			IProcessItemService service = (IProcessItemService) repo.getClientLibrary(IProcessItemService.class);
			projectArea = (IProjectArea) service.getMutableCopy(projectArea);
			
			service.archiveProcessItem(found, monitor);
			
			monitor.subTask("Archived Iteration "+found.getLabel()+" with id "+found.getId() +" in project area "+itorArchive.projectAreaName);
	  }
	  
	  public static void archiveDevelopmentLine(ITeamRepository repo, IProgressMonitor monitor, DevelopmentLineArchive developmentLine) throws TeamRepositoryException {
		  	IProcessClientService processClient = (IProcessClientService) repo.getClientLibrary(IProcessClientService.class);
		  	 
			URI uri = URI.create(developmentLine.projectAreaName.replaceAll(" ", "%20"));
			IProjectArea projectArea = (IProjectArea) processClient.findProcessArea(uri, null, monitor);
			if (projectArea == null) {
				throw new TeamRepositoryException("Project area " + developmentLine.projectAreaName+ " does not exist.");
			}
			
        List<String> path = Arrays.asList(developmentLine.developmentLineName.split("/"));
        // map by ID
        

        DevelopmentLineHelper devLineUtil = new DevelopmentLineHelper(repo, monitor);
        IDevelopmentLine devLine = devLineUtil.findDevelopmentLine(projectArea, path, devLineUtil.BYLABEL);
        if (devLine == null) {
      	  devLine = devLineUtil.findDevelopmentLine(projectArea, path, devLineUtil.BYNAME);
        }
        if (devLine == null) {
        	  devLine = devLineUtil.findDevelopmentLine(projectArea, path, devLineUtil.BYID);
          }
        
	      	if (devLine==null)
	      	{
	      		throw new TeamRepositoryException("Development line value "+developmentLine+" does not exist.");
	      	}
    	
			IProcessItemService service = (IProcessItemService) repo.getClientLibrary(IProcessItemService.class);
			projectArea = (IProjectArea) service.getMutableCopy(projectArea);
			
			service.archiveProcessItem(devLine, monitor);
			
			monitor.subTask("Archived Development line "+devLine.getLabel()+" with id "+devLine.getId() +" in project area "+developmentLine.projectAreaName);
	  }
	  
	  public static void createIteration(ITeamRepository repo, IProgressMonitor monitor, IterationCreate itor) throws TeamRepositoryException {
		  	IProcessClientService processClient = (IProcessClientService) repo.getClientLibrary(IProcessClientService.class);
		  	 
			URI uri = URI.create(itor.projectAreaName.replaceAll(" ", "%20"));
			IProjectArea projectArea = (IProjectArea) processClient.findProcessArea(uri, null, monitor);
			if (projectArea == null) {
				throw new TeamRepositoryException("Project area " + itor.projectAreaName+ " does not exist.");
			}
			
			IProcessItemService service = (IProcessItemService) repo.getClientLibrary(IProcessItemService.class);
			projectArea = (IProjectArea) service.getMutableCopy(projectArea);
			DevelopmentLineHelper devLineUtil = new DevelopmentLineHelper(repo, monitor);
			
			
			IIteration newIteration = (IIteration) IIteration.ITEM_TYPE.createItem(); 
			newIteration.setName(itor.iterationName); 
			newIteration.setId(itor.iterationId); 
			
			if(itor.startDate!=null)
			{
		       	java.sql.Timestamp timer = Util.convertStringToTimestamp(itor.startDate);
		       	newIteration.setStartDate(timer);
			}

			if(itor.endDate!=null)
			{
		       	java.sql.Timestamp timer = Util.convertStringToTimestamp(itor.endDate);
		       	newIteration.setEndDate(timer);
			}
			
			newIteration.setHasDeliverable(itor.hasDeliverable);
			
			
			List<String> path = Arrays.asList(itor.iterationPath.split("/"));
			

			if (path.size()==1)
			{	
				IDevelopmentLine devLine = devLineUtil.findDevelopmentLine(projectArea, path, devLineUtil.BYLABEL);
				if (devLine==null)
				{
					devLine = devLineUtil.findDevelopmentLine(projectArea, path, devLineUtil.BYNAME);
				}
				if (devLine==null)
				{
					devLine = devLineUtil.findDevelopmentLine(projectArea, path, devLineUtil.BYID);
				}
				
				if (devLine == null) {
					throw new TeamRepositoryException("Development line " + itor.iterationPath + " does not exist.");
				}
				
				IDevelopmentLine workingDevLine = (IDevelopmentLine) devLine.getWorkingCopy(); 
				workingDevLine.addIteration((IIterationHandle) newIteration.getItemHandle()); 
			    newIteration.setDevelopmentLine(workingDevLine); 
			    service.save(new IProcessItem[] { workingDevLine, newIteration },monitor); 
			}
			else
			{
				IIteration it = devLineUtil.findIteration(projectArea, path, devLineUtil.BYLABEL);
				if (it==null)
				{
					it = devLineUtil.findIteration(projectArea, path, devLineUtil.BYNAME);
				}
				if (it==null)
				{
					it = devLineUtil.findIteration(projectArea, path, devLineUtil.BYID);
				}
				if (it == null) {
					throw new TeamRepositoryException("Iteration " + itor.iterationPath + " does not exist.");
				}
				IIteration workingIteration = (IIteration)it.getWorkingCopy();
				workingIteration.addChild((IIterationHandle) newIteration.getItemHandle()); 
				newIteration.setDevelopmentLine(workingIteration.getDevelopmentLine());
				newIteration.setParent(workingIteration);
				service.save(new IProcessItem[] { workingIteration, newIteration },monitor);
			}
			monitor.subTask("Created itertion "+itor.iterationName+" with id "+itor.iterationId +" in project area "+itor.projectAreaName);
	  }
	  
	  public static void createCategory(ITeamRepository repo, IProgressMonitor monitor, CategoryCreate categoryCreate) throws TeamRepositoryException {
		  	IProcessClientService processClient = (IProcessClientService) repo.getClientLibrary(IProcessClientService.class);
		  	 
			URI uri = URI.create(categoryCreate.projectArea.replaceAll(" ", "%20"));
			IProjectArea projectArea = (IProjectArea) processClient.findProcessArea(uri, null, monitor);
			if (projectArea == null) {
				throw new TeamRepositoryException("Project area " + categoryCreate.projectArea+ " does not exist.");
			}
			
			IWorkItemClient workItemClient = (IWorkItemClient) repo.getClientLibrary(IWorkItemClient.class); 
			ICategory cat = null;
			
			if(categoryCreate.parentCategorypath!=null)
			{
				
				List<String> path = Arrays.asList(categoryCreate.parentCategorypath.split("/"));
				
				CategoryHelper catUtil = new CategoryHelper(repo, monitor);
				ICategory cato = catUtil.findCategory(projectArea, path);
				if (cato==null)
				{
					throw new TeamRepositoryException("Category value "+categoryCreate.parentCategorypath+" does not exist.");
				}
				cat = workItemClient.createSubcategory(cato, categoryCreate.categoryName, monitor);
				
			}
			else{
				cat = workItemClient.createCategory(projectArea, categoryCreate.categoryName, monitor);
			}
			
			if(categoryCreate.teamAreaPath!=null)
			{
				
				List<String> path = Arrays.asList(categoryCreate.teamAreaPath.split("/"));
				
				TeamAreaHelper teamUtil = new TeamAreaHelper(repo,monitor);
				ITeamArea team = teamUtil.findTeamArea(projectArea, path);
				if (team==null)
				{
					throw new TeamRepositoryException("Team Area value "+categoryCreate.teamAreaPath+" does not exist.");
				}

				((Category) cat).getTeamAreas().removeAll(((Category) cat).getTeamAreas());
				((Category) cat).getTeamAreas().add(team);
				((Category) cat).setDefaultTeamArea(team);
			}
	
			
			 workItemClient.saveCategory(cat, monitor);
			 monitor.subTask("Created category "+categoryCreate.categoryName+" in project area "+categoryCreate.projectArea);
			 
	  }
	  
	  
	  public static void associateCategoryWithTeamArea(ITeamRepository repo, IProgressMonitor monitor, String projectName, String categoryPath, String teamAreaPath) throws TeamRepositoryException {
		  	IProcessClientService processClient = (IProcessClientService) repo.getClientLibrary(IProcessClientService.class);
		  	IProcessItemService service = (IProcessItemService) repo.getClientLibrary(IProcessItemService.class);
		  	
			URI uri = URI.create(projectName.replaceAll(" ", "%20"));
			IProjectArea projectArea = (IProjectArea) processClient.findProcessArea(uri, null, monitor);
			
			if (projectArea == null) {
				throw new TeamRepositoryException("Project area " + projectName+ " does not exist.");
			}
			
			IWorkItemClient workItemClient = (IWorkItemClient) repo.getClientLibrary(IWorkItemClient.class); 
			
			ICategory cato=null;
			if(categoryPath!=null)
			{
				
				List<String> path = Arrays.asList(categoryPath.split("/"));
				
				CategoryHelper catUtil = new CategoryHelper(repo, monitor);
				cato = catUtil.findCategory(projectArea, path);
				if (cato==null)
				{
					throw new TeamRepositoryException("Category value "+categoryPath+" does not exist.");
				}
				
			}

			if(teamAreaPath!=null)
			{
				
				List<String> path = Arrays.asList(teamAreaPath.split("/"));
				
				TeamAreaHelper teamUtil = new TeamAreaHelper(repo,monitor);
				ITeamArea team = teamUtil.findTeamArea(projectArea, path);
				if (team==null)
				{
					throw new TeamRepositoryException("Team Area value "+teamAreaPath+" does not exist.");
				}
				cato = (ICategory)cato.getWorkingCopy();
				((Category) cato).getTeamAreas().removeAll(((Category) cato).getTeamAreas());
				((Category) cato).getTeamAreas().add(team);
				((Category) cato).setDefaultTeamArea(team);
			}
	
			
			 workItemClient.saveCategory(cato, monitor);
			 monitor.subTask("Associated category "+categoryPath+" from project area "+projectName+" with team area "+teamAreaPath);
			 
	  }
	  
	  public static void createPlan(ITeamRepository repo, IProgressMonitor monitor, PlanCreate planCreate) throws TeamRepositoryException {
		  	IProcessClientService processClient = (IProcessClientService) repo.getClientLibrary(IProcessClientService.class);
		  	 
			URI uri = URI.create(planCreate.projectAreaName.replaceAll(" ", "%20"));
			IProjectArea projectArea = (IProjectArea) processClient.findProcessArea(uri, null, monitor);
			if (projectArea == null) {
				throw new TeamRepositoryException("Project area " + planCreate.projectAreaName+ " does not exist.");
			}
			
			
			// Iteration
			List<String> path = Arrays.asList(planCreate.iterationPath.split("/"));
        
			DevelopmentLineHelper devLineUtil = new DevelopmentLineHelper(repo, monitor);
			IIteration found = devLineUtil.findIteration(projectArea, path , devLineUtil.BYLABEL);
			if (found == null) {
				found = devLineUtil.findIteration(projectArea, path , devLineUtil.BYNAME);
			}
			if (found == null) {
				found = devLineUtil.findIteration(projectArea, path , devLineUtil.BYID);
			}
			if (found==null)
			{
				throw new TeamRepositoryException("Iteration value "+planCreate.iterationPath+" does not exist.");
			}
			
			ITeamArea team=null;
			if(planCreate.teamAreaPath!=null)
			{
				
				List<String> teamPath = Arrays.asList(planCreate.teamAreaPath.split("/"));
				
				TeamAreaHelper teamUtil = new TeamAreaHelper(repo,monitor);
				team = teamUtil.findTeamArea(projectArea, teamPath);
				if (team==null)
				{
					throw new TeamRepositoryException("Team Area value "+planCreate.teamAreaPath+" does not exist.");
				}
			}

			IProcessItemService service = (IProcessItemService) repo.getClientLibrary(IProcessItemService.class);
			projectArea = (IProjectArea) service.getMutableCopy(projectArea);
			
			try {
				// get classes for plan creation
				IAuditableCommon auditableCommon = (IAuditableCommon) repo.getClientLibrary(IAuditableCommon.class);
				// The IIterationPlanService - this is an internal API class
				IIterationPlanService planService = (IIterationPlanService) ((IClientLibraryContext) repo).getServiceInterface(IIterationPlanService.class);
				
				// The IIterationPlanRecord
				
				IIterationPlanRecord plan = (IIterationPlanRecord) IIterationPlanRecord.ITEM_TYPE.createItem();
				// setup plan values
				plan.setName(planCreate.planName);
				plan.setIteration(found);
				plan.setPlanType(planCreate.planType);
	
				
				if (team==null)
				{
					plan.setOwner(projectArea);
				}
				else{
					plan.setOwner(team);
				}
				
				// The IWikiPage - this is an internal API class
				IWikiPage wiki = (IWikiPage) IWikiPage.ITEM_TYPE.createItem();

				// setup wiki page
				String encoding = "UTF8";
				String xmlText = "";
				byte[] bytes = xmlText.getBytes(encoding);
				InputStream inputStream = new ByteArrayInputStream(bytes);

				// The IWikiPage methods - these methods are all internal API
				wiki.setName("");
				wiki.setWikiID(IIterationPlanRecord.OVERVIEW_PAGE_ID);
				wiki.setCreator(repo.loggedInContributor());
				wiki.setOwner(plan);
				wiki.setContent(auditableCommon.storeContent(IContent.CONTENT_TYPE_TEXT,
						encoding, inputStream, bytes.length, monitor));

				// save plan
				// The these classes and methods are all internal API
				DTO_IterationPlanSaveResult saveResult = planService.save(projectArea,plan, wiki);


				if (saveResult.isSetIterationPlanRecord() == true) {
				monitor.subTask("Created plan "+planCreate.planName+" for iteration "+planCreate.iterationPath+" in project area "+planCreate.projectAreaName);
				}
				
			
			} catch (UnsupportedEncodingException e) {
			}
	  }

}
