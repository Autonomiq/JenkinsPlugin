package io.jenkins.plugins.autonomiq.service.types;

import java.util.ArrayList;

public class Environment{
    public String environmentType;
    public Environment2 environment;
    
    public Environment(String environmentType,Environment2 environment)
    {
    	this.environmentType=environmentType;
    	this.environment=environment;
    }
    
    public String getenvironmentType()
    {
    	return environmentType;
    }
    
    public Environment2 getenvironment()
    {
    	return environment;
    }
}


