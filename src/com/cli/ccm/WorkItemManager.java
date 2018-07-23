package com.cli.ccm;

import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.runtime.IProgressMonitor;

import com.cli.ccmauto.cli.WICommentAdd;
import com.cli.ccmauto.cli.WorkItemChangeState;
import com.cli.ccmauto.cli.WorkItemChangeStateAction;
import com.cli.ccmauto.cli.WorkItemCreate;
import com.cli.ccmauto.cli.WorkItemCreateTemplate;
import com.cli.ccmauto.cli.WorkItemExportFile;
import com.cli.ccmauto.cli.WorkItemLink;
import com.cli.ccmauto.cli.WorkItemModify;
import com.ibm.team.foundation.common.text.XMLString;
import com.ibm.team.links.client.ILinkManager;
import com.ibm.team.links.common.IItemReference;
import com.ibm.team.links.common.ILink;
import com.ibm.team.process.client.IProcessClientService;
import com.ibm.team.process.common.IIteration;
import com.ibm.team.process.common.IProjectArea;
import com.ibm.team.repository.client.IItemManager;
import com.ibm.team.repository.client.ITeamRepository;
import com.ibm.team.repository.client.internal.ItemManager;
import com.ibm.team.repository.common.IContributor;
import com.ibm.team.repository.common.TeamRepositoryException;
import com.ibm.team.workitem.api.common.WorkItem;
import com.ibm.team.workitem.client.IDetailedStatus;
import com.ibm.team.workitem.client.IWorkItemClient;
import com.ibm.team.workitem.client.IWorkItemTemplateClient;
import com.ibm.team.workitem.client.WorkItemWorkingCopy;
import com.ibm.team.workitem.common.internal.template.AttributeVariable;
import com.ibm.team.workitem.common.model.IAttribute;
import com.ibm.team.workitem.common.model.IAttributeHandle;
import com.ibm.team.workitem.common.model.ICategory;
import com.ibm.team.workitem.common.model.IComment;
import com.ibm.team.workitem.common.model.IComments;
import com.ibm.team.workitem.common.model.IEnumeration;
import com.ibm.team.workitem.common.model.ILiteral;
import com.ibm.team.workitem.common.model.IState;
import com.ibm.team.workitem.common.model.IWorkItem;
import com.ibm.team.workitem.common.model.IWorkItemHandle;
import com.ibm.team.workitem.common.model.IWorkItemType;
import com.ibm.team.workitem.common.model.Identifier;
import com.ibm.team.workitem.common.template.IAttributeVariable;
import com.ibm.team.workitem.common.template.ITemplateAttributeIdentifiers;
import com.ibm.team.workitem.common.template.ITemplateTypeIdentifiers;
import com.ibm.team.workitem.common.template.IWorkItemTemplateHandle;
import com.ibm.team.workitem.common.workflow.IWorkflowAction;
import com.ibm.team.workitem.common.workflow.IWorkflowInfo;

public class WorkItemManager {
	
	  public static void changeWorkItemStateUsingTargetState(ITeamRepository repo, IProgressMonitor monitor, WorkItemChangeState changeS) throws TeamRepositoryException {
			IWorkItemClient service = (IWorkItemClient) repo.getClientLibrary(IWorkItemClient.class);
		    IWorkItem wi = service.findWorkItemById(Integer.parseInt(changeS.id), IWorkItem.FULL_PROFILE, monitor);
			if (wi == null) {
				throw new TeamRepositoryException("Work Item  " + changeS.id+ " does not exist.");
			}
			service.getWorkItemWorkingCopyManager().connect(wi, IWorkItem.FULL_PROFILE, monitor);
		    WorkItemWorkingCopy wc = service.getWorkItemWorkingCopyManager().getWorkingCopy(wi);
		    wi = wc.getWorkItem();
		        
			IWorkflowInfo workflowInfo = service.findWorkflowInfo(wi, monitor);

			if(workflowInfo==null)
			{
				throw new TeamRepositoryException("Work Item  " + Integer.parseInt(changeS.id)+ " doesn't have any workflow.");
			}
			
			Identifier<IWorkflowAction> [] actions = workflowInfo.getActionIds(wi.getState2());
			
			Set<String> list = new HashSet<String>();
			
			Identifier<IWorkflowAction> toChange = null;
			
			for (Identifier<IWorkflowAction> s : actions)
			{
				
				Identifier<IState> state = workflowInfo.getActionResultState(s);
				if (workflowInfo.getStateName(state).equals(changeS.targetState))
				{
					toChange = s;
					break;
				}
			}
			
			if(toChange==null)
			{
				throw new TeamRepositoryException("State transition to " + changeS.targetState+ " isn't possible for work item in the current state.");
			}
			service.getWorkItemWorkingCopyManager().getWorkingCopy(wi);

			wc.setWorkflowAction(toChange.getStringIdentifier());	
			IDetailedStatus s = wc.save(monitor);
			
			if(!s.isOK()) {
				throw new TeamRepositoryException("Error saving work item", s.getException());
			}
		  }
	  
	  public static void changeWorkItemStateUsingAction(ITeamRepository repo, IProgressMonitor monitor,WorkItemChangeStateAction wiChangeA) throws TeamRepositoryException {
			IWorkItemClient service = (IWorkItemClient) repo.getClientLibrary(IWorkItemClient.class);
		    IWorkItem wi = service.findWorkItemById(Integer.parseInt(wiChangeA.id), IWorkItem.FULL_PROFILE, monitor);
			if (wi == null) {
				throw new TeamRepositoryException("Work Item  " + wiChangeA.id+ " does not exist.");
			}
			service.getWorkItemWorkingCopyManager().connect(wi, IWorkItem.FULL_PROFILE, monitor);
		    WorkItemWorkingCopy wc = service.getWorkItemWorkingCopyManager().getWorkingCopy(wi);
		    wi = wc.getWorkItem();
		        
			IWorkflowInfo workflowInfo = service.findWorkflowInfo(wi, monitor);

			if(workflowInfo==null)
			{
				throw new TeamRepositoryException("Work Item  " + Integer.parseInt(wiChangeA.id)+ " doesn't have any workflow.");
			}
			
			Identifier<IWorkflowAction> [] actions = workflowInfo.getActionIds(wi.getState2());
			
			
			
			Identifier<IWorkflowAction> toChange = null;			
			
			for (Identifier<IWorkflowAction> s : actions)
			{
				if (workflowInfo.getActionName(s).equals(wiChangeA.actionName))
				{
					toChange = s;
					break;
				}
			}
			
			if(toChange==null)
			{
				throw new TeamRepositoryException("Action " + wiChangeA.actionName+ " isn't available for work item in the current state.");
			}
			service.getWorkItemWorkingCopyManager().getWorkingCopy(wi);

			wc.setWorkflowAction(toChange.getStringIdentifier());	
			IDetailedStatus s = wc.save(monitor);
			
			if(!s.isOK()) {
				throw new TeamRepositoryException("Error saving work item", s.getException());
			}
		  }
	
	  public static void addSubscriberToWorkItem(ITeamRepository repo, IProgressMonitor monitor, int workItemId, String subscriberName) throws TeamRepositoryException {
			IWorkItemClient service = (IWorkItemClient) repo.getClientLibrary(IWorkItemClient.class);
	        IWorkItem workItem = service.findWorkItemById(workItemId, IWorkItem.FULL_PROFILE, monitor);
			if (workItem == null) {
				throw new TeamRepositoryException("Work Item  " + workItemId+ " does not exist.");
			}
			service.getWorkItemWorkingCopyManager().connect(workItem, IWorkItem.FULL_PROFILE, monitor);
	        WorkItemWorkingCopy wc = service.getWorkItemWorkingCopyManager().getWorkingCopy(workItem);
	        workItem = wc.getWorkItem();
	        
	        // Adding subscribers
	        
	        if (subscriberName!=null)
			{
	        	IContributor contributor= repo.contributorManager().fetchContributorByUserId(subscriberName, monitor);
	    		if (contributor == null) {
	    			throw new TeamRepositoryException("Subscriber user id " + subscriberName+ " does not exist.");
	    		}
	    		workItem.getSubscriptions().add(contributor);
			}
	        
	        try {
	            IDetailedStatus s = wc.save(null);
	            if(!s.isOK()) {
	                throw new TeamRepositoryException("Error saving work item", s.getException());
	            }
	        } finally {
	            service.getWorkItemWorkingCopyManager().disconnect(workItem);
	        }
	        workItem = (IWorkItem) repo.itemManager().fetchCompleteItem(workItem, IItemManager.DEFAULT, monitor);    
	        monitor.subTask("Addded subscriber "+subscriberName+" to work item " + workItem.getId()+" - "+workItem.getHTMLSummary().getPlainText());
	        
	        
		  }
	
	 public static void createLinksBetweenWorkItems(ITeamRepository repo, IProgressMonitor monitor, WorkItemLink wiLink) throws TeamRepositoryException {	
			IWorkItemClient service = (IWorkItemClient) repo.getClientLibrary(IWorkItemClient.class);
			
			IWorkItem fromWi = service.findWorkItemById(wiLink.fromId, IWorkItem.DEFAULT_PROFILE, monitor);
			IWorkItem toWi = service.findWorkItemById(wiLink.toId, IWorkItem.DEFAULT_PROFILE, monitor);
			
			if (fromWi == null) {
				throw new TeamRepositoryException("Work Item  " + wiLink.fromId+ " does not exist.");
			}
			
			if (toWi == null) {
				throw new TeamRepositoryException("Work Item  " + wiLink.toId+ " does not exist.");
			}
			final ILinkManager linkManager = (ILinkManager) repo.getClientLibrary(ILinkManager.class);
			
			IItemReference source = linkManager.referenceFactory().createReferenceToItem(fromWi);
			IItemReference target = linkManager.referenceFactory().createReferenceToItem(toWi);
			
			ILink link = linkManager.createLink(wiLink.linkType, source, target);
			
			linkManager.saveLink(link, monitor);
			monitor.subTask("Create link "+wiLink.linkType+" from workitem #"+wiLink.fromId+" to workitem #"+wiLink.toId);
			
		    }
	  public static void addCommenttoWorkItem(ITeamRepository repo, IProgressMonitor monitor, WICommentAdd add) throws TeamRepositoryException {
			IWorkItemClient service = (IWorkItemClient) repo.getClientLibrary(IWorkItemClient.class);
	        IWorkItem workItem = service.findWorkItemById(add.id, IWorkItem.FULL_PROFILE, monitor);
			if (workItem == null) {
				throw new TeamRepositoryException("Work Item  " + add.id+ " does not exist.");
			}
			service.getWorkItemWorkingCopyManager().connect(workItem, IWorkItem.FULL_PROFILE, monitor);
	        WorkItemWorkingCopy wc = service.getWorkItemWorkingCopyManager().getWorkingCopy(workItem);
	        workItem = wc.getWorkItem();
	        //Setting attributes
	        try {
	            IComments comments = workItem.getComments();
	            IComment com = comments.createComment(repo.loggedInContributor(), XMLString.createFromXMLText(add.comment));
	            comments.append(com);
	            
	            IDetailedStatus s = wc.save(monitor);
	            if(!s.isOK()) {
	                throw new TeamRepositoryException("Error saving work item", s.getException());
	            }
	        } finally {
	            service.getWorkItemWorkingCopyManager().disconnect(workItem);
	        }
	        workItem = (IWorkItem) repo.itemManager().fetchCompleteItem(workItem, IItemManager.DEFAULT, monitor);    
	        monitor.subTask("Added comment to work item " + workItem.getId()+" - "+add.comment);
		  }
	  

	  public static int createWorkItem(ITeamRepository repo, IProgressMonitor monitor, WorkItemCreate wi) throws TeamRepositoryException {
		IProcessClientService processClient = (IProcessClientService) repo.getClientLibrary(IProcessClientService.class);
		URI uri = URI.create(wi.projectArea.replaceAll(" ", "%20"));
		IProjectArea projectArea = (IProjectArea) processClient.findProcessArea(uri, null, monitor);
		if (projectArea == null) {
			throw new TeamRepositoryException("Project area " + wi.projectArea+ " does not exist.");
		}

		IWorkItemClient service = (IWorkItemClient) repo.getClientLibrary(IWorkItemClient.class);
        IWorkItemType workItemType = service.findWorkItemType(projectArea, wi.type, monitor);
		if (workItemType == null) {
			throw new TeamRepositoryException("Work item type " + wi.type+ " does not exist.");
		}
		
        IWorkItemHandle handle = service.getWorkItemWorkingCopyManager().connectNew(workItemType, monitor);
        WorkItemWorkingCopy wc = service.getWorkItemWorkingCopyManager().getWorkingCopy(handle);
        IWorkItem workItem = wc.getWorkItem();
        
        //Setting attributes
        try {
        	
        	
        	if (wi.categoryPath!=null)
        	{
    			ICategory cato=null;
    			if(wi.categoryPath!=null)
    			{
    				
    				List<String> path = Arrays.asList(wi.categoryPath.split("/"));
    				
    				CategoryHelper catUtil = new CategoryHelper(repo, monitor);
    				cato = catUtil.findCategory(projectArea, path);
    				if (cato==null)
    				{
    					throw new TeamRepositoryException("Category value "+wi.categoryPath+" does not exist.");
    				}
    				workItem.setCategory(cato);
    			}
        	}
        	else{
	            List<ICategory> findCategories= service.findCategories(projectArea, ICategory.FULL_PROFILE, monitor);
	            ICategory category = findCategories.get(0);
	            workItem.setCategory(category);
        	}
            workItem.setCreator(repo.loggedInContributor());
            workItem.setHTMLSummary(XMLString.createFromPlainText(wi.summary));
            
            // Attrybuty
            
            // Description
            
            if (wi.description!=null)
            {
            	workItem.setHTMLDescription(XMLString.createFromPlainText(wi.description));
            }
            
            // Owner
            
            if (wi.owner!=null)
    		{
            	IContributor contributor= repo.contributorManager().fetchContributorByUserId(wi.owner, monitor);
        		if (contributor == null) {
        			throw new TeamRepositoryException("Owner user id " + wi.owner+ " does not exist.");
        		}
        		workItem.setOwner(contributor);
    		}
            
            // Priority
            
            if (wi.priority!=null)
    		{
            	IAttribute priorityAttr = service.findAttribute(projectArea, "internalPriority", monitor);
            	if (priorityAttr==null)
            	{
            		throw new TeamRepositoryException("Priority attribute does not exist.");
            	}
            	
            	Identifier id = WorkItemManager.getLiteralEqualsString(repo, wi.priority, priorityAttr);
            	if (id==null)
            	{
            		throw new TeamRepositoryException("Priority value "+wi.priority+" does not exist.");
            	}
            	workItem.setValue(priorityAttr, id);
    		}
            
            // Severity
            
            if (wi.severity!=null)
    		{
            	IAttribute severityAttr = service.findAttribute(projectArea, "internalSeverity", monitor);
            	if (severityAttr==null)
            	{
            		throw new TeamRepositoryException("Severity attribute does not exist.");
            	}
            	
            	Identifier id = WorkItemManager.getLiteralEqualsString(repo, wi.severity, severityAttr);
            	if (id==null)
            	{
            		throw new TeamRepositoryException("Severity value "+wi.severity+" does not exist.");
            	}
            	workItem.setValue(severityAttr, id);
    		}
            
            // Tags
            
            if (wi.tags!=null)
            {
            	workItem.setTags2(Arrays.asList(wi.tags.split(" ")));
            }
            
            // Duration
            
            if (wi.duration!=null)
            {
               workItem.setDuration(wi.duration.longValue());
            }
            
            // Due Date
            
            if (wi.dueDate!=null)
            {
            	java.sql.Timestamp timer = Util.convertStringToTimestamp(wi.dueDate);
            	workItem.setDueDate(timer);
            }
            
            // Planned For
            
            if (wi.plannedFor!=null)
    		{
            List<String> path = Arrays.asList(wi.plannedFor.split("/"));
            // map by ID
            
            DevelopmentLineHelper devLineUtil = new DevelopmentLineHelper(repo, monitor);
            IIteration found = devLineUtil.findIteration(projectArea, path , devLineUtil.BYLABEL);
            if (found == null) {
            	found = devLineUtil.findIteration(projectArea, path , devLineUtil.BYNAME);
            }
        	if (found==null)
        	{
        		throw new TeamRepositoryException("Iteration value "+wi.plannedFor+" does not exist.");
        	}
            workItem.setTarget(found);
    		}
            
            // Custom Attributes
            
            
            ////////////////////
            

            IDetailedStatus s = wc.save(monitor);
            if(!s.isOK()) {
                throw new TeamRepositoryException("Error saving work item", s.getException());
            }
        } finally {
            service.getWorkItemWorkingCopyManager().disconnect(workItem);
        }
        workItem = (IWorkItem) repo.itemManager().fetchCompleteItem(workItem, IItemManager.DEFAULT, monitor);    
        monitor.subTask("Created a work item " + workItem.getId()+" - "+workItem.getHTMLSummary().getPlainText());
        return workItem.getId();
	  }
	  
	  public static void modifyWorkItem(ITeamRepository repo, IProgressMonitor monitor, WorkItemModify wiModify) throws TeamRepositoryException {
		IWorkItemClient service = (IWorkItemClient) repo.getClientLibrary(IWorkItemClient.class);
        IWorkItem workItem = service.findWorkItemById(wiModify.id, IWorkItem.FULL_PROFILE, monitor);
		if (workItem == null) {
			throw new TeamRepositoryException("Work Item  " + wiModify.id+ " does not exist.");
		}
		service.getWorkItemWorkingCopyManager().connect(workItem, IWorkItem.FULL_PROFILE, monitor);
        WorkItemWorkingCopy wc = service.getWorkItemWorkingCopyManager().getWorkingCopy(workItem);
        workItem = wc.getWorkItem();
        
        //Setting attributes
        
        //Setting Summary 
        
        try {
        	if (wiModify.summary!=null)
        	{
        		if (wiModify.summary.length()>0)
        		{
        			workItem.setHTMLSummary(XMLString.createFromPlainText(wiModify.summary));
        		}
        	}
        	//Category
        	if (wiModify.categoryPath!=null)
        	{
    			ICategory cato=null;
    			if(wiModify.categoryPath!=null)
    			{
    				
    				List<String> path = Arrays.asList(wiModify.categoryPath.split("/"));
    				CategoryHelper catUtil = new CategoryHelper(repo, monitor);
    				IProjectArea projectArea = (IProjectArea) repo.itemManager().fetchCompleteItem(workItem.getProjectArea(), IItemManager.REFRESH,monitor);
    				cato = catUtil.findCategory(projectArea, path);
    				if (cato==null)
    				{
    					throw new TeamRepositoryException("Category value "+wiModify.categoryPath+" does not exist.");
    				}
    				workItem.setCategory(cato);
    			}
        	}
        	
    // Description
            
            if (wiModify.description!=null)
            {
            	workItem.setHTMLDescription(XMLString.createFromPlainText(wiModify.description));
            }
            
            // Owner
            
            if (wiModify.owner!=null)
    		{
            	IContributor contributor= repo.contributorManager().fetchContributorByUserId(wiModify.owner, monitor);
        		if (contributor == null) {
        			throw new TeamRepositoryException("Owner user id " + wiModify.owner+ " does not exist.");
        		}
        		workItem.setOwner(contributor);
    		}
            
            // Priority
            
            if (wiModify.priority!=null)
    		{
            	
            	
            	IAttribute priorityAttr = service.findAttribute(workItem.getProjectArea(), "internalPriority", monitor);
            	if (priorityAttr==null)
            	{
            		throw new TeamRepositoryException("Priority attribute does not exist.");
            	}
            	
            	Identifier id = WorkItemManager.getLiteralEqualsString(repo, wiModify.priority, priorityAttr);
            	if (id==null)
            	{
            		throw new TeamRepositoryException("Priority value "+wiModify.priority+" does not exist.");
            	}
            	workItem.setValue(priorityAttr, id);
    		}
            
            // Severity
            
            if (wiModify.severity!=null)
    		{
            	IAttribute severityAttr = service.findAttribute(workItem.getProjectArea(), "internalSeverity", monitor);
            	if (severityAttr==null)
            	{
            		throw new TeamRepositoryException("Severity attribute does not exist.");
            	}
            	
            	Identifier id = WorkItemManager.getLiteralEqualsString(repo, wiModify.severity, severityAttr);
            	if (id==null)
            	{
            		throw new TeamRepositoryException("Severity value "+wiModify.severity+" does not exist.");
            	}
            	workItem.setValue(severityAttr, id);
    		}
            
            // Tags
            
            if (wiModify.tags!=null)
            {
            	workItem.setTags2(Arrays.asList(wiModify.tags.split(" ")));
            }
            
            // Duration
            
            if (wiModify.duration!=null)
            {
               workItem.setDuration(wiModify.duration.longValue());
            }
            
            // Due Date
            
            if (wiModify.dueDate!=null)
            {
            	java.sql.Timestamp timer = Util.convertStringToTimestamp(wiModify.dueDate);
            	workItem.setDueDate(timer);
            }
            
            // Planned For
            
            if (wiModify.plannedFor!=null)
    		{
            List<String> path = Arrays.asList(wiModify.plannedFor.split("/"));
            // map by ID
            
            DevelopmentLineHelper devLineUtil = new DevelopmentLineHelper(repo, monitor);
            IIteration found = devLineUtil.findIteration(workItem.getProjectArea(), path , devLineUtil.BYLABEL);
            if (found == null) {
            	found = devLineUtil.findIteration(workItem.getProjectArea(), path , devLineUtil.BYNAME);
            }
        	if (found==null)
        	{
        		throw new TeamRepositoryException("Iteration value "+wiModify.plannedFor+" does not exist.");
        	}
            workItem.setTarget(found);
    		}
            
            // Custom Attributes
            
            
            ////////////////////

        	
        	
        	
            IDetailedStatus s = wc.save(monitor);
            if(!s.isOK()) {
                throw new TeamRepositoryException("Error saving work item", s.getException());
            }
        } finally {
            service.getWorkItemWorkingCopyManager().disconnect(workItem);
        }
        workItem = (IWorkItem) repo.itemManager().fetchCompleteItem(workItem, IItemManager.DEFAULT, monitor);    
        monitor.subTask("Modified a work item " + workItem.getId()+" - "+workItem.getHTMLSummary().getPlainText());
	  }
	  
	  public static void createWorkItemFromTemplate(ITeamRepository repo, IProgressMonitor monitor, WorkItemCreateTemplate wiCreateTemp) throws TeamRepositoryException {
			Map<IAttributeVariable, Object> attributes = new HashMap<IAttributeVariable, Object>();
			Map<String, String> parameters = new HashMap<String, String>();
			
			IAttributeVariable categoryAttribute = new AttributeVariable(ITemplateAttributeIdentifiers.WORKITEM_CATEGORY, ITemplateTypeIdentifiers.CATEGORY);
			IAttributeVariable iterationAttribute = new AttributeVariable(ITemplateAttributeIdentifiers.WORKITEM_ITERATION, ITemplateTypeIdentifiers.ITERATION);
			
			IProcessClientService processClient = (IProcessClientService) repo.getClientLibrary(IProcessClientService.class);
			IWorkItemClient wiService = (IWorkItemClient) repo.getClientLibrary(IWorkItemClient.class);
			URI uri = URI.create(wiCreateTemp.projectArea.replaceAll(" ", "%20"));
			IProjectArea projectArea = (IProjectArea) processClient.findProcessArea(uri, null, monitor);
			if (projectArea == null) {
				throw new TeamRepositoryException("Project area " + wiCreateTemp.projectArea+ " does not exist.");
			}
			
			IWorkItemTemplateClient service = (IWorkItemTemplateClient) repo.getClientLibrary(IWorkItemTemplateClient.class);
			
			List <IWorkItemTemplateHandle> handles = service.getTemplateHandles(projectArea, monitor);
			IWorkItemTemplateHandle handle = null;
			
			for (IWorkItemTemplateHandle itor : handles)
			{
				if(itor.getName().equals(wiCreateTemp.templateName))
				{
					handle = itor;
				}
			}
			if (handle == null) {
				throw new TeamRepositoryException("Template " + wiCreateTemp.templateName+ " does not exist.");
			}
			
			if (wiCreateTemp.plannedFor!=null)
  		{
          	
				List<String> path = Arrays.asList(wiCreateTemp.plannedFor.split("/"));
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
		      		throw new TeamRepositoryException("Iteration value "+wiCreateTemp.plannedFor+" does not exist.");
		      	 }

		      	 attributes.put(iterationAttribute, found);
  		}
			
			// CATEGORY
			
			if (wiCreateTemp.categoryPath!=null)
  		{
          	
				List<String> path = Arrays.asList(wiCreateTemp.categoryPath.split("/"));
		          // map by ID
		          
		        CategoryHelper catUtil = new CategoryHelper(repo, monitor);
		        ICategory found = catUtil.findCategory(projectArea, path);
		       
		          
		      	 if (found==null)
		      	 {
		      		throw new TeamRepositoryException("Category value "+wiCreateTemp.categoryPath+" does not exist.");
		      	 }
		      	 attributes.put(categoryAttribute, found);
  		}
			
			if (wiCreateTemp.keywords!=null)
			{
				for (String keyword:wiCreateTemp.keywords)
				{
					String [] keyAndValue = keyword.split("=");
					if (keyAndValue.length!=2)
					{
						throw new TeamRepositoryException("Keyword(s) (variables) are not properly defined. Example: -k \"keyword(variable name)=value with spaces\" or keyword=value. For multiple keywords -k \"keyword=value\" -k \"keyword2=value2\"");
					}
					else{
						parameters.put(keyAndValue[0], keyAndValue[1]);
					}
				}
			}			
			
			int [] id = service.instantiateTemplate(handle, attributes, parameters, monitor);

			monitor.subTask("Template " + wiCreateTemp.templateName+" was successfully instantiated. ACCM created "+id.length+" new work items in project "+wiCreateTemp.projectArea);
			
			
			for (int i : id)
			{
				IWorkItem workItem = wiService.findWorkItemById(i, IWorkItem.FULL_PROFILE, monitor);
				if (workItem == null) {
					throw new TeamRepositoryException("Work Item  " + i+ " does not exist.");
				}
				monitor.subTask(i+" - "+workItem.getHTMLSummary().getPlainText());
			}
	
	  }
	  
	  public static List<WorkItemExportFile> getWorkItemsById(ITeamRepository repo, IProgressMonitor monitor, List<String> ids) throws TeamRepositoryException {
		IWorkItemClient service = (IWorkItemClient) repo.getClientLibrary(IWorkItemClient.class);
		List<WorkItemExportFile> lista = new ArrayList<WorkItemExportFile>();
		if (ids==null) return lista;
		for (String id : ids)
		{
			IWorkItem workItem = service.findWorkItemById(Integer.parseInt(id), IWorkItem.FULL_PROFILE, monitor);
			if (workItem == null) {
				throw new TeamRepositoryException("Work Item  " + Integer.parseInt(id)+ " does not exist.");
			}
			
//			WorkItemExportFile tmp = new WorkItemExportFile();
//			tmp.id = Integer.parseInt(id);
//			tmp.projectArea;
//			tmp.summary = workItem.getHTMLSummary().getPlainText();
//			tmp.status = workItem.getS;
//			tmp.description = workItem.getHTMLDescription().getPlainText();
//			tmp.category;
//			tmp.owner;
//			tmp.priority;
//			tmp.severity;
//			tmp.tags;
//			tmp.duration;
//			tmp.dueDate;
//			tmp.plannedFor;
//			tmp.type;
		}
			
			return lista;
		}
	  
	  
	  private static Identifier getLiteralEqualsString(ITeamRepository repo, String name, IAttributeHandle ia) throws TeamRepositoryException {
			IWorkItemClient workItemClient = (IWorkItemClient) repo.getClientLibrary(IWorkItemClient.class);
			Identifier literalID = null;
			IEnumeration enumeration = workItemClient.resolveEnumeration(ia, null); // or IWorkitemCommon
			List literals = enumeration.getEnumerationLiterals();
			for (Iterator iterator = literals.iterator(); iterator.hasNext();) {
				ILiteral iLiteral = (ILiteral) iterator.next();
				if (iLiteral.getName().equals(name)) {
					literalID = iLiteral.getIdentifier2();
					break;
				}
			}
			return literalID;
		}  
}
