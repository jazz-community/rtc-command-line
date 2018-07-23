/*******************************************************************************
 * Licensed Materials - Property of IBM
 * (c) Copyright IBM Corporation 2012. All Rights Reserved. 
 *
 * Note to U.S. Government Users Restricted Rights:  Use, duplication or 
 * disclosure restricted by GSA ADP Schedule Contract with IBM Corp.
 *******************************************************************************/
package com.cli.ccm;

import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;

import com.ibm.team.process.common.IDevelopmentLine;
import com.ibm.team.process.common.IDevelopmentLineHandle;
import com.ibm.team.process.common.IIteration;
import com.ibm.team.process.common.IIterationHandle;
import com.ibm.team.process.common.IProjectArea;
import com.ibm.team.process.common.IProjectAreaHandle;
import com.ibm.team.repository.client.IItemManager;
import com.ibm.team.repository.client.ITeamRepository;
import com.ibm.team.repository.common.TeamRepositoryException;
import com.ibm.team.workitem.client.IAuditableClient;
import com.ibm.team.workitem.common.model.ItemProfile;

/**
 * Tries to find a development line and enclosed iteration for a project area. 
 * 
 *
 */
public class DevelopmentLineHelper {
	
	private enum Comparemode{BYID,BYNAME,BYLABEL};
	
	public static final Comparemode BYID = Comparemode.BYID;
	public static final Comparemode BYNAME = Comparemode.BYNAME;
	public static final Comparemode BYLABEL = Comparemode.BYLABEL;

	private ITeamRepository fTeamRepository;
	private IProgressMonitor fMonitor;
	private IAuditableClient fAuditableClient;

	public DevelopmentLineHelper(ITeamRepository teamRepository,
			IProgressMonitor monitor) {
		fTeamRepository = teamRepository;
		fMonitor = monitor;
	}
	
	/**
	 * Find a development line based on the path provided.
	 * 
	 * @param projectArea
	 * @param path
	 * @param byId search by id or name
	 * @return a development line found or null.
	 * @throws TeamRepositoryException
	 */
	public IDevelopmentLine findDevelopmentLine(IProjectArea projectArea,
			List<String> path, Comparemode comparemode) throws TeamRepositoryException {
		if (this.fAuditableClient==null)
		{
			fAuditableClient = (IAuditableClient) fTeamRepository.getClientLibrary(IAuditableClient.class);
		}
		int level = 0;
		String fookFor = path.get(level);
		IDevelopmentLineHandle[] developmentLineHandles = projectArea
				.getDevelopmentLines();
		for (IDevelopmentLineHandle developmentLineHandle : developmentLineHandles) {
			IDevelopmentLine developmentLine = fAuditableClient
					.resolveAuditable(developmentLineHandle,
							ItemProfile.DEVELOPMENT_LINE_DEFAULT, fMonitor);
			String compare = "";
			switch(comparemode){
			case BYID:
				compare = developmentLine.getId();
				break;
			case BYNAME:
				compare = developmentLine.getName();
				break;
			case BYLABEL:
				compare = developmentLine.getLabel();
				break;
			}
			if (fookFor.equals(compare)) {
				return developmentLine;
			}
		}
		return null;
	}


	/**
	 * Find an iteration based on the path provided.
	 * 
	 * @param iProjectAreaHandle
	 * @param path
	 * @param byId
	 * @return an iteration if one can be found or null otherwise
	 * 
	 * @throws TeamRepositoryException
	 */
	public IIteration findIteration(IProjectAreaHandle iProjectAreaHandle,
			List<String> path, Comparemode comparemode) throws TeamRepositoryException {
		if (this.fAuditableClient==null)
		{
			fAuditableClient = (IAuditableClient) fTeamRepository.getClientLibrary(IAuditableClient.class);
		}
		IIteration foundIteration = null;
		IProjectArea projectArea = (IProjectArea) fTeamRepository.itemManager()
				.fetchCompleteItem(iProjectAreaHandle, IItemManager.REFRESH,
						fMonitor);
		IDevelopmentLine developmentLine = findDevelopmentLine(projectArea,
				path, comparemode);
		if (developmentLine != null) {
			foundIteration = findIteration(developmentLine.getIterations(),
					path, 1, comparemode);
		}
		return foundIteration;
	}

	/**
	 * Find an Iteration
	 * 
	 * @param iterations
	 * @param path
	 * @param level
	 * @param comparemode
	 * @return
	 * @throws TeamRepositoryException
	 */
	private IIteration findIteration(IIterationHandle[] iterations,
			List<String> path, int level, Comparemode comparemode)
			throws TeamRepositoryException {
		String lookFor;
		try{
			lookFor = path.get(level);
		}
		catch (ArrayIndexOutOfBoundsException ex)
		{
			return null;
		}
		for (IIterationHandle iIterationHandle : iterations) {

			IIteration iteration = fAuditableClient.resolveAuditable(
					iIterationHandle, ItemProfile.ITERATION_DEFAULT, fMonitor);
			String compare = "";
			switch(comparemode){
			case BYID:
				compare = iteration.getId();
				break;
			case BYNAME:
				compare = iteration.getName();
				break;
			case BYLABEL:
				compare = iteration.getLabel();
				break;
			}
			if (lookFor.equals(compare)) {
				if (path.size() > level + 1) {
					IIteration found = findIteration(iteration.getChildren(),
							path, level + 1, comparemode);
					if (found != null) {
						return found;
					}
				} else {
					return iteration;
				}
			}
		}
		return null;
	}
}
