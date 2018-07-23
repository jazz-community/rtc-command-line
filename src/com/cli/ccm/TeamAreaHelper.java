package com.cli.ccm;

import java.util.List;
import java.util.Set;

import org.eclipse.core.runtime.IProgressMonitor;

import com.ibm.team.process.common.IProjectArea;
import com.ibm.team.process.common.ITeamArea;
import com.ibm.team.process.common.ITeamAreaHandle;
import com.ibm.team.repository.client.IItemManager;
import com.ibm.team.repository.client.ITeamRepository;
import com.ibm.team.repository.common.TeamRepositoryException;


public class TeamAreaHelper {
	

	private ITeamRepository fTeamRepository;
	private IProgressMonitor fMonitor;

	public TeamAreaHelper(ITeamRepository teamRepository,
			IProgressMonitor monitor) {
		fTeamRepository = teamRepository;
		fMonitor = monitor;
	}
	
	public ITeamArea findTeamArea(IProjectArea projectArea,List<String> path) throws TeamRepositoryException {

		if (path.size()>0)
		{
			String name = path.get(0);
			Set rootAreas = projectArea.getTeamAreaHierarchy().getRoots();
			for (Object tmpObj : rootAreas.toArray())
			{
				ITeamAreaHandle handle = (ITeamAreaHandle)tmpObj;
				ITeamArea teamArea = (ITeamArea) fTeamRepository.itemManager().fetchCompleteItem(handle, IItemManager.DEFAULT, null);
				if (name.equals(teamArea.getName()))
				{
					if (path.size()==1){
						return teamArea;
					}
					else
					{
						List<String> tmp = path.subList(1, path.size());
						Set children = projectArea.getTeamAreaHierarchy().getChildren(handle);
						return findTeamArea(projectArea,children.toArray(), tmp);
					}
				}
			}
			return null;
		}	
		else return null;
	}
	
	
	private ITeamArea findTeamArea(IProjectArea projectArea,Object[] teams, List<String> path) throws TeamRepositoryException
	{
		if (path.size()>0)
		{
			String name = path.get(0);
			for (Object tmpObj : teams)
			{
				ITeamAreaHandle handle = (ITeamAreaHandle)tmpObj;
				ITeamArea teamArea = (ITeamArea) fTeamRepository.itemManager().fetchCompleteItem(handle, IItemManager.DEFAULT, null);
				if (name.equals(teamArea.getName()))
				{
					if (path.size()==1){
						return teamArea;
					}
					else
					{
						List<String> tmp = path.subList(1, path.size());
						Set children = projectArea.getTeamAreaHierarchy().getChildren(handle);
						return findTeamArea(projectArea,children.toArray(), tmp);
					}
				}
			}
			
		}
		return null;
	}



}
