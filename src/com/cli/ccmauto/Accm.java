package com.cli.ccmauto;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.varia.NullAppender;
import org.eclipse.core.runtime.IProgressMonitor;
import com.beust.jcommander.JCommander;
import com.beust.jcommander.JCommander.Builder;
import com.cli.ccm.CLMConfig;
import com.cli.ccm.ProjectManager;
import com.cli.ccm.SCMManager;
import com.cli.ccm.SysoutProgressMonitor;
import com.cli.ccm.WorkItemManager;
import com.cli.ccm.connection.AuthConfigUtil;
import com.cli.ccm.connection.ConnectionManager;
import com.cli.ccmauto.cli.CategoryCreate;
import com.cli.ccmauto.cli.ComponentCreateStream;
import com.cli.ccmauto.cli.ComponentCreateWorkspace;
import com.cli.ccmauto.cli.DeleteConfig;
import com.cli.ccmauto.cli.DevelopmentLineArchive;
import com.cli.ccmauto.cli.DevelopmentLineCreate;
import com.cli.ccmauto.cli.IterationArchive;
import com.cli.ccmauto.cli.IterationCreate;
import com.cli.ccmauto.cli.PlanCreate;
import com.cli.ccmauto.cli.ProjectAreaCreate;
import com.cli.ccmauto.cli.RegisterConfig;
import com.cli.ccmauto.cli.Settings;
import com.cli.ccmauto.cli.StreamCreate;
import com.cli.ccmauto.cli.Test;
import com.cli.ccmauto.cli.WICommentAdd;
import com.cli.ccmauto.cli.WorkItemChangeState;
import com.cli.ccmauto.cli.WorkItemChangeStateAction;
import com.cli.ccmauto.cli.WorkItemCreate;
import com.cli.ccmauto.cli.WorkItemCreateFile;
import com.cli.ccmauto.cli.WorkItemCreateTemplate;
import com.cli.ccmauto.cli.WorkItemLink;
import com.cli.ccmauto.cli.WorkItemModify;
import com.cli.ccmauto.cli.WorkItemModifyFile;
import com.cli.ccmauto.cli.WorkspaceCreateStream;
import com.beust.jcommander.ParameterException;
import com.ibm.team.repository.client.ITeamRepository;
import com.ibm.team.repository.client.TeamPlatform;
import com.ibm.team.repository.common.TeamRepositoryException;

public class ACCM {

	public static void main(String[] args) {
		org.apache.log4j.BasicConfigurator.configure(new NullAppender());
	    Settings settings = new Settings();
	    
	    //Auth and administration commands
	    RegisterConfig registerConfig = new RegisterConfig();
	    DeleteConfig deleteConfig = new DeleteConfig();
	    Test test = new Test();
	    
	    // RTC commands
	    CategoryCreate categoryCreate = new CategoryCreate();
	    ComponentCreateStream componentCreateStream = new ComponentCreateStream();
	    ComponentCreateWorkspace componentCreateWorkspace = new ComponentCreateWorkspace();
	    DevelopmentLineArchive devLineArchive = new DevelopmentLineArchive();
	    DevelopmentLineCreate devLineCreate = new DevelopmentLineCreate();
	    IterationArchive itorArchive = new IterationArchive();
	    IterationCreate itorCreate = new IterationCreate();
	    PlanCreate planCreate = new PlanCreate();
	    ProjectAreaCreate projectCreate = new ProjectAreaCreate();
	    StreamCreate streamCreate = new StreamCreate();
	    WICommentAdd addComment = new WICommentAdd();
	    WorkItemChangeState wiChangeS = new WorkItemChangeState();
	    WorkItemChangeStateAction wiChangeA = new WorkItemChangeStateAction();
	    WorkItemCreate wiCreate = new WorkItemCreate();
	    WorkItemCreateTemplate wiTemplateCreate = new WorkItemCreateTemplate();
	    WorkItemLink wiLink = new WorkItemLink();
	    WorkItemModify wiModify = new WorkItemModify();
	    WorkspaceCreateStream workspaceStream = new WorkspaceCreateStream();

	    
	    Builder build = JCommander.newBuilder();
	    
//	    WorkItemCreateFile wiCreateFile = new WorkItemCreateFile();
//	    WorkItemModifyFile wiModifyFile = new WorkItemModifyFile();
	    
	    
	    JCommander jCommander = build.addObject(settings).
	    		addCommand("add-comment", addComment).
	    		addCommand("archive-iteration", itorArchive).
	    		addCommand("archive-devline", devLineArchive).
	    		addCommand("change-state-wi", wiChangeS).
	    		addCommand("change-state-wi-action", wiChangeA).
	    		addCommand("create-category", categoryCreate).
	    		addCommand("create-component-in-stream", componentCreateStream).
	    		addCommand("create-component-in-workspace", componentCreateWorkspace).
	    		addCommand("create-devline",devLineCreate).
	    		addCommand("create-iteration",itorCreate). 
	    		addCommand("create-plan",planCreate).
	    		addCommand("create-project", projectCreate).
	    		addCommand("create-stream", streamCreate).
	    		addCommand("create-wi", wiCreate).
	    		/*addCommand("create-wi-file", wiCreateFile).*/
	    		addCommand("create-wi-link", wiLink).
	    		addCommand("create-workitem-template", wiTemplateCreate).
	    		addCommand("create-workspace-from-stream", workspaceStream).
	    		addCommand("delete-config", deleteConfig).
	    		addCommand("modify-wi", wiModify).
	    		addCommand("test", test).
	    		addCommand("register-config", registerConfig).
	    		/*addCommand("modify-wi-file", wiModifyFile).*/

	    		build();
	    jCommander.setProgramName("accm.bat / accm.sh");
	    
		try {
			jCommander.parse(args);
			if (settings.help || jCommander.getParsedCommand() == null) {
				build.build().usage();
				System.exit(0);
			} else if (jCommander.getParsedCommand().equals("create-wi")) {
				IProgressMonitor monitor = new SysoutProgressMonitor();
		        TeamPlatform.startup();
		        try {     
		            CLMConfig loadClmConfig = AuthConfigUtil.loadConfig();
		            ITeamRepository repo = ConnectionManager.login(monitor,loadClmConfig.getRepoURL(),loadClmConfig.getUsername(),loadClmConfig.getPassword());
		            WorkItemManager.createWorkItem(repo, monitor, wiCreate);
		            
		        } catch (Exception e) {
		            System.out.println(e.getMessage());
		        } finally {
		        	monitor.subTask("Disconnecting ...");
		        	TeamPlatform.shutdown();
		            
		        }
			} 
			
			else if (jCommander.getParsedCommand().equals("modify-wi")) {
		        TeamPlatform.startup();
		        IProgressMonitor monitor = new SysoutProgressMonitor();
		        try {     
		        	CLMConfig loadClmConfig = AuthConfigUtil.loadConfig();
		            ITeamRepository repo = ConnectionManager.login(monitor,loadClmConfig.getRepoURL(),loadClmConfig.getUsername(),loadClmConfig.getPassword());
		            WorkItemManager.modifyWorkItem(repo, monitor, wiModify);
		        } catch (TeamRepositoryException e) {
		        	System.out.println(e.getMessage());
		        } 
				catch (Exception e) {
		        	System.out.println(e.getMessage());
		        } finally {
		        	monitor.subTask("Disconnecting ...");
		            TeamPlatform.shutdown();
		        }
			} 
			else if (jCommander.getParsedCommand().equals("create-workspace-from-stream")) {
		        TeamPlatform.startup();
		        IProgressMonitor monitor = new SysoutProgressMonitor();
		        try {     
		        	CLMConfig loadClmConfig = AuthConfigUtil.loadConfig();
		            ITeamRepository repo = ConnectionManager.login(monitor,loadClmConfig.getRepoURL(),loadClmConfig.getUsername(),loadClmConfig.getPassword());
		            if(workspaceStream.snapshotName==null)
		            {
		            	SCMManager.createWorkspaceFromStream(repo, monitor, workspaceStream);
		            }
		            else{
		            	SCMManager.createWorkspaceFromSnapshot(repo, monitor, workspaceStream);
		            }
		        } catch (Exception e) {
		            System.out.println(e.getMessage());
		        } 
		        finally {
		        	monitor.subTask("Disconnecting ...");
		            TeamPlatform.shutdown();
		        }
			} 
			else if (jCommander.getParsedCommand().equals("create-workitem-template")) {
		        TeamPlatform.startup();
		        IProgressMonitor monitor = new SysoutProgressMonitor();
		        try {     
		            
		        	CLMConfig loadClmConfig = AuthConfigUtil.loadConfig();
		            ITeamRepository repo = ConnectionManager.login(monitor,loadClmConfig.getRepoURL(),loadClmConfig.getUsername(),loadClmConfig.getPassword());
		            WorkItemManager.createWorkItemFromTemplate(repo, monitor, wiTemplateCreate);

		        } catch (Exception e) {
		            System.out.println(e.getMessage());
		        } 
		        finally {
		        	monitor.subTask("Disconnecting ...");
		            TeamPlatform.shutdown();
		        }
			} 
			else if (jCommander.getParsedCommand().equals("delete-config")) {
				IProgressMonitor monitor = new SysoutProgressMonitor();
		        try {     
		        	AuthConfigUtil.removeConfig();
		        	monitor.subTask("Config file was successfully removed.");
		        }
		        	catch (Exception e) {
		        	monitor.subTask("Repo configuration file does not exist or accm was not able to remove it. Please use accm register-config command to register repository");
		        } 
			} 
			else if (jCommander.getParsedCommand().equals("register-config")) {
					IProgressMonitor monitor = new SysoutProgressMonitor();	
					CLMConfig conf = new CLMConfig();
		        	conf.setRepoURL(registerConfig.repository);
		        	conf.setUsername(registerConfig.username);
		        	conf.setPassword(registerConfig.password);
		        	AuthConfigUtil.saveCLMConfig(conf);
		        	monitor.subTask("Config file was successfully registered.");
			} 
			
			else if (jCommander.getParsedCommand().equals("change-state-wi-action")) {
		        TeamPlatform.startup();
		        IProgressMonitor monitor = new SysoutProgressMonitor();
		        try {     
		        	CLMConfig loadClmConfig = AuthConfigUtil.loadConfig();
		            ITeamRepository repo = ConnectionManager.login(monitor,loadClmConfig.getRepoURL(),loadClmConfig.getUsername(),loadClmConfig.getPassword());
		            WorkItemManager.changeWorkItemStateUsingAction(repo, monitor, wiChangeA);
		        } catch (Exception e) {
		            System.out.println(e.getMessage());
		        } 
		        finally {
		        	monitor.subTask("Disconnecting ...");
		            TeamPlatform.shutdown();
		        }
			} 
			else if (jCommander.getParsedCommand().equals("create-component-in-stream")) {
		        TeamPlatform.startup();
		        IProgressMonitor monitor = new SysoutProgressMonitor();
		        try {     
		        	CLMConfig loadClmConfig = AuthConfigUtil.loadConfig();
		            ITeamRepository repo = ConnectionManager.login(monitor,loadClmConfig.getRepoURL(),loadClmConfig.getUsername(),loadClmConfig.getPassword());
		             SCMManager.createComponentInStream(repo, monitor, componentCreateStream);
		            
		        } catch (Exception e) {
		            System.out.println(e.getMessage());
		        } 
		        finally {
		        	monitor.subTask("Disconnecting ...");
		            TeamPlatform.shutdown();
		        }
			} 
			else if (jCommander.getParsedCommand().equals("create-component-in-workspace")) {
		        TeamPlatform.startup();
		        IProgressMonitor monitor = new SysoutProgressMonitor();
		        try {     
		        	CLMConfig loadClmConfig = AuthConfigUtil.loadConfig();
		            ITeamRepository repo = ConnectionManager.login(monitor,loadClmConfig.getRepoURL(),loadClmConfig.getUsername(),loadClmConfig.getPassword());
		            SCMManager.createComponentInWorkspace(repo, monitor, componentCreateWorkspace);
		        } catch (Exception e) {
		            System.out.println(e.getMessage());
		        } 
		        finally {
		        	monitor.subTask("Disconnecting ...");
		            TeamPlatform.shutdown();
		        }
			} 
			else if (jCommander.getParsedCommand().equals("change-state-wi")) {
		        TeamPlatform.startup();
		        IProgressMonitor monitor = new SysoutProgressMonitor();
		        try {     
		        	CLMConfig loadClmConfig = AuthConfigUtil.loadConfig();
		            ITeamRepository repo = ConnectionManager.login(monitor,loadClmConfig.getRepoURL(),loadClmConfig.getUsername(),loadClmConfig.getPassword());
		            WorkItemManager.changeWorkItemStateUsingTargetState(repo, monitor, wiChangeS);
		        } catch (Exception e) {
		            System.out.println(e.getMessage());
		        } 
		        finally {
		        	monitor.subTask("Disconnecting ...");
		            TeamPlatform.shutdown();
		        }
			} 
			else if (jCommander.getParsedCommand().equals("create-iteration")) {
		        TeamPlatform.startup();
		        IProgressMonitor monitor = new SysoutProgressMonitor();
		        try {     
		            CLMConfig loadClmConfig = AuthConfigUtil.loadConfig();
		            ITeamRepository repo = ConnectionManager.login(monitor,loadClmConfig.getRepoURL(),loadClmConfig.getUsername(),loadClmConfig.getPassword());
		            ProjectManager.createIteration(repo, monitor, itorCreate);
		        	throw new TeamRepositoryException("");
		        } catch (Exception e) {
		            System.out.println(e.getMessage());
		        } finally {
		        	monitor.subTask("Disconnecting ...");
		            TeamPlatform.shutdown();
		        }
			} 
			else if (jCommander.getParsedCommand().equals("create-plan")) {
				IProgressMonitor monitor = new SysoutProgressMonitor();
		        TeamPlatform.startup();
		        try {     
		            CLMConfig loadClmConfig = AuthConfigUtil.loadConfig();
		            ITeamRepository repo = ConnectionManager.login(monitor,loadClmConfig.getRepoURL(),loadClmConfig.getUsername(),loadClmConfig.getPassword());
		            ProjectManager.createPlan(repo, monitor, planCreate);
		        }catch (Exception e) {
		        	System.out.println(e.getMessage());
				} 
		        finally {
		        	monitor.subTask("Disconnecting ...");
		            TeamPlatform.shutdown();
		        }
			} 
			else if (jCommander.getParsedCommand().equals("add-comment")) {
				IProgressMonitor monitor = new SysoutProgressMonitor();
		        TeamPlatform.startup();
		        try {     
		        	CLMConfig loadClmConfig = AuthConfigUtil.loadConfig();
		            ITeamRepository repo = ConnectionManager.login(monitor,loadClmConfig.getRepoURL(),loadClmConfig.getUsername(),loadClmConfig.getPassword());
		            WorkItemManager.addCommenttoWorkItem(repo, monitor, addComment);
		        } catch (Exception e) {
		            System.out.println(e.getMessage());
		        } finally {
		        	monitor.subTask("Disconnecting ...");
		            TeamPlatform.shutdown();
		        }
			} 
			else if (jCommander.getParsedCommand().equals("test")) {
		        TeamPlatform.startup();
		        IProgressMonitor monitor = new SysoutProgressMonitor();
		        try {   
		            CLMConfig loadClmConfig = AuthConfigUtil.loadConfig();
		            ITeamRepository repo = ConnectionManager.login(monitor,loadClmConfig.getRepoURL(),loadClmConfig.getUsername(),loadClmConfig.getPassword());
		            monitor.subTask("ACCM status ... [OK]");
		            
		        } catch (TeamRepositoryException e) {
		            System.out.println(e.getMessage());
		        } finally {
		        	monitor.subTask("Disconnecting ...");
		            TeamPlatform.shutdown();
		        }
			} 
			else if (jCommander.getParsedCommand().equals("create-stream")) {
		        TeamPlatform.startup();
		        IProgressMonitor monitor = new SysoutProgressMonitor();
		        try {     
		            CLMConfig loadClmConfig = AuthConfigUtil.loadConfig();
		            ITeamRepository repo = ConnectionManager.login(monitor,loadClmConfig.getRepoURL(),loadClmConfig.getUsername(),loadClmConfig.getPassword());
		            SCMManager.createStream(repo, monitor, streamCreate);
		        } catch (Exception e) {
		            System.out.println(e.getMessage());
		        } finally {
		            TeamPlatform.shutdown();
		            monitor.subTask("Disconnecting ...");
		        }
			} 
			else if (jCommander.getParsedCommand().equals("create-devline")) {
				IProgressMonitor monitor = new SysoutProgressMonitor();
		        TeamPlatform.startup();
		        try {     
		            CLMConfig loadClmConfig = AuthConfigUtil.loadConfig();
		            ITeamRepository repo = ConnectionManager.login(monitor,loadClmConfig.getRepoURL(),loadClmConfig.getUsername(),loadClmConfig.getPassword());
		            ProjectManager.createDevelopmentLine(repo, monitor, devLineCreate);
		        } catch (Exception e) {
		            System.out.println(e.getMessage());
		        } finally {
		        	monitor.subTask("Disconnecting ...");
		            TeamPlatform.shutdown();
		        }
			} 
			else if (jCommander.getParsedCommand().equals("archive-devline")) {
		        TeamPlatform.startup();
		        IProgressMonitor monitor = new SysoutProgressMonitor();
		        try {     
		            CLMConfig loadClmConfig = AuthConfigUtil.loadConfig();
		            ITeamRepository repo = ConnectionManager.login(monitor,loadClmConfig.getRepoURL(),loadClmConfig.getUsername(),loadClmConfig.getPassword());
		            ProjectManager.archiveDevelopmentLine(repo, monitor, devLineArchive);
		        } catch (Exception e) {
		            System.out.println(e.getMessage());
		        } finally {
		        	monitor.subTask("Disconnecting ...");
		            TeamPlatform.shutdown();
		        }

			}
			else if (jCommander.getParsedCommand().equals("create-category")) {
				IProgressMonitor monitor = new SysoutProgressMonitor();
		        TeamPlatform.startup();
		        try {
		        	CLMConfig loadClmConfig = AuthConfigUtil.loadConfig();
		            ITeamRepository repo = ConnectionManager.login(monitor,loadClmConfig.getRepoURL(),loadClmConfig.getUsername(),loadClmConfig.getPassword());
		            ProjectManager.createCategory(repo, monitor, categoryCreate);
		            
		        } catch (Exception e) {
		            System.out.println(e.getMessage());
		        } finally {
		        	monitor.subTask("Disconnecting ...");
		            TeamPlatform.shutdown();
		        }
			}
			else if (jCommander.getParsedCommand().equals("archive-iteration")) {
				IProgressMonitor monitor = new SysoutProgressMonitor();
		        TeamPlatform.startup();
		        try {     
		            
		            CLMConfig loadClmConfig = AuthConfigUtil.loadConfig();
		            ITeamRepository repo = ConnectionManager.login(monitor,loadClmConfig.getRepoURL(),loadClmConfig.getUsername(),loadClmConfig.getPassword());
		            ProjectManager.archiveIteration(repo, monitor,itorArchive);
		        } catch (Exception e) {
		            System.out.println(e.getMessage());
		        } finally {
		        	monitor.subTask("Disconnecting ...");
		            TeamPlatform.shutdown();
		        }

			}
			else if (jCommander.getParsedCommand().equals("create-wi-link")) {
				IProgressMonitor monitor = new SysoutProgressMonitor();
		        TeamPlatform.startup();
		        try {     
		            CLMConfig loadClmConfig = AuthConfigUtil.loadConfig();
		            ITeamRepository repo = ConnectionManager.login(monitor,loadClmConfig.getRepoURL(),loadClmConfig.getUsername(),loadClmConfig.getPassword());
		            WorkItemManager.createLinksBetweenWorkItems(repo, monitor, wiLink);
		        } catch (Exception e) {
		            System.out.println(e.getMessage());
		        } finally {
		        	monitor.subTask("Disconnecting ...");
		            TeamPlatform.shutdown();
		        }

			} 
			
			else if (jCommander.getParsedCommand().equals("create-project")) {
				IProgressMonitor monitor = new SysoutProgressMonitor();
		        TeamPlatform.startup();
		        try {     
		            CLMConfig loadClmConfig = AuthConfigUtil.loadConfig();
		            ITeamRepository repo = ConnectionManager.login(monitor,loadClmConfig.getRepoURL(),loadClmConfig.getUsername(),loadClmConfig.getPassword());
		            ProjectManager.createProject(repo,monitor,projectCreate);
		        } catch (Exception e) {
		            System.out.println(e.getMessage());
		        } finally {
		        	monitor.subTask("Disconnecting ...");
		            TeamPlatform.shutdown();
		        }
			}
			/*
			else if (jCommander.getParsedCommand().equals("create-wi-file")) {
			
			IProgressMonitor monitor = new SysoutProgressMonitor();
			if(wiCreateFile.example)
			{
				wiCreateFile.generateExampleFile();
			}
			else{
				
				List<WorkItemCreate> lista =  wiCreateFile.importWorkItems(wiCreateFile.inputFile);
				List<WorkItemCreate> success = new ArrayList<WorkItemCreate>();
				List<WorkItemCreate> fails = new ArrayList<WorkItemCreate>();
				if (lista.size()>0)
				{
					CLMConfig loadClmConfig = AuthConfigUtil.loadConfig();
					TeamPlatform.startup();	
					try {   
						
						ITeamRepository repo = ConnectionManager.login(monitor,loadClmConfig.getRepoURL(),loadClmConfig.getUsername(),loadClmConfig.getPassword());
						for (WorkItemCreate wi : lista)
						{
							try {  
							 wi.id = WorkItemManager.createWorkItem(repo, monitor, wi);
							 wi.creationStatus="Created";
							 success.add(wi);
							} catch (TeamRepositoryException e) {
					            System.out.println(e.getMessage());
					            wi.id=-1;
					            wi.creationStatus=e.getMessage();
					            fails.add(wi);
					        } 
						}
						if (wiCreateFile.outputFile!=null)
						{
							monitor.subTask("Generating output file "+wiCreateFile.outputFile+" ...");
							wiCreateFile.generateOutputFile(wiCreateFile.outputFile, fails,success);
						}
			            monitor.subTask("Disconnecting ...");
					} catch (TeamRepositoryException e) {
			            System.out.println(e.getMessage());
			        } finally {
			            TeamPlatform.shutdown();
			        }
				}
			}	
		} 
		
		else if (jCommander.getParsedCommand().equals("modify-wi-file")) {
			//Create a work item from filename
			if(wiModifyFile.example)
			{
				wiModifyFile.generateExampleFile();
			}
			else{
				List<WorkItemModify> lista =  wiModifyFile.importWorkItems(wiModifyFile.inputFile);
				List<WorkItemModify> success = new ArrayList<WorkItemModify>();
				List<WorkItemModify> fails = new ArrayList<WorkItemModify>();
				if (lista.size()>0)
				{
					CLMConfig loadClmConfig = AuthConfigUtil.loadConfig();
					TeamPlatform.startup();	
					try {   
						IProgressMonitor monitor = new SysoutProgressMonitor();
						ITeamRepository repo = ConnectionManager.login(monitor,loadClmConfig.getRepoURL(),loadClmConfig.getUsername(),loadClmConfig.getPassword());
						for (WorkItemModify wi : lista)
						{
							try {  
							 WorkItemManager.modifyWorkItem(repo, monitor, wi);
							 wi.modififcationStatus="Modified";
							 success.add(wi);
							} catch (TeamRepositoryException e) {
					            System.out.println(e.getMessage());
					            wi.modififcationStatus=e.getMessage();
					            fails.add(wi);
					        } 
						}
						if (wiModifyFile.outputFile!=null)
						{
							monitor.subTask("Generating output file "+wiModifyFile.outputFile+" ...");
							wiModifyFile.generateOutputFile(wiModifyFile.outputFile, fails,success);
						}
			            monitor.subTask("Disconnecting ...");
					} catch (TeamRepositoryException e) {
			            System.out.println(e.getMessage());
			        } finally {
			            TeamPlatform.shutdown();
			        }
				}
			}	
		}*/
			else {
			    System.out.println("Not supported comamnd. Please verify available command list.");
				build.build().usage();
				System.exit(0);
			}
	    }
	    catch (ParameterException ex)
	    {
	    	if (jCommander.getParsedCommand()==null)
	    	{
	    		build.build().usage();
	    	}
	    	else{
	    	build.build().usage(jCommander.getParsedCommand());
	    	}
	    	System.exit(0);
	    }
		
	    catch (NoSuchMethodError ex)
	    {
	    	System.out.println("Missing required parameter(s)");
	    	build.build().usage(jCommander.getParsedCommand());
	    	System.exit(0);
	    }
	}


}
