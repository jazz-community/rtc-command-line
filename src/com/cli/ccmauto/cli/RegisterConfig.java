package com.cli.ccmauto.cli;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;


@Parameters(commandDescription = "Creating new config.properties file with username, password and repoURL that will be used to execute accm commands. Example: register-config -r https://localhost:9443/ccm -u user -p pass")
public class RegisterConfig {
	@Parameter(names = { "-u", "--username" }, description = "The user ID of the user executing the commands", order=1, required = true)
	public String username;
	
	@Parameter(names = { "-p", "--password" }, description = "The password of the user", order=2, required = true)
	public String password;
	
	@Parameter(names = { "-r", "--repositoryURL" }, description = "The repository URI - ex. https://localhost:9443/ccm", order=0, required = true)
	public String repository;
}
