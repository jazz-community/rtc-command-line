package com.cli.ccm;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;

import com.ibm.team.process.common.IProjectArea;
import com.ibm.team.repository.client.ITeamRepository;
import com.ibm.team.repository.common.TeamRepositoryException;
import com.ibm.team.workitem.client.IWorkItemClient;
import com.ibm.team.workitem.common.model.ICategory;


public class CategoryHelper {
	

	private ITeamRepository fTeamRepository;
	private IProgressMonitor fMonitor;

	public CategoryHelper(ITeamRepository teamRepository,
			IProgressMonitor monitor) {
		fTeamRepository = teamRepository;
		fMonitor = monitor;
	}
	
	public ICategory findCategory(IProjectArea projectArea,List<String> path) throws TeamRepositoryException {
		
		IWorkItemClient service = (IWorkItemClient) fTeamRepository.getClientLibrary(IWorkItemClient.class);
		List<ICategory> cat = service.findCategories(projectArea, ICategory.FULL_PROFILE, fMonitor);
		
		ICategory category = this.findCategory(cat, path, 0);
	
		return category;
	}
	
	public String getCategoryPath(ICategory cat)
	{
		if (cat==null)return null;
		else
		{
			return cat.getHierarchicalName();
		}
	}
	
	private ICategory findCategory(List<ICategory> cat, List<String> path, int depth)
	{
		if (path.size()>0)
		{
			String name = path.get(0);
			for (ICategory cato:cat)
			{
				if(cato.getName().equals(name))
				{
					if(path.size()==1)
					{
						return cato;
					}
					else 
					{
						List<String> tmp = path.subList(1, path.size());
						List<ICategory> tmpCat = new ArrayList<ICategory>();
							for (ICategory catot:cat)
							{
								if (catot.getDepth()!=depth)
								{
									tmpCat.add(catot);
								}
							}
							depth++;
							return this.findCategory(tmpCat, tmp,depth);
					}
					}
				}
			}
			return null;
	}



}
