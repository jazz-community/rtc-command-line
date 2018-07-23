package com.cli.ccm;

public class CLMConfig {
	
	private String repoURL = new String();
	private String username = new String();
	private String password = new String();
	
	public CLMConfig(String repo, String username, String password)
	{
		this.repoURL=repo;
		this.username=username;
		this.password=password;
	}
	
	public CLMConfig()
	{
		
	}
	
	public String getRepoURL() {
		return repoURL;
	}
	public void setRepoURL(String repoURL) {
		this.repoURL = repoURL;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	
	public String toString()
	{
		return "repo="+repoURL+" username="+username+" password="+password;
	}
	

}
