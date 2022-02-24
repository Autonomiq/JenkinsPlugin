package io.jenkins.plugins.autonomiq.service.types;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ExecutionEnvironment{
    public Integer accountId;
    public ArrayList<Environment> environments;
    
    public ExecutionEnvironment(Integer accountId,ArrayList<Environment> environments)
    {
    	this.accountId=accountId;
    	this.environments=environments;
    }
    @SuppressWarnings("unused")
    public Integer getaccountId()
    {
    	return accountId;
    }
    @SuppressWarnings("unused")
    public ArrayList<Environment> getenvironments()
    {
    	return environments;
    }
}




