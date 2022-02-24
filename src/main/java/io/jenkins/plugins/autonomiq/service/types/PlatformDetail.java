package io.jenkins.plugins.autonomiq.service.types;

public class PlatformDetail{
    public String appiumVersion;
    public String browser;
    public String browserVersion;
    public String deviceName;
    public String deviceOrientation;
    public String platform;
    public String platformVersion;
    public boolean isDefault;
    
    public PlatformDetail(String appiumVersion,String browser,String browserVersion,String deviceName,String deviceOrientation,String platform,
    		String platformVersion,boolean isDefault) {
    	this.appiumVersion=appiumVersion;
    	this.browser=browser;
    	this.browserVersion=browserVersion;
    	this.deviceName=deviceName;
    	this.deviceOrientation=deviceOrientation;
    	this.platform=platform;
    	this.platformVersion=platformVersion;
    	this.isDefault=isDefault;
    }
    
    
    public String getappiumVersion()
    {
    	return appiumVersion;
    }
    
    public String getbrowser()
    {
    	return browser;
    }
    
    public String getbrowserVersion()
    {
    	return browserVersion;
    }
    
    public String getdeviceName()
    {
    	return deviceName;
    }
    
    public String getdeviceOrientation()
    {
    	return deviceOrientation;
    }
    
    public String getplatform()
    {
    	return platform;
    }
    
    public String getplatformVersion()
    {
    	return platformVersion;
    }
    
    public boolean getisDefault()
    {
    	return isDefault;
    }
}

