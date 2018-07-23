package com.cli.ccm.connection;

import org.eclipse.core.runtime.IProgressMonitor;

import com.ibm.team.repository.client.ITeamRepository;
import com.ibm.team.repository.client.TeamPlatform;
import com.ibm.team.repository.common.TeamRepositoryException;

public class ConnectionManager {
	
    public static ITeamRepository login(IProgressMonitor monitor,String repo, final String username,final String password) throws TeamRepositoryException {
        ITeamRepository repository = TeamPlatform.getTeamRepositoryService().getTeamRepository(repo);
        repository.registerLoginHandler(new ITeamRepository.ILoginHandler() {
            public ILoginInfo challenge(ITeamRepository repository) {
                return new ILoginInfo() {
                    public String getUserId() {
                        return username;
                    }
                    public String getPassword() {
                        return password;                        
                    }
                };
            }
        });
        monitor.subTask("Contacting " + repository.getRepositoryURI() + "...");
        repository.login(monitor);
        monitor.subTask("Connected ...");
        return repository;
    }
}
