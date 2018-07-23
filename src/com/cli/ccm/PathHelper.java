/*******************************************************************************
 * Licensed Materials - Property of IBM
 * (c) Copyright IBM Corporation 2012. All Rights Reserved. 
 *
 * Note to U.S. Government Users Restricted Rights:  Use, duplication or 
 * disclosure restricted by GSA ADP Schedule Contract with IBM Corp.
 *******************************************************************************/
package com.cli.ccm;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;

import com.ibm.team.process.common.IDevelopmentLine;
import com.ibm.team.process.common.IDevelopmentLineHandle;
import com.ibm.team.process.common.IIteration;
import com.ibm.team.process.common.IIterationHandle;
import com.ibm.team.repository.client.IItemManager;
import com.ibm.team.repository.client.ITeamRepository;
import com.ibm.team.repository.common.IFetchResult;
import com.ibm.team.repository.common.TeamRepositoryException;

/**
 * Helps to create a path representation of an iteration that build up a logical
 * path down to the root development line. Handles three parallel path
 * representations based on an path created from ID's, from a name and from a
 * label. Allows to output the path using a separator that can be customized. *
 * 
 * 
 */
public class PathHelper {

	List<String> fIdPath = null;
	List<String> fNamePath = null;
	List<String> fLabelPath = null;
	ITeamRepository fTeamRepository = null;
	IProgressMonitor fProgressMonitor = null;

	String fSeperator = "/";

	public PathHelper(ITeamRepository teamRepository,
			IProgressMonitor progressMonitor) {
		super();
		fTeamRepository = teamRepository;
		fProgressMonitor = progressMonitor;
	}

	private void initialize() {
		fIdPath = new ArrayList<String>();
		fNamePath = new ArrayList<String>();
		fLabelPath = new ArrayList<String>();
	}

	/**
	 * Calculate the path for the iteration. Make paths available for ID's displaynames and labels 
	 * 
	 * @param handle
	 * @throws TeamRepositoryException
	 */
	public void calculateIterationPath(IIterationHandle handle)
			throws TeamRepositoryException {
		initialize();
		if(handle==null){
			return;
		}
		IIteration iteration = resolveIterationHandle(handle);
		getIterationPath(iteration);
	}

	/**
	 * @return the calculated path with names or null if the calculation never ran.
	 */
	public List<String> getNamePath() {
		return fNamePath;
	}

	/**
	 * @return  the calculated path with ID's or null if the calculation never ran.
	 */
	public List<String> getIdPath() {
		return fIdPath;
	}

	/**
	 * @return  the calculated path with labels or null if the calculation never ran.
	 */
	public List<String> getLabelPath() {
		return fLabelPath;
	}
	
	/**
	 * @return the id path as string or null, if it was never created.
	 */
	public String toIDPathString() {
		return toPathString(fIdPath);
	}

	/**
	 * @return the label path as string or null, if it was never created.
	 */
	public String toLabelPathString() {
		return toPathString(fLabelPath);
	}

	/**
	 * @return the name path as string or null, if it was never created.
	 */
	public String toNamePathString() {
		return toPathString(fNamePath);
	}
	
	/**
	 * @return the seperator used to build up the string representation
	 */
	public String getSeperator() {
		return fSeperator;
	}

	/**
	 * Set a seperator string used to build up the string representation
	 * 
	 * @param fSeperator
	 */
	public void setSeperator(String seperator) {
		this.fSeperator = seperator;
	}

	/**
	 * Find the path to an iteration using the iteration handle
	 * 
	 * @param iteration
	 * @throws TeamRepositoryException
	 */
	private void getIterationPath(IIteration iteration)
			throws TeamRepositoryException {
		this.add(0, iteration.getId(), iteration.getName(),
				iteration.getLabel());
		IIterationHandle parentHandle = iteration.getParent();
		if (parentHandle != null) {
			IIteration parent = resolveIterationHandle(parentHandle);
			getIterationPath(parent);
		} else {
			IDevelopmentLineHandle devLineHandle = iteration
					.getDevelopmentLine();
			List<IDevelopmentLineHandle> handles = new ArrayList<IDevelopmentLineHandle>();
			handles.add(devLineHandle);
			IFetchResult result = getTeamRepository().itemManager()
					.fetchCompleteItemsPermissionAware(handles,
							IItemManager.REFRESH, getProgressMonitor());
			IDevelopmentLine developmentLine = (IDevelopmentLine) result
					.getRetrievedItems().get(0);
			add(0, developmentLine.getId(), developmentLine.getName(),
					developmentLine.getLabel());
		}
	}

	/**
	 * Get the iteration from its handle
	 * 
	 * @param handle
	 * @return
	 * @throws TeamRepositoryException
	 */
	private IIteration resolveIterationHandle(IIterationHandle handle)
			throws TeamRepositoryException {
		List<IIterationHandle> handles = new ArrayList<IIterationHandle>();
		handles.add(handle);
		IFetchResult result = getTeamRepository().itemManager()
				.fetchCompleteItemsPermissionAware(handles,
						IItemManager.REFRESH, getProgressMonitor());
		return (IIteration) result.getRetrievedItems().get(0);
	}
	
	private IProgressMonitor getProgressMonitor() {
		return fProgressMonitor;
	}

	private ITeamRepository getTeamRepository() {
		return fTeamRepository;
	}

	
	/**
	 * Add a path segment to the path
	 * 
	 * @param i
	 * @param id
	 * @param name
	 * @param label
	 */
	private void add(int i, String id, String name, String label) {
		fIdPath.add(i, id);
		fNamePath.add(i, name);
		fLabelPath.add(i, label);
	}

	/**
	 * @param list
	 * @return
	 */
	private String toPathString(List<String> list) {
		boolean first = true;
		String outStr = "";
		for (Iterator<String> iterator = list.iterator(); iterator.hasNext();) {
			String name = (String) iterator.next();
			if (first) {
				first = false;
			} else {
				outStr += fSeperator;
			}
			outStr += name;
		}
		return outStr;
	}
}
