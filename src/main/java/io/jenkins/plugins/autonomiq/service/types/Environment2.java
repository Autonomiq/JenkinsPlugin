package io.jenkins.plugins.autonomiq.service.types;

import java.util.ArrayList;

public class Environment2{
    public ArrayList<PlatformDetail> platformDetails;
    public String sauceDataCentreName;
    public String saucePassword;
    public String sauceUsername;
   
    public Environment2(ArrayList<PlatformDetail> platformDetails,String sauceDataCentreName,String saucePassword,String sauceUsername)
    {
    	this.platformDetails=platformDetails;
    	this.saucePassword=saucePassword;
    	this.sauceUsername=sauceUsername;
    	this.sauceDataCentreName=sauceDataCentreName;
    }
    
    public ArrayList<PlatformDetail> getplatformDetails()
    {
    	return platformDetails;
    }
    
    public String getsauceDataCentreName()
    {
    	return sauceDataCentreName;
    }
    
    public String getsaucePassword()
    {
    	return saucePassword;
    }
    public String getsauceUsername()
    {
    	return sauceUsername;
    }
}