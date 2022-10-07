package io.jenkins.plugins.autonomiq;

import hudson.Launcher;
import hudson.XmlFile;
import hudson.Extension;
import hudson.FilePath;
import hudson.model.*;
import hudson.util.FormValidation;
import hudson.tasks.Builder;
import hudson.tasks.BuildStepDescriptor;

import hudson.util.ListBoxModel;
import hudson.util.ListBoxModel.Option;
import io.jenkins.plugins.autonomiq.service.ServiceAccess;
import io.jenkins.plugins.autonomiq.service.ServiceException;
import io.jenkins.plugins.autonomiq.service.types.AutInformation;
import io.jenkins.plugins.autonomiq.service.types.DiscoveryResponse;
import io.jenkins.plugins.autonomiq.service.types.Environment2;
import io.jenkins.plugins.autonomiq.service.types.ExecutionEnvironment;
import io.jenkins.plugins.autonomiq.service.types.GetSauceConnect;
import io.jenkins.plugins.autonomiq.service.types.GetTestSuitesResponse;
import io.jenkins.plugins.autonomiq.service.types.PlatformDetail;
import io.jenkins.plugins.autonomiq.service.types.TestCasesResponse;
import io.jenkins.plugins.autonomiq.util.AiqUtil;
import io.jenkins.plugins.autonomiq.util.TimeStampedLogger;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.QueryParameter;
import org.kohsuke.stapler.export.Exported;
import org.kohsuke.stapler.verb.POST;

import io.jenkins.plugins.autonomiq.service.types.Environment;




import javax.servlet.ServletException;
import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import hudson.util.Secret;
import jenkins.model.Jenkins;
import jenkins.tasks.SimpleBuildStep;

import org.apache.commons.lang.StringUtils;
import org.jenkinsci.Symbol;
import org.kohsuke.stapler.DataBoundSetter;

public class AutonomiqBuilder extends Builder implements SimpleBuildStep {
    private String aiqUrl;
    private String login;
    private Secret password;
    private String project; // json of ProjectData class
    private Boolean genScripts;
    private Boolean runTestCases;
    private Boolean runTestSuites;
    private Boolean crossBrowser;
    private Boolean mobileDevice;
    private Boolean crossBrowserTestcases;
    private Boolean mobileDeviceTestcases;
    private Boolean crossBrowsergenScripts;
    private Boolean mobileDevicegenScripts;
    private String platformTestCases;
    private String browserTestCases;
    private String platformTestSuites;
    private String browserTestSuites;
    private String genCaseList;
    private String runCaseList;
    private String runSuiteList;
    private String mobileRunSuiteList;
    private String mobileRunTestcaseList;
    private String proxyHost;
    private String proxyPort;
    private String proxyUser;
    private Secret proxyPassword;
    private Boolean httpProxy;
    private String executionMode;
    private String mobileExecutionMode;
    private String environmentType;
    private String environmentTypeTestcases;
    private String platformVersion;
    private String browserVersion;
    private String sauceConnectProxy;
    private String browserVersionTestcases;
    private String sauceConnectProxyTestcases;
    private String mobileSauceConnectProxy;
    private String mobileSauceConnectProxyTc;
    private String mobileplatformTestSuites;
    private String mobileplatformTestcases;
    private String mobilePlatformVersion;
    private String mobilePlatformVersionTc;
    private String deviceName;
    private String deviceNameTestcases;
    private String deviceOrientation;
    private String deviceOrientationTc;
    private String enableAnimations;
    private String enableAnimationsTc;
    private String autoGrantPermission;
    private String autoGrantPermissionTc;
    private static Long pollingIntervalMs = 10000L;

    @DataBoundConstructor
    public AutonomiqBuilder(String aiqUrl, String login, Secret password, String project,
    		               
                            Boolean genScripts,
                            Boolean runTestCases,
                            Boolean runTestSuites,
                            Boolean crossBrowser,
                            Boolean mobileDevice,
                            Boolean crossBrowserTestcases,
                            Boolean mobileDeviceTestcases,
                            Boolean crossBrowsergenScripts,
                            Boolean mobileDevicegenScripts,
                            String platformTestCases, String browserTestCases,
                            String platformTestSuites, String browserTestSuites,
                            String genCaseList,
                            String runCaseList,
                            String runSuiteList,
                            String mobileRunSuiteList,
                            String mobileRunTestcaseList,
                            String proxyHost,
                            String proxyPort,
                            String proxyUser,
                            Secret proxyPassword,
                            Boolean httpProxy,
                            String executionMode,
                            String mobileExecutionMode,
                            String environmentType,
                            String platformVersion,
                            String browserVersion,
                            String sauceConnectProxy,
                            String environmentTypeTestcases,
                            String browserVersionTestcases,
    						String sauceConnectProxyTestcases,
    						String mobileSauceConnectProxy,
    						String mobileSauceConnectProxyTc,
    					    String mobileplatformTestSuites,
    					    String mobileplatformTestcases,
    					    String mobilePlatformVersion,
    					    String mobilePlatformVersionTc,
    					    String deviceName,
    					    String deviceNameTestcases,
    					    String deviceOrientation,
    					    String deviceOrientationTc,
    					    String enableAnimations,
    					    String enableAnimationsTc,
    					    String autoGrantPermission,
    					    String autoGrantPermissionTc
    ) {

        this.aiqUrl = aiqUrl;
        this.login = login;
        this.password = password;
        this.project = project;
       
        this.genScripts = genScripts;
        this.runTestCases = runTestCases;
        this.runTestSuites = runTestSuites;
        this.crossBrowser = crossBrowser;
        this.mobileDevice = mobileDevice;
        this.crossBrowserTestcases = crossBrowserTestcases;
        this.mobileDeviceTestcases = mobileDeviceTestcases;
        this.crossBrowsergenScripts=crossBrowsergenScripts;
        this.mobileDevicegenScripts=mobileDevicegenScripts;
        this.platformTestCases = platformTestCases;
        this.browserTestCases = browserTestCases;
        this.platformTestSuites = platformTestSuites;
        this.browserTestSuites = browserTestSuites;
        this.genCaseList = genCaseList;
        this.runCaseList = runCaseList;
        this.runSuiteList = runSuiteList;
        this.mobileRunSuiteList=mobileRunSuiteList;
        this.mobileRunTestcaseList=mobileRunTestcaseList;
        this.proxyHost = proxyHost;
        this.proxyPort = proxyPort;
        this.proxyUser = proxyUser;
        this.proxyPassword = proxyPassword;
        this.httpProxy = httpProxy;
        this.executionMode=executionMode;
        this.mobileExecutionMode=mobileExecutionMode;
        this.platformVersion=platformVersion;
        this.browserVersion=browserVersion;
        this.environmentType=environmentType;
        this.sauceConnectProxy=sauceConnectProxy;
        this.environmentTypeTestcases=environmentTypeTestcases;
        this.sauceConnectProxyTestcases=sauceConnectProxyTestcases;
        this.mobileSauceConnectProxy=mobileSauceConnectProxy;
        this.mobileSauceConnectProxyTc=mobileSauceConnectProxyTc;
        this.browserVersionTestcases=browserVersionTestcases;
        this.mobileplatformTestSuites=mobileplatformTestSuites;
        this.mobileplatformTestcases=mobileplatformTestcases;
        this.mobilePlatformVersion=mobilePlatformVersion;
        this.mobilePlatformVersionTc=mobilePlatformVersionTc;
        this.deviceName=deviceName;
        this.deviceNameTestcases=deviceNameTestcases;
	    this.deviceOrientation=deviceOrientation;
	    this.deviceOrientationTc=deviceOrientationTc;
	    this.enableAnimations=enableAnimations;
	    this.enableAnimationsTc=enableAnimationsTc;
	    this.autoGrantPermission=autoGrantPermission;
	    this.autoGrantPermissionTc=autoGrantPermissionTc;
	    
    }
    
    @SuppressWarnings("unused")
    @DataBoundSetter
    public void setAiqUrl(String aiqUrl) {
    	
        this.aiqUrl = aiqUrl;
    }
    
    @SuppressWarnings("unused")
    public String getAiqUrl() {
        return aiqUrl;
    }

    @SuppressWarnings("unused")
    @DataBoundSetter
    public void setLogin(String login) {
        this.login = login;
    }

    @SuppressWarnings("unused")
    public String getLogin() {
        return login;
    }

    @SuppressWarnings("unused")
    @DataBoundSetter
    public void setPassword(Secret password) {
        this.password = password;
    }

    @SuppressWarnings("unused")
    public Secret getPassword() {
        return password;
    }

    @SuppressWarnings("unused")
    @DataBoundSetter
    public void setProject(String project) {
        this.project = project;
    }

    @SuppressWarnings("unused")
    public String getProject() {
        return project;
    }

    @DataBoundSetter
    @SuppressWarnings("unused")
    public void setGenScripts(Boolean genScripts) {
        this.genScripts = genScripts;
    }

    @SuppressWarnings("unused")
    public Boolean getGenScripts() {
        return genScripts;
    }

    @SuppressWarnings("unused")
    @DataBoundSetter
    public void setRunTestCases(Boolean runTestCases) {
        this.runTestCases = runTestCases;
    }

    @SuppressWarnings("unused")
    public Boolean getRunTestCases() {
    	//System.out.println("testcases value inside methods databounders"+ runTestCases);
        return runTestCases;
    }

    @SuppressWarnings("unused")
    @DataBoundSetter
    public void setRunTestSuites(Boolean runTestSuites) {
        this.runTestSuites = runTestSuites;
    }

    @SuppressWarnings("unused")
    public Boolean getRunTestSuites() {
    	//System.out.println("testsuites value inside methods databounders"+ runTestSuites);
        return runTestSuites;
    }
    
 // mobile Data Bounder start
    
    @SuppressWarnings("unused")
    @DataBoundSetter
    public void setMobileDevice(Boolean mobileDevice) {
        this.mobileDevice = mobileDevice;
    }

    @SuppressWarnings("unused")
    public Boolean getMobileDevice() {
        return mobileDevice;
    }
    
    @SuppressWarnings("unused")
    @DataBoundSetter
    public void setCrossBrowser(Boolean crossBrowser) {
        this.crossBrowser = crossBrowser;
    }

    @SuppressWarnings("unused")
    public Boolean getCrossBrowser() {
    	System.out.println("value inside methods databounders"+ crossBrowser);
        return true;
    }
    
    @SuppressWarnings("unused")
    @DataBoundSetter
    public void setMobileDeviceTestcases(Boolean mobileDeviceTestcases) {
        this.mobileDeviceTestcases = mobileDeviceTestcases;
    }

    @SuppressWarnings("unused")
    public Boolean getMobileDeviceTestcases() {
        return mobileDeviceTestcases;
    }
    @SuppressWarnings("unused")
    @DataBoundSetter
    public void setCrossBrowserTestcases(Boolean crossBrowserTestcases) {
        this.crossBrowserTestcases = crossBrowserTestcases;
    }

    @SuppressWarnings("unused")
    public Boolean getCrossBrowserTestcases() {
        return crossBrowserTestcases;
    }
    
    @SuppressWarnings("unused")
    @DataBoundSetter
    public void setMobileDevicegenScripts(Boolean mobileDevicegenScripts) {
        this.mobileDevicegenScripts = mobileDevicegenScripts;
    }

    @SuppressWarnings("unused")
    public Boolean getMobileDevicegenScripts() {
        return mobileDevicegenScripts;
    }
    
    @SuppressWarnings("unused")
    @DataBoundSetter
    public void setCrossBrowsergenScripts(Boolean crossBrowsergenScripts) {
        this.crossBrowsergenScripts = crossBrowsergenScripts;
    }

    @SuppressWarnings("unused")
    public Boolean getCrossBrowsergenScripts() {
        return crossBrowsergenScripts;
    }
    
    @SuppressWarnings("unused")
    @DataBoundSetter
    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    @SuppressWarnings("unused")
    public String getDeviceName() {
        return deviceName;
    }
    
    @SuppressWarnings("unused")
    @DataBoundSetter
    public void setDeviceNameTestcases(String deviceNameTestcases) {
        this.deviceNameTestcases = deviceNameTestcases;
    }

    @SuppressWarnings("unused")
    public String getDeviceNameTestcases() {
        return deviceNameTestcases;
    }
    
    @SuppressWarnings("unused")
    @DataBoundSetter
    public void setDeviceOrientation(String deviceOrientation) {
        this.deviceOrientation = deviceOrientation;
    }

    @SuppressWarnings("unused")
    public String getDeviceOrientation() {
        return deviceOrientation;
    }
    
    @SuppressWarnings("unused")
    @DataBoundSetter
    public void setDeviceOrientationTc(String deviceOrientationTc) {
        this.deviceOrientationTc = deviceOrientationTc;
    }

    @SuppressWarnings("unused")
    public String getDeviceOrientationTc() {
        return deviceOrientationTc;
    }
    
    @SuppressWarnings("unused")
    @DataBoundSetter
    public void setEnableAnimations(String enableAnimations) {
        this.enableAnimations = enableAnimations;
    }

    @SuppressWarnings("unused")
    public String getEnableAnimations() {
        return enableAnimations;
    }
    
    @SuppressWarnings("unused")
    @DataBoundSetter
    public void setEnableAnimationsTc(String enableAnimationsTc) {
        this.enableAnimationsTc = enableAnimationsTc;
    }

    @SuppressWarnings("unused")
    public String getEnableAnimationsTc() {
        return enableAnimationsTc;
    }
    
    @SuppressWarnings("unused")
    @DataBoundSetter
    public void setAutoGrantPermission(String autoGrantPermission) {
        this.autoGrantPermission = autoGrantPermission;
    }

    @SuppressWarnings("unused")
    public String getAutoGrantPermission() {
        return autoGrantPermission;
    }
    
    @SuppressWarnings("unused")
    @DataBoundSetter
    public void setAutoGrantPermissionTc(String autoGrantPermissionTc) {
        this.autoGrantPermissionTc = autoGrantPermissionTc;
    }

    @SuppressWarnings("unused")
    public String getAutoGrantPermissionTc() {
        return autoGrantPermissionTc;
    }
    @SuppressWarnings("unused")
    @DataBoundSetter
    public void setMobileplatformTestSuites(String mobileplatformTestSuites) {
        this.mobileplatformTestSuites = mobileplatformTestSuites;
    }

    @SuppressWarnings("unused")
    public String getMobileplatformTestSuites() {
        return mobileplatformTestSuites;
    }
    
    @SuppressWarnings("unused")
    @DataBoundSetter
    public void setMobileplatformTestcasess(String mobileplatformTestcases) {
        this.mobileplatformTestcases = mobileplatformTestcases;
    }

    @SuppressWarnings("unused")
    public String getMobileplatformTestcases() {
        return mobileplatformTestcases;
    }
    @SuppressWarnings("unused")
    @DataBoundSetter
    public void setMobilePlatformVersion(String mobilePlatformVersion) {
        this.mobilePlatformVersion = mobilePlatformVersion;
    }

    @SuppressWarnings("unused")
    public String getMobilePlatformVersion() {
        return mobilePlatformVersion;
    }
    @SuppressWarnings("unused")
    @DataBoundSetter
    public void setMobilePlatformVersionTc(String mobilePlatformVersionTc) {
        this.mobilePlatformVersionTc = mobilePlatformVersionTc;
    }

    @SuppressWarnings("unused")
    public String getMobilePlatformVersionTc() {
        return mobilePlatformVersionTc;
    }
    

    @SuppressWarnings("unused")
    @DataBoundSetter
    public void setMobileRunSuiteList(String mobileRunSuiteList) {
        this.mobileRunSuiteList = mobileRunSuiteList;
    }

    @SuppressWarnings("unused")
    @DataBoundSetter
    public String getMobileRunSuiteList() {
        return mobileRunSuiteList;
    }
    
    @SuppressWarnings("unused")
    @DataBoundSetter
    public void setMobileRunTestcaseList(String mobileRunTestcaseList) {
        this.mobileRunTestcaseList = mobileRunTestcaseList;
    }

    @SuppressWarnings("unused")
    @DataBoundSetter
    public String getMobileRunTestcaseList() {
        return mobileRunTestcaseList;
    }
    
// mobile Data Bounder ends
    
    @SuppressWarnings("unused")
    @DataBoundSetter
    public void setPlatformTestCases(String platform) {
        this.platformTestCases = platform;
    }

    @SuppressWarnings("unused")
    public String getPlatformTestCases() {
        return platformTestCases;
    }

    @SuppressWarnings("unused")
    @DataBoundSetter
    public void setBrowserTestCases(String browser) {
        this.browserTestCases = browser;
    }

    @SuppressWarnings("unused")
    public String getBrowserTestCases() {
        return browserTestCases;
    }


    @SuppressWarnings("unused")
    @DataBoundSetter
    public void setPlatformTestSuites(String platform) {
        this.platformTestSuites = platform;
    }

    @SuppressWarnings("unused")
    public String getPlatformTestSuites() {
        return platformTestSuites;
    }

    @SuppressWarnings("unused")
    @DataBoundSetter
    public void setBrowserTestSuites(String browser) {
        this.browserTestSuites = browser;
    }

    @SuppressWarnings("unused")
    public String getBrowserTestSuites() {
        return browserTestSuites;
    }




    @SuppressWarnings("unused")
    public String getRunCaseList() {
    	
        return runCaseList;
    }

    @SuppressWarnings("unused")
    @DataBoundSetter
    public void setRunCaseList(String runCaseList) {
        this.runCaseList = runCaseList;
    }

    @SuppressWarnings("unused")
    public void setGenCaseList(String genCaseList) {
        this.genCaseList = genCaseList;
    }

    @SuppressWarnings("unused")
    @DataBoundSetter
    public void setRunSuiteList(String runSuiteList) {
        this.runSuiteList = runSuiteList;
    }

    @SuppressWarnings("unused")
    public String getGenCaseList() {

        return genCaseList;
    }

    @SuppressWarnings("unused")
    @DataBoundSetter
    public String getRunSuiteList() {
    	        return runSuiteList;
    }
    
    @SuppressWarnings("unused")
    @DataBoundSetter
    public void setProxyHost(String proxyHost) {
        this.proxyHost = proxyHost;
    }

    @SuppressWarnings("unused")
    public String getProxyHost() {
        return proxyHost;
    }
    
    @SuppressWarnings("unused")
    @DataBoundSetter
    public void setProxyPort(String proxyPort) {
        this.proxyPort = proxyPort;
    }

    @SuppressWarnings("unused")
    public String getProxyPort() {
        return proxyPort;
    }

    @SuppressWarnings("unused")
    @DataBoundSetter
    public void setProxyUser(String proxyUser) {
        this.proxyUser = proxyUser;
    }

    @SuppressWarnings("unused")
    public String getProxyUser() {
        return proxyUser;
    }
    
    @SuppressWarnings("unused")
    @DataBoundSetter
    public void setProxyPassword(Secret proxyPassword) {
        this.proxyPassword = proxyPassword;
    }

    @SuppressWarnings("unused")
    public Secret getProxyPassword() {
        return proxyPassword;
    }
    
    @DataBoundSetter
    @SuppressWarnings("unused")
    public void setHttpProxy(Boolean httpProxy) {
        this.httpProxy = httpProxy;
    }

    @SuppressWarnings("unused")
    public Boolean getHttpProxy() {
        return httpProxy;
    }
    

    @SuppressWarnings("unused")
    @DataBoundSetter
    public void setExecutionMode(String executionMode) {
        this.executionMode = executionMode;
    }

    @SuppressWarnings("unused")
    public String getExecutionMode() {
        return executionMode;
    }

//String platformVersion,
    //String browserVersion

    @SuppressWarnings("unused")
    @DataBoundSetter
    public void setEnvironmentType(String environmentType) {
        this.environmentType = environmentType;
    }

    @SuppressWarnings("unused")
    public String getEnvironmentType() {
    	//System.out.println("values of env inside data bounders:-"+ environmentType);
        return environmentType;
    }

    @SuppressWarnings("unused")
    @DataBoundSetter
    public void setEnvironmentTypeTestcases(String environmentTypeTestcases) {
        this.environmentTypeTestcases = environmentTypeTestcases;
    }

    @SuppressWarnings("unused")
    public String getEnvironmentTypeTestcases() {
        return environmentTypeTestcases;
    }
    

    @SuppressWarnings("unused")
    @DataBoundSetter
    public void setPlatformVersion(String platformVersion) {
        this.platformVersion = platformVersion;
    }

    @SuppressWarnings("unused")
    public String getPlatformVersion() {
        return platformVersion;
    }
    
   // sauceConnectProxy
    @SuppressWarnings("unused")
    @DataBoundSetter
    public void setBrowserVersion(String browserVersion) {
        this.browserVersion = browserVersion;
    }

    @SuppressWarnings("unused")
    public String getBrowserVersion() {
        return browserVersion;
    }
    @SuppressWarnings("unused")
    @DataBoundSetter
    public void setBrowserVersionTestcases(String browserVersionTestcases) {
        this.browserVersionTestcases = browserVersionTestcases;
    }

    @SuppressWarnings("unused")
    public String getBrowserVersionTestcases() {
        return browserVersionTestcases;
    }

    @SuppressWarnings("unused")
    @DataBoundSetter
    public void setSauceConnectProxy(String sauceConnectProxy) {
        this.sauceConnectProxy = sauceConnectProxy;
    }

    @SuppressWarnings("unused")
    public String getSauceConnectProxy() {
        return sauceConnectProxy;
    }
    
    @SuppressWarnings("unused")
    @DataBoundSetter
    public void setMobileSauceConnectProxy(String mobileSauceConnectProxy) {
        this.mobileSauceConnectProxy = mobileSauceConnectProxy;
    }

    @SuppressWarnings("unused")
    public String getMobileSauceConnectProxy() {
        return mobileSauceConnectProxy;
    }
    
    @SuppressWarnings("unused")
    @DataBoundSetter
    public void setMobileSauceConnectProxyTc(String mobileSauceConnectProxyTc) {
        this.mobileSauceConnectProxyTc = mobileSauceConnectProxyTc;
    }

    @SuppressWarnings("unused")
    public String getMobileSauceConnectProxyTc() {
        return mobileSauceConnectProxyTc;
    }
    @SuppressWarnings("unused")
    @DataBoundSetter
    public void setSauceConnectProxyTestcases(String sauceConnectProxyTestcases) {
        this.sauceConnectProxyTestcases = sauceConnectProxyTestcases;
    }

    @SuppressWarnings("unused")
    public String getSauceConnectProxyTestcases() {
        return sauceConnectProxyTestcases;
    }


    private static ServiceAccess getServiceAccess(String proxyHost, String proxyPort, String proxyUser, Secret proxyPassword,
    		String aiqUrl, String login, Secret password, Boolean httpProxy) throws ServiceException {
    	ServiceAccess svc = null;
    	if (httpProxy && !StringUtils.isEmpty(proxyHost) && !StringUtils.isEmpty(proxyPort) ) {
    		svc = new ServiceAccess(proxyHost, proxyPort, proxyUser, proxyPassword, aiqUrl, login, password);
    	} else {
    		 svc = new ServiceAccess(aiqUrl, login, password);
    	}
    	return svc;
    }

    @Override
    public void perform(Run<?, ?> run, FilePath workspace, Launcher launcher,
                        TaskListener listener) throws InterruptedException, IOException {
        
        TimeStampedLogger log = new TimeStampedLogger(listener.getLogger());

        boolean ok = true;
        AiqUtil.gson.fromJson(project, ProjectData.class);

        log.println();
        log.printf("Logging in as user '%s' to Autonomiq service at: %s\n", login, aiqUrl);
        log.printf("browserversion '%s' to platformversion at: %s\n", browserVersion,platformVersion);
        log.println();
        log.printf("browserversion '%s' to platformversion at: %s\n", browserVersion,platformVersion);
        ProjectData pd;

        try {
            pd = AiqUtil.gson.fromJson(project, ProjectData.class);

        } catch (Exception e) {
            throw new IOException("Exception unpacking project data", e);
        }

        ServiceAccess svc = null;
        try {
        	svc = getServiceAccess(proxyHost, proxyPort, proxyUser, proxyPassword, aiqUrl, login, password, httpProxy);
        } catch (Exception e) {
            ok = false;
            log.println("Authentication with Autonomiq service failed");
            log.println(AiqUtil.getExceptionTrace(e));
        }

        if (ok) {
            try {
            	log.printf("list of svc '%s'\n",svc);
            	log.printf("list of pd '%s'\n",pd);
            	log.printf("list of logs '%s'\n",log);                 
                RunTests rt = new RunTests(svc, log, pd, pollingIntervalMs);
                ok = rt.runTests(genScripts, runTestCases, runTestSuites,crossBrowser,mobileDevice,crossBrowserTestcases,mobileDeviceTestcases,crossBrowsergenScripts,mobileDevicegenScripts,
                        platformTestCases, browserTestCases,
                        platformTestSuites, browserTestSuites,
                        genCaseList, runCaseList, runSuiteList,executionMode,environmentType,browserVersion,platformVersion,sauceConnectProxy,environmentTypeTestcases,browserVersionTestcases,sauceConnectProxyTestcases,
                        mobileplatformTestSuites,mobilePlatformVersion,deviceName,mobileSauceConnectProxy,mobileExecutionMode,deviceOrientation,enableAnimations,autoGrantPermission,mobileRunSuiteList,
                        mobileplatformTestcases,mobilePlatformVersionTc,deviceNameTestcases,mobileSauceConnectProxyTc,deviceOrientationTc,enableAnimationsTc,autoGrantPermissionTc,mobileRunTestcaseList);
            } catch (PluginException e) {
                log.println("Running test case failed with exception");
                log.println(AiqUtil.getExceptionTrace(e));
            }
        }

        if (ok) {
            run.setResult(Result.SUCCESS);
        } else {
            run.setResult(Result.FAILURE);
        }
    }

    @SuppressWarnings("unused")
    @Symbol("greet")
    @Extension
    public static final class DescriptorImpl extends BuildStepDescriptor<Builder> {	
    
    	
    	
    	@SuppressWarnings("unused")
        @POST
        public FormValidation doCheckGenScripts(@QueryParameter String value, @QueryParameter String genScripts)
                throws IOException, ServletException {
        	Jenkins.get().checkPermission(Jenkins.ADMINISTER);
        	System.out.println("value of mystring param0:"+value.length());
       
            return FormValidation.ok();
        }
    	
    	@SuppressWarnings("unused")
        @POST
        public FormValidation doCheckAiqUrl(@QueryParameter String value, @QueryParameter String aiqUrl)
                throws IOException, ServletException {
        	Jenkins.get().checkPermission(Jenkins.ADMINISTER);
        	
            if (value.length() == 0)
                return FormValidation.error(Messages.AutonomiqBuilder_DescriptorImpl_errors_missingAiqUrl());
            if (!(value.startsWith("http://") || value.startsWith("https://")))
                return FormValidation.warning(Messages.AutonomiqBuilder_DescriptorImpl_errors_notUrl());
            
            return FormValidation.ok();
        }
        
        @SuppressWarnings("unused")
        @POST
        public FormValidation doCheckLogin(@QueryParameter String value, @QueryParameter String login)
                throws IOException, ServletException {
        	Jenkins.get().checkPermission(Jenkins.ADMINISTER);
            if (value.length() == 0)
                return FormValidation.error(Messages.AutonomiqBuilder_DescriptorImpl_errors_missingLogin());
            if (value.length() < 4)
                return FormValidation.warning(Messages.AutonomiqBuilder_DescriptorImpl_warnings_tooShort());

            return FormValidation.ok();
        }

        @SuppressWarnings("unused")
        @POST
        public FormValidation doCheckPassword(@QueryParameter String value, @QueryParameter Secret password)
                throws IOException, ServletException {
        	Jenkins.get().checkPermission(Jenkins.ADMINISTER);
            if (value.length() == 0)
                return FormValidation.error(Messages.AutonomiqBuilder_DescriptorImpl_errors_missingPassword());
            if (value.length() < 6)
                return FormValidation.warning(Messages.AutonomiqBuilder_DescriptorImpl_warnings_tooShort());

            return FormValidation.ok();
        }

        @SuppressWarnings("unused")
        @POST
        public FormValidation doCheckProject(@QueryParameter String value)
                throws IOException, ServletException {
        	Jenkins.get().checkPermission(Jenkins.ADMINISTER);
            if (value.length() == 0)
                return FormValidation.error(Messages.AutonomiqBuilder_DescriptorImpl_errors_missingProject());
            
            return FormValidation.ok();
        }
        @POST
        public FormValidation doCheckGenCaseList(@QueryParameter String value,
                                                 @QueryParameter String aiqUrl,
                                                 @QueryParameter String login,
                                                 @QueryParameter Secret password,
                                                 @QueryParameter String project,
                                                 @QueryParameter String proxyHost,
                                                 @QueryParameter String proxyPort,
                                                 @QueryParameter String proxyUser,
                                                 @QueryParameter Secret proxyPassword,
                                                 @QueryParameter Boolean httpProxy)
                throws IOException, ServletException {
        	Jenkins.get().checkPermission(Jenkins.ADMINISTER);
            return checkTestCasesFromText(value, aiqUrl, login, password, project, proxyHost, proxyPort, proxyUser, proxyPassword, httpProxy);
        }

        @POST
        public FormValidation doCheckRunCaseList(@QueryParameter String value,
                                                 @QueryParameter String aiqUrl,
                                                 @QueryParameter String login,
                                                 @QueryParameter Secret password,
                                                 @QueryParameter String project,
                                                 @QueryParameter String proxyHost,
                                                 @QueryParameter String proxyPort,
                                                 @QueryParameter String proxyUser,
                                                 @QueryParameter Secret proxyPassword,
                                                 @QueryParameter Boolean httpProxy)
                throws IOException, ServletException {
        	Jenkins.get().checkPermission(Jenkins.ADMINISTER);
            return checkTestCasesFromText(value, aiqUrl, login, password, project, proxyHost, proxyPort, proxyUser, proxyPassword, httpProxy);
        }

        @POST
        public FormValidation doCheckRunSuiteList(@QueryParameter String value,
                                                  @QueryParameter String aiqUrl,
                                                  @QueryParameter String login,
                                                  @QueryParameter Secret password,
                                                  @QueryParameter String project,
                                                  @QueryParameter String proxyHost,
                                                  @QueryParameter String proxyPort,
                                                  @QueryParameter String proxyUser,
                                                  @QueryParameter Secret proxyPassword,
                                                  @QueryParameter Boolean httpProxy)
                throws IOException, ServletException {
        	Jenkins.get().checkPermission(Jenkins.ADMINISTER);
            if (value.length() > 0
                    && aiqUrl.length() > 0
                    && login.length() > 0
                    && Secret.toString(password).length() > 0
                    && project.length() > 0) {

                // get the project or skip
                ProjectData pd;
                try {
                    pd = AiqUtil.gson.fromJson(project, ProjectData.class);
                } catch (Exception e) {
                    return FormValidation.ok();
                }

                AiqUtil.ItemListFromString itemsObj = AiqUtil.getItemListFromString(value);

                if (itemsObj.getError() != null) {
                    // show error from processing text
                    return FormValidation.error(itemsObj.getError());
                } else if (itemsObj.getItemList().size() > 0) {
                    // try checking the items

                    try {
                        ServiceAccess svc = AutonomiqBuilder.getServiceAccess(proxyHost, proxyPort, proxyUser, proxyPassword, aiqUrl, login, password, httpProxy);
                        Set<String> set = getTestSuiteSet(svc, pd.getProjectId());
                        if (set != null) {
                            for (String item : itemsObj.getItemList()) {
                                if (!set.contains(item)) {
                                    return FormValidation.error(String.format("Test suite not found: '%s'", item));
                                }
                            }
                        }
                    } catch (Exception e) {
                        return FormValidation.ok();
                    }

                }

            }

            return FormValidation.ok();

        }
        
        //mobile
        @POST
        public FormValidation doCheckMobileRunSuiteList(@QueryParameter String value,
                                                  @QueryParameter String aiqUrl,
                                                  @QueryParameter String login,
                                                  @QueryParameter Secret password,
                                                  @QueryParameter String project,
                                                  @QueryParameter String proxyHost,
                                                  @QueryParameter String proxyPort,
                                                  @QueryParameter String proxyUser,
                                                  @QueryParameter Secret proxyPassword,
                                                  @QueryParameter Boolean httpProxy)
                throws IOException, ServletException {
        	Jenkins.get().checkPermission(Jenkins.ADMINISTER);
            if (value.length() > 0
                    && aiqUrl.length() > 0
                    && login.length() > 0
                    && Secret.toString(password).length() > 0
                    && project.length() > 0) {

                // get the project or skip
                ProjectData pd;
                try {
                    pd = AiqUtil.gson.fromJson(project, ProjectData.class);
                } catch (Exception e) {
                    return FormValidation.ok();
                }

                AiqUtil.ItemListFromString itemsObj = AiqUtil.getItemListFromString(value);

                if (itemsObj.getError() != null) {
                    // show error from processing text
                    return FormValidation.error(itemsObj.getError());
                } else if (itemsObj.getItemList().size() > 0) {
                    // try checking the items

                    try {
                        ServiceAccess svc = AutonomiqBuilder.getServiceAccess(proxyHost, proxyPort, proxyUser, proxyPassword, aiqUrl, login, password, httpProxy);
                        Set<String> set = getTestSuiteSet(svc, pd.getProjectId());
                        if (set != null) {
                            for (String item : itemsObj.getItemList()) {
                                if (!set.contains(item)) {
                                    return FormValidation.error(String.format("Test suite not found: '%s'", item));
                                }
                            }
                        }
                    } catch (Exception e) {
                        return FormValidation.ok();
                    }

                }

            }

            return FormValidation.ok();
        }
        
        @POST
        public FormValidation doCheckmobileRunTestcaseList(@QueryParameter String value,
                                                  @QueryParameter String aiqUrl,
                                                  @QueryParameter String login,
                                                  @QueryParameter Secret password,
                                                  @QueryParameter String project,
                                                  @QueryParameter String proxyHost,
                                                  @QueryParameter String proxyPort,
                                                  @QueryParameter String proxyUser,
                                                  @QueryParameter Secret proxyPassword,
                                                  @QueryParameter Boolean httpProxy)
                throws IOException, ServletException {
        	Jenkins.get().checkPermission(Jenkins.ADMINISTER);
            if (value.length() > 0
                    && aiqUrl.length() > 0
                    && login.length() > 0
                    && Secret.toString(password).length() > 0
                    && project.length() > 0) {

                // get the project or skip
                ProjectData pd;
                try {
                    pd = AiqUtil.gson.fromJson(project, ProjectData.class);
                } catch (Exception e) {
                    return FormValidation.ok();
                }

                AiqUtil.ItemListFromString itemsObj = AiqUtil.getItemListFromString(value);

                if (itemsObj.getError() != null) {
                    // show error from processing text
                    return FormValidation.error(itemsObj.getError());
                } else if (itemsObj.getItemList().size() > 0) {
                    // try checking the items

                    try {
                        ServiceAccess svc = AutonomiqBuilder.getServiceAccess(proxyHost, proxyPort, proxyUser, proxyPassword, aiqUrl, login, password, httpProxy);
                        Set<String> set = getTestSuiteSet(svc, pd.getProjectId());
                        if (set != null) {
                            for (String item : itemsObj.getItemList()) {
                                if (!set.contains(item)) {
                                    return FormValidation.error(String.format("Test suite not found: '%s'", item));
                                }
                            }
                        }
                    } catch (Exception e) {
                        return FormValidation.ok();
                    }

                }

            }

            return FormValidation.ok();
        }
        

        private FormValidation checkTestCasesFromText(String value,
                                                      String aiqUrl,
                                                      String login,
                                                      Secret password,
                                                      String project,
                                                      String proxyHost,
                                                      String proxyPort,
                                                      String proxyUser,
                                                      Secret proxyPassword,
                                                      Boolean httpProxy) {
        	Jenkins.get().checkPermission(Jenkins.ADMINISTER);
            if (value.length() > 0
                    && aiqUrl.length() > 0
                    && login.length() > 0
                    && Secret.toString(password).length() > 0
                    && project.length() > 0) {

                // get the project or skip
                ProjectData pd;
                try {
                    pd = AiqUtil.gson.fromJson(project, ProjectData.class);
                } catch (Exception e) {
                    return FormValidation.ok();
                }

                AiqUtil.ItemListFromString itemsObj = AiqUtil.getItemListFromString(value);

                if (itemsObj.getError() != null) {
                    // show error from processing text
                    return FormValidation.error(itemsObj.getError());
                } else if (itemsObj.getItemList().size() > 0) {
                    // try checking the items

                    try {
                        ServiceAccess svc = AutonomiqBuilder.getServiceAccess(proxyHost, proxyPort, proxyUser, proxyPassword, aiqUrl, login, password, httpProxy);
                        Set<String> set = getTestCaseSet(svc, pd.getProjectId());
                        if (set != null) {
                            for (String item : itemsObj.getItemList()) {
                                if (!set.contains(item)) {
                                    return FormValidation.error(String.format("Test case not found: '%s'", item));
                                }
                            }
                        }
                    } catch (Exception e) {
                        return FormValidation.ok();
                    }

                }

            }

            return FormValidation.ok();

        }

        private Set<String> getTestCaseSet(ServiceAccess svc, Long projectId) {
            try {
                Set<String> set = new HashSet<>();

                List<TestCasesResponse> cases = svc.getTestCasesForProject(projectId);

                for (TestCasesResponse t : cases) {
                    set.add(t.getTestCaseName());
                }
                return set;
            } catch (Exception e) {
                return null;
            }
        }

        private Set<String> getTestSuiteSet(ServiceAccess svc, Long projectId) {
            try {
                Set<String> set = new HashSet<>();

                List<GetTestSuitesResponse> suites = svc.getTestSuitesForProject(projectId);

                for (GetTestSuitesResponse t : suites) {
                    set.add(t.getTestSuiteName());
                }
                return set;
            } catch (Exception e) {
                return null;
            }
        }

        @SuppressWarnings("unused")
        public String getDefaultAiqUrl() {
            String ret = AutonomiqConfiguration.get().getDefaultAiqUrl();
            return ret;
        }

        @SuppressWarnings("unused")
        public String getDefaultLogin() {
            String ret = AutonomiqConfiguration.get().getDefaultLogin();
            return ret;
        }

        @SuppressWarnings("unused")
        public Secret getDefaultPassword() {
            Secret ret = AutonomiqConfiguration.get().getDefaultPassword();
            return ret;
        }



        @Override
        public boolean isApplicable(Class<? extends AbstractProject> aClass) {
            return true;
        }

        @Override
        public String getDisplayName() {
            return Messages.AutonomiqBuilder_DescriptorImpl_DisplayName();
        }

        @SuppressWarnings("unused")
        @POST
        public ListBoxModel doFillProjectItems(@QueryParameter String aiqUrl,
                                               @QueryParameter String login,
                                               @QueryParameter Secret password,
                                               @QueryParameter String proxyHost,
                                               @QueryParameter String proxyPort,
                                               @QueryParameter String proxyUser,
                                               @QueryParameter Secret proxyPassword,
                                               @QueryParameter Boolean httpProxy) {
        	Jenkins.get().checkPermission(Jenkins.ADMINISTER);


            // make sure other fields have been filled in
            if (aiqUrl.length() > 0 && login.length() > 0 && Secret.toString(password).length() > 0) {

                try {

                    Option[] options = getProjectOptions(aiqUrl, login, password, proxyHost, proxyPort, proxyUser, proxyPassword, httpProxy);

                    return new ListBoxModel(options);

                } catch (Exception e) {
                    //
                }
            }

            return new ListBoxModel();

        }

        @SuppressWarnings("unused")
        @POST
        public ListBoxModel doFillEnvironmentTypeTestcasesItems(@QueryParameter String aiqUrl,
                @QueryParameter String login,
                @QueryParameter Secret password,
                @QueryParameter String proxyHost,
                @QueryParameter String proxyPort,
                @QueryParameter String proxyUser,
                @QueryParameter Secret proxyPassword,
                @QueryParameter Boolean httpProxy) throws ServiceException {

        	if (aiqUrl.length() > 0 && login.length() > 0 && Secret.toString(password).length() > 0) {

            String[] values= getEnvironmentType(aiqUrl, login, password, proxyHost, proxyPort, proxyUser, proxyPassword, httpProxy);

            Option[] options = buildSimpleOptions(values);

            return  new ListBoxModel(options);
        	}

        	return new ListBoxModel();
        }
        
       
        @SuppressWarnings("unused")
        @POST
        public ListBoxModel doFillPlatformTestCasesItems(@QueryParameter String environmentTypeTestcases,@QueryParameter String aiqUrl,
                @QueryParameter String login,
                @QueryParameter Secret password,
                @QueryParameter String proxyHost,
                @QueryParameter String proxyPort,
                @QueryParameter String proxyUser,
                @QueryParameter Secret proxyPassword,
                @QueryParameter Boolean httpProxy) throws ServiceException, InvocationTargetException {
        	
        	if (environmentTypeTestcases.equalsIgnoreCase("Saucelabs")) {
        		
        		String[] values= getplatformType(environmentTypeTestcases,aiqUrl, login, password, proxyHost, proxyPort, proxyUser, proxyPassword, httpProxy);

        		Option[] options = buildSimpleOptions(values);

        		return new ListBoxModel(options);
        	}
        	else if (environmentTypeTestcases.equalsIgnoreCase("Local")) {
        	
        		 String[] values = {"Linux"};  //, "Windows"};

                 Option[] options = buildSimpleOptions(values);

                 return new ListBoxModel(options);
        	}
        	else if (aiqUrl.length() > 0 && login.length() > 0 && Secret.toString(password).length() > 0)
        	{

         		String[] values= getplatformType(environmentTypeTestcases,aiqUrl, login, password, proxyHost, proxyPort, proxyUser, proxyPassword, httpProxy);

                Option[] options = buildSimpleOptions(values);
                 //String[] values = {"Windows 10","macOS 11.00","macOS 10.15"};  //, "Windows"};

                 //Option[] options = buildSimpleOptions(values);

                 return  new ListBoxModel(options);
        	}
        	else
        	return new ListBoxModel();

        }

        @SuppressWarnings("unused")
        @POST
        public ListBoxModel doFillBrowserTestCasesItems(@QueryParameter String environmentTypeTestcases,@QueryParameter String platformTestCases,@QueryParameter String aiqUrl,
                @QueryParameter String login,
                @QueryParameter Secret password,
                @QueryParameter String proxyHost,
                @QueryParameter String proxyPort,
                @QueryParameter String proxyUser,
                @QueryParameter Secret proxyPassword,
                @QueryParameter Boolean httpProxy) throws ServiceException {

        	 if(platformTestCases.equalsIgnoreCase("Android (Beta)")){
           	  platformTestCases="Android";
             }

        	if (environmentTypeTestcases.equalsIgnoreCase("Saucelabs") && platformTestCases.equalsIgnoreCase("Android"))
        	{
        		//String[] values= getMobileversion(platformTestCases,aiqUrl, login, password, proxyHost, proxyPort, proxyUser, proxyPassword, httpProxy);
   				String[] values = {"NotApplicable"};
   				Option[] options = buildSimpleOptions(values);

   				return new ListBoxModel(options);
        	}
        	
        	else if ((environmentTypeTestcases.equalsIgnoreCase("Saucelabs")) && (platformTestCases.equalsIgnoreCase("macOS 11.00") || platformTestCases.equalsIgnoreCase("macOS 10.15")||platformTestCases.equalsIgnoreCase("Windows 10")))
        	{
       				String[] values= getBrowser(environmentTypeTestcases,platformTestCases,aiqUrl, login, password, proxyHost, proxyPort, proxyUser, proxyPassword, httpProxy);
       				Option[] options = buildSimpleOptions(values);

       				return new ListBoxModel(options);
        	}

        	else if (environmentTypeTestcases.equalsIgnoreCase("Local"))
        	{

        	   if (platformTestCases.equalsIgnoreCase("Linux"))
        	   {
       			String[] values = {"Chrome (headless)","Firefox (headless)","Chrome (headful)","Firefox (headful)"};  //, "Windows"};
                Option[] options = buildSimpleOptions(values);

                return new ListBoxModel(options);
        	   }
        	}
        	else if (aiqUrl.length() > 0 && login.length() > 0 && Secret.toString(password).length() > 0)
        	{
     			String[] values= getBrowser(environmentTypeTestcases,platformTestCases,aiqUrl, login, password, proxyHost, proxyPort, proxyUser, proxyPassword, httpProxy);

                 Option[] options = buildSimpleOptions(values);

                 return  new ListBoxModel(options);
        	}
        	else
        	{
        		return new ListBoxModel();
        	}
            return new ListBoxModel();
        }

        @SuppressWarnings("unused")
        @POST
        public ListBoxModel doFillBrowserVersionTestcasesItems(@QueryParameter String environmentTypeTestcases,@QueryParameter String platformTestCases,@QueryParameter String browserTestCases,@QueryParameter String aiqUrl,
                @QueryParameter String login,
                @QueryParameter Secret password,
                @QueryParameter String proxyHost,
                @QueryParameter String proxyPort,
                @QueryParameter String proxyUser,
                @QueryParameter Secret proxyPassword,
                @QueryParameter Boolean httpProxy) throws ServiceException {

        	if(platformTestCases.equalsIgnoreCase("Android (Beta)")){
           	  platformTestCases="Android";
             }
        	
        	if (environmentTypeTestcases.equalsIgnoreCase("Saucelabs") && platformTestCases.equalsIgnoreCase("Android"))
        	{
   				//String[] values= getMobileversion(platformTestCases,aiqUrl, login, password, proxyHost, proxyPort, proxyUser, proxyPassword, httpProxy);
   				String[] values = {"NotApplicable"};
   				Option[] options = buildSimpleOptions(values);

   				return new ListBoxModel(options);
    	   }
        	else if ((environmentTypeTestcases.equalsIgnoreCase("Saucelabs")) && (platformTestCases.equalsIgnoreCase("macOS 11.00") || platformTestCases.equalsIgnoreCase("macOS 10.15")||platformTestCases.equalsIgnoreCase("Windows 10")))
        	{
        		 String[] values= getBrowserVersion(platformTestCases,browserTestCases,aiqUrl, login, password, proxyHost, proxyPort, proxyUser, proxyPassword, httpProxy);
                 Option[] options = buildSimpleOptions(values);
                 return new ListBoxModel(options);

        	}
        	else if (environmentTypeTestcases.equalsIgnoreCase("Local"))
        	{
				if((platformTestCases.equalsIgnoreCase("Linux"))&&(browserTestCases.equalsIgnoreCase("Chrome (headless)") || browserTestCases.equalsIgnoreCase("Firefox (headless)") || browserTestCases.equalsIgnoreCase("Chrome (headful)") || browserTestCases.equalsIgnoreCase("Firefox (headful)") || browserTestCases.equalsIgnoreCase("Chrome") || browserTestCases.length()==0))
        		{
             		String[] values = {"NotApplicable"};  //, "Windows"};
                    Option[] options = buildSimpleOptions(values);
                    return new ListBoxModel(options);

        	  }
        	}
        	else if (aiqUrl.length() > 0 && login.length() > 0 && Secret.toString(password).length() > 0)
        	{
        		 String[] values= getBrowserVersion(platformTestCases,browserTestCases,aiqUrl, login, password, proxyHost, proxyPort, proxyUser, proxyPassword, httpProxy);
                 Option[] options = buildSimpleOptions(values);
                 return  new ListBoxModel(options);
        	}
        	else
        	{
        		return new ListBoxModel();
        	}

        	return new ListBoxModel();
        }
        @SuppressWarnings("unused")
        @POST
        public ListBoxModel doFillSauceConnectProxyTestcasesItems(@QueryParameter String environmentTypeTestcases,@QueryParameter String aiqUrl,
                @QueryParameter String login,
                @QueryParameter Secret password,
                @QueryParameter String proxyHost,
                @QueryParameter String proxyPort,
                @QueryParameter String proxyUser,
                @QueryParameter Secret proxyPassword,
                @QueryParameter Boolean httpProxy) throws ServiceException
        {

        	if (environmentTypeTestcases.equalsIgnoreCase("saucelabs")) {

            String[] values= getSauceconnect(aiqUrl, login, password, proxyHost, proxyPort, proxyUser, proxyPassword, httpProxy);

            Option[] options = buildSimpleOptions(values);

            return new ListBoxModel(options);
        	}
        	else if( environmentTypeTestcases.equalsIgnoreCase("Local"))
        	{
                 String[] values = {"NotApplicable"};
                 Option[] options = buildSimpleOptions(values);
                 return new ListBoxModel(options);
        	}
        	else if (aiqUrl.length() > 0 && login.length() > 0 && Secret.toString(password).length() > 0)
        	{

                 String[] values= getSauceconnect(aiqUrl, login, password, proxyHost, proxyPort, proxyUser, proxyPassword, httpProxy);
                 Option[] options = buildSimpleOptions(values);
                 return  new ListBoxModel(options);
        	}
        	else
        	return new ListBoxModel();
        }

        @SuppressWarnings("unused")
        @POST
        public ListBoxModel doFillEnvironmentTypeItems(@QueryParameter String environmentType,@QueryParameter String aiqUrl,
                @QueryParameter String login,
                @QueryParameter Secret password,
                @QueryParameter String proxyHost,
                @QueryParameter String proxyPort,
                @QueryParameter String proxyUser,
                @QueryParameter Secret proxyPassword,
                @QueryParameter Boolean httpProxy) throws ServiceException {


        	if (aiqUrl.length() > 0 && login.length() > 0 && Secret.toString(password).length() > 0) {

            String[] values= getEnvironmentType(aiqUrl, login, password, proxyHost, proxyPort, proxyUser, proxyPassword, httpProxy);
        	Option[] options = buildSimpleOptions(values);
        	System.out.println(options);
            return  new ListBoxModel(options);
        	}

        	return new ListBoxModel();
        }

        @SuppressWarnings("unused")
        @POST
        public ListBoxModel doFillPlatformTestSuitesItems(@QueryParameter String environmentType,@QueryParameter String aiqUrl,
                @QueryParameter String login,
                @QueryParameter Secret password,
                @QueryParameter String proxyHost,
                @QueryParameter String proxyPort,
                @QueryParameter String proxyUser,
                @QueryParameter Secret proxyPassword,
                @QueryParameter Boolean httpProxy) throws ServiceException, InvocationTargetException {

        	

        	if (environmentType.equalsIgnoreCase("Saucelabs")) {

        		String[] values= getplatformType(environmentType,aiqUrl, login, password, proxyHost, proxyPort, proxyUser, proxyPassword, httpProxy);

        		Option[] options = buildSimpleOptions(values);

        		return new ListBoxModel(options);
        	}
        	else if (environmentType.equalsIgnoreCase("Local")) {
        	
        		 String[] values = {"Linux"};  //, "Windows"};

                 Option[] options = buildSimpleOptions(values);

                 return new ListBoxModel(options);
        	}
        	else if (aiqUrl.length() > 0 && login.length() > 0 && Secret.toString(password).length() > 0)
        	{

         		String[] values= getplatformType(environmentType,aiqUrl, login, password, proxyHost, proxyPort, proxyUser, proxyPassword, httpProxy);

                 Option[] options = buildSimpleOptions(values);

                 return  new ListBoxModel(options);
        	}
        	else
        	return new ListBoxModel();

        }

        @SuppressWarnings("unused")
        @POST
        public ListBoxModel doFillBrowserTestSuitesItems(@QueryParameter String environmentType,@QueryParameter String platformTestSuites,@QueryParameter String aiqUrl,
                @QueryParameter String login,
                @QueryParameter Secret password,
                @QueryParameter String proxyHost,
                @QueryParameter String proxyPort,
                @QueryParameter String proxyUser,
                @QueryParameter Secret proxyPassword,
                @QueryParameter Boolean httpProxy) throws ServiceException {

      	  if(platformTestSuites.equalsIgnoreCase("Android (Beta)")){
    		  platformTestSuites="Android";
          }

        	if (environmentType.equalsIgnoreCase("Saucelabs") && platformTestSuites.equalsIgnoreCase("Android"))
        	{
       				//String[] values= getBrowser(environmentType,platformTestSuites,aiqUrl, login, password, proxyHost, proxyPort, proxyUser, proxyPassword, httpProxy);
        		    String[] values = {"NotApplicable"};
        		    Option[] options = buildSimpleOptions(values);

       				return new ListBoxModel(options);
        	}
        	else if ((environmentType.equalsIgnoreCase("Saucelabs")) && (platformTestSuites.equalsIgnoreCase("macOS 11.00") || platformTestSuites.equalsIgnoreCase("macOS 10.15")|| platformTestSuites.equalsIgnoreCase("Windows 10")))
        	{
       				String[] values= getBrowser(environmentType,platformTestSuites,aiqUrl, login, password, proxyHost, proxyPort, proxyUser, proxyPassword, httpProxy);
       				Option[] options = buildSimpleOptions(values);

       				return new ListBoxModel(options);
        	}

        	else if (environmentType.equalsIgnoreCase("Local"))
        	{

        	   if (platformTestSuites.equalsIgnoreCase("Linux"))
        	   {
       			String[] values = {"Chrome (headless)","Firefox (headless)","Chrome (headful)","Firefox (headful)"};  //, "Windows"};
                Option[] options = buildSimpleOptions(values);

                return new ListBoxModel(options);
        	   }
        	}
        	else if (aiqUrl.length() > 0 && login.length() > 0 && Secret.toString(password).length() > 0)
        	{
     			String[] values= getBrowser(environmentType,platformTestSuites,aiqUrl, login, password, proxyHost, proxyPort, proxyUser, proxyPassword, httpProxy);

                 Option[] options = buildSimpleOptions(values);

                 return  new ListBoxModel(options);
        	}
        	else
        	{
        		return new ListBoxModel();
        	}
            return new ListBoxModel();
        }

        @SuppressWarnings("unused")
        @POST
        public ListBoxModel doFillBrowserVersionItems(@QueryParameter String environmentType,@QueryParameter String platformTestSuites,@QueryParameter String browserTestSuites,@QueryParameter String aiqUrl,
                @QueryParameter String login,
                @QueryParameter Secret password,
                @QueryParameter String proxyHost,
                @QueryParameter String proxyPort,
                @QueryParameter String proxyUser,
                @QueryParameter Secret proxyPassword,
                @QueryParameter Boolean httpProxy) throws ServiceException {

        	//System.out.println("env type"+environmentType);
        	//System.out.println("browser testsuites"+browserTestSuites);
      	  if(platformTestSuites.equalsIgnoreCase("Android (Beta)")){
    		  platformTestSuites="Android";
          }

        	if (environmentType.equalsIgnoreCase("Saucelabs") && platformTestSuites.equalsIgnoreCase("Android"))
        	{
        		 //String[] values= getBrowserVersion(platformTestSuites,browserTestSuites,aiqUrl, login, password, proxyHost, proxyPort, proxyUser, proxyPassword, httpProxy);
        		String[] values = {"NotApplicable"}; 
        		Option[] options = buildSimpleOptions(values);
                 return new ListBoxModel(options);
        	}
        	
        	else if ((environmentType.equalsIgnoreCase("Saucelabs")) && (platformTestSuites.equalsIgnoreCase("macOS 11.00") || platformTestSuites.equalsIgnoreCase("macOS 10.15")||platformTestSuites.equalsIgnoreCase("Windows 10")))
        	{
        		 String[] values= getBrowserVersion(platformTestSuites,browserTestSuites,aiqUrl, login, password, proxyHost, proxyPort, proxyUser, proxyPassword, httpProxy);
                 Option[] options = buildSimpleOptions(values);
                 return new ListBoxModel(options);

        	}
        	
        	else if (environmentType.equalsIgnoreCase("Local"))
        	{
        		try {
					if((platformTestSuites.equalsIgnoreCase("Linux"))&&(browserTestSuites.equalsIgnoreCase("Chrome (headless)") || browserTestSuites.equalsIgnoreCase("Firefox (headless)") || browserTestSuites.equalsIgnoreCase("Chrome (headful)") || browserTestSuites.equalsIgnoreCase("Firefox (headful)") || browserTestSuites.equalsIgnoreCase("Chrome") || browserTestSuites.length()==0))
					{
						String[] values = {"NotApplicable"};  //, "Windows"};
					    Option[] options = buildSimpleOptions(values);
					    
					    return new ListBoxModel(options);

     	  }
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
        	}
        	else if (aiqUrl.length() > 0 && login.length() > 0 && Secret.toString(password).length() > 0)
        	{
        		 String[] values= getBrowserVersion(platformTestSuites,browserTestSuites,aiqUrl, login, password, proxyHost, proxyPort, proxyUser, proxyPassword, httpProxy);
                 Option[] options = buildSimpleOptions(values);
                 return  new ListBoxModel(options);
        	}
        	else
        	{
        		return new ListBoxModel();
        	}

        	return new ListBoxModel();
        }

        @SuppressWarnings("unused")
        @POST
        public ListBoxModel doFillSauceConnectProxyItems(@QueryParameter String environmentType,@QueryParameter String aiqUrl,
                @QueryParameter String login,
                @QueryParameter Secret password,
                @QueryParameter String proxyHost,
                @QueryParameter String proxyPort,
                @QueryParameter String proxyUser,
                @QueryParameter Secret proxyPassword,
                @QueryParameter Boolean httpProxy) throws ServiceException
        {

        	if (environmentType.equalsIgnoreCase("saucelabs")) {
        	Jenkins.get().checkPermission(Jenkins.ADMINISTER);
                String[] values= getSauceconnect(aiqUrl, login, password, proxyHost, proxyPort, proxyUser, proxyPassword, httpProxy);

                Option[] options = buildSimpleOptions(values);

                return new ListBoxModel(options);
            	}
            	else if( environmentType.equalsIgnoreCase("Local"))
            	{
                     String[] values = {"NotApplicable"};
                     Option[] options = buildSimpleOptions(values);
                     return new ListBoxModel(options);
            	}
            	else if (aiqUrl.length() > 0 && login.length() > 0 && Secret.toString(password).length() > 0)
            	{

                     String[] values= getSauceconnect(aiqUrl, login, password, proxyHost, proxyPort, proxyUser, proxyPassword, httpProxy);
                     Option[] options = buildSimpleOptions(values);
                     return  new ListBoxModel(options);
            	}
            	else
            	return new ListBoxModel();
        }

        @SuppressWarnings("unused")
        @POST
        public ListBoxModel doFillExecutionModeItems(@QueryParameter String aiqUrl,
                @QueryParameter String login,
                @QueryParameter Secret password,
                @QueryParameter String proxyHost,
                @QueryParameter String proxyPort,
                @QueryParameter String proxyUser,
                @QueryParameter Secret proxyPassword,
                @QueryParameter Boolean httpProxy) {
        	Jenkins.get().checkPermission(Jenkins.ADMINISTER);
        	if (aiqUrl.length() > 0 && login.length() > 0 && Secret.toString(password).length() > 0) {
            String[] values = {"serial", "parallel"};

            Option[] options = buildSimpleOptions(values);

            return new ListBoxModel(options);
        	}

        	return new ListBoxModel();
        }
        
       // mobile starts
        
        @SuppressWarnings("unused")
        @POST
        public ListBoxModel doFillMobileSauceConnectProxyItems(@QueryParameter String aiqUrl,
                @QueryParameter String login,
                @QueryParameter Secret password,
                @QueryParameter String proxyHost,
                @QueryParameter String proxyPort,
                @QueryParameter String proxyUser,
                @QueryParameter Secret proxyPassword,
                @QueryParameter Boolean httpProxy) throws ServiceException {
        	Jenkins.get().checkPermission(Jenkins.ADMINISTER);
        	if (aiqUrl.length() > 0 && login.length() > 0 && Secret.toString(password).length() > 0) {
            String[] values= getSauceconnect(aiqUrl, login, password, proxyHost, proxyPort, proxyUser, proxyPassword, httpProxy);

            Option[] options = buildSimpleOptions(values);

            return new ListBoxModel(options);
        }
        	return new ListBoxModel();
        }
        
        @SuppressWarnings("unused")
        @POST
        public ListBoxModel doFillMobileSauceConnectProxyTcItems(@QueryParameter String aiqUrl,
                @QueryParameter String login,
                @QueryParameter Secret password,
                @QueryParameter String proxyHost,
                @QueryParameter String proxyPort,
                @QueryParameter String proxyUser,
                @QueryParameter Secret proxyPassword,
                @QueryParameter Boolean httpProxy) throws ServiceException {
        	Jenkins.get().checkPermission(Jenkins.ADMINISTER);
        	if (aiqUrl.length() > 0 && login.length() > 0 && Secret.toString(password).length() > 0) {
        	
            String[] values= getSauceconnect(aiqUrl, login, password, proxyHost, proxyPort, proxyUser, proxyPassword, httpProxy);

            Option[] options = buildSimpleOptions(values);

            return new ListBoxModel(options);
        	}
        	return new ListBoxModel();
          }
        
        @SuppressWarnings("unused")
        @POST
        public ListBoxModel doFillMobileplatformTestSuitesItems(@QueryParameter String aiqUrl,@QueryParameter Boolean mobileplatformTestSuites,
                @QueryParameter String login,
                @QueryParameter Secret password,
                @QueryParameter String proxyHost,
                @QueryParameter String proxyPort,
                @QueryParameter String proxyUser,
                @QueryParameter Secret proxyPassword,
                @QueryParameter Boolean httpProxy) {
        	Jenkins.get().checkPermission(Jenkins.ADMINISTER);
        	System.out.println("values of boolean"+mobileplatformTestSuites);
        	if (aiqUrl.length() > 0 && login.length() > 0 && Secret.toString(password).length() > 0) {
        	
            
            String[] values = {"Android"};

            Option[] options = buildSimpleOptions(values);

            return new ListBoxModel(options);
        	}
        	return new ListBoxModel();
        }
        
        @SuppressWarnings("unused")
        @POST
        public ListBoxModel doFillMobileplatformTestcasesItems(@QueryParameter String aiqUrl,
                @QueryParameter String login,
                @QueryParameter Secret password,
                @QueryParameter String proxyHost,
                @QueryParameter String proxyPort,
                @QueryParameter String proxyUser,
                @QueryParameter Secret proxyPassword,
                @QueryParameter Boolean httpProxy) {
        	Jenkins.get().checkPermission(Jenkins.ADMINISTER);
        	if (aiqUrl.length() > 0 && login.length() > 0 && Secret.toString(password).length() > 0) {
        	
        	
            String[] values = {"Android"};

            Option[] options = buildSimpleOptions(values);

            return new ListBoxModel(options);
        }
        	return new ListBoxModel();
        }
        
        @SuppressWarnings("unused")
        @POST
        public ListBoxModel doFillMobileExecutionModeItems(@QueryParameter String aiqUrl,
                @QueryParameter String login,
                @QueryParameter Secret password,
                @QueryParameter String proxyHost,
                @QueryParameter String proxyPort,
                @QueryParameter String proxyUser,
                @QueryParameter Secret proxyPassword,
                @QueryParameter Boolean httpProxy) {
        	Jenkins.get().checkPermission(Jenkins.ADMINISTER);
        	if (aiqUrl.length() > 0 && login.length() > 0 && Secret.toString(password).length() > 0) {
            String[] values = {"serial", "parallel"};

            Option[] options = buildSimpleOptions(values);

            return new ListBoxModel(options);
        }

        	return new ListBoxModel();
        }
        
        @SuppressWarnings("unused")
        @POST
        public ListBoxModel doFillMobilePlatformVersionItems(@QueryParameter String environmentType,@QueryParameter String platformTestSuites,@QueryParameter String mobileplatformTestSuites,@QueryParameter String aiqUrl,
                @QueryParameter String login,
                @QueryParameter Secret password,
                @QueryParameter String proxyHost,
                @QueryParameter String proxyPort,
                @QueryParameter String proxyUser,
                @QueryParameter Secret proxyPassword,
                @QueryParameter Boolean httpProxy) throws ServiceException {
        	Jenkins.get().checkPermission(Jenkins.ADMINISTER);

        	if(platformTestSuites.equalsIgnoreCase("Android (Beta)")){
      		  platformTestSuites="Android";
            }
        	if (environmentType.equalsIgnoreCase("Saucelabs") && platformTestSuites.equalsIgnoreCase("Android"))
        	{
       				String[] values= getMobileversion(platformTestSuites,aiqUrl, login, password, proxyHost, proxyPort, proxyUser, proxyPassword, httpProxy);
       				Option[] options = buildSimpleOptions(values);

       				return new ListBoxModel(options);
        	}
        	else if ((environmentType.equalsIgnoreCase("Saucelabs")) && (platformTestSuites.equalsIgnoreCase("macOS 11.00") || platformTestSuites.equalsIgnoreCase("macOS 10.15")||platformTestSuites.equalsIgnoreCase("Windows 10")))
        	{
       				//String[] values= getMobileversion(platformTestCases,aiqUrl, login, password, proxyHost, proxyPort, proxyUser, proxyPassword, httpProxy);
       				String[] values = {"NotApplicable"};
       				Option[] options = buildSimpleOptions(values);

       				return new ListBoxModel(options);
        	}
        	else if (environmentType.equalsIgnoreCase("Local"))
        	{

        	   if (platformTestSuites.equalsIgnoreCase("Linux"))
        	   {
        		   String[] values = {"NotApplicable"};
       			//String[] values = {"Chrome (headless)","Firefox (headless)","Chrome (headful)","Firefox (headful)"};  //, "Windows"};
                Option[] options = buildSimpleOptions(values);

                return new ListBoxModel(options);
        	   }
        	}
        	else if (aiqUrl.length() > 0 && login.length() > 0 && Secret.toString(password).length() > 0)
        	{

   			 String[] values = {"NotApplicable"};
                Option[] options = buildSimpleOptions(values);

                return  new ListBoxModel(options);
        	}
        	else
        	{
        		return new ListBoxModel();
        	}
            return new ListBoxModel();
        }
        
        @SuppressWarnings("unused")
        @POST
        public ListBoxModel doFillMobilePlatformVersionTcItems(@QueryParameter String environmentTypeTestcases,@QueryParameter String platformTestCases,@QueryParameter String mobileplatformTestcases,@QueryParameter String aiqUrl,
                @QueryParameter String login,
                @QueryParameter Secret password,
                @QueryParameter String proxyHost,
                @QueryParameter String proxyPort,
                @QueryParameter String proxyUser,
                @QueryParameter Secret proxyPassword,
                @QueryParameter Boolean httpProxy) throws ServiceException {
        	Jenkins.get().checkPermission(Jenkins.ADMINISTER);

        	if(platformTestCases.equalsIgnoreCase("Android (Beta)")){
        		platformTestCases="Android";
            }
        	//System.out.println(environmentTypeTestcases+"values of ev and PTc's :-"+platformTestCases);
        	if (environmentTypeTestcases.equalsIgnoreCase("Saucelabs") && platformTestCases.equalsIgnoreCase("Android"))
        	{
       				String[] values= getMobileversion(platformTestCases,aiqUrl, login, password, proxyHost, proxyPort, proxyUser, proxyPassword, httpProxy);
       				Option[] options = buildSimpleOptions(values);

       				return new ListBoxModel(options);
        	}
        	else if ((environmentTypeTestcases.equalsIgnoreCase("Saucelabs")) && (platformTestCases.equalsIgnoreCase("macOS 11.00") || platformTestCases.equalsIgnoreCase("macOS 10.15")||platformTestCases.equalsIgnoreCase("Windows 10")))
        	{
       				//String[] values= getMobileversion(platformTestCases,aiqUrl, login, password, proxyHost, proxyPort, proxyUser, proxyPassword, httpProxy);
       				String[] values = {"NotApplicable"};
       				Option[] options = buildSimpleOptions(values);

       				return new ListBoxModel(options);
        	}

        	else if (environmentTypeTestcases.equalsIgnoreCase("Local"))
        	{

        	   if (platformTestCases.equalsIgnoreCase("Linux"))
        	   {
        		   String[] values = {"NotApplicable"};
       			//String[] values = {"Chrome (headless)","Firefox (headless)","Chrome (headful)","Firefox (headful)"};  //, "Windows"};
                Option[] options = buildSimpleOptions(values);

                return new ListBoxModel(options);
        	   }
        	}
        	
        	else if (aiqUrl.length() > 0 && login.length() > 0 && Secret.toString(password).length() > 0)
        	{

   			 String[] values = {"NotApplicable"};
                Option[] options = buildSimpleOptions(values);

                return  new ListBoxModel(options);
        	}
        	else
        	{
        		return new ListBoxModel();
        	}
            return new ListBoxModel();
        }
        
        
        
        @SuppressWarnings("unused")
        @POST
        public ListBoxModel doFillDeviceNameItems(@QueryParameter String environmentType,@QueryParameter String platformTestSuites,@QueryParameter String mobilePlatformVersion,@QueryParameter String aiqUrl,
                @QueryParameter String login,
                @QueryParameter Secret password,
                @QueryParameter String proxyHost,
                @QueryParameter String proxyPort,
                @QueryParameter String proxyUser,
                @QueryParameter Secret proxyPassword,
                @QueryParameter Boolean httpProxy) throws ServiceException {
        	Jenkins.get().checkPermission(Jenkins.ADMINISTER);

        	if(platformTestSuites.equalsIgnoreCase("Android (Beta)")){
        		platformTestSuites="Android";
              }

        	if (environmentType.equalsIgnoreCase("Saucelabs") && platformTestSuites.equalsIgnoreCase("Android"))
        	{ 
        		 
       				String[] values= getDevice(platformTestSuites,mobilePlatformVersion,aiqUrl, login, password, proxyHost, proxyPort, proxyUser, proxyPassword, httpProxy);
       				Option[] options = buildSimpleOptions(values);

       				return new ListBoxModel(options);
        	}
        	else if ((environmentType.equalsIgnoreCase("Saucelabs")) && (platformTestSuites.equalsIgnoreCase("macOS 11.00") || platformTestSuites.equalsIgnoreCase("macOS 10.15")||platformTestSuites.equalsIgnoreCase("Windows 10")))
        	{
       				//String[] values= getMobileversion(platformTestCases,aiqUrl, login, password, proxyHost, proxyPort, proxyUser, proxyPassword, httpProxy);
       				String[] values = {"NotApplicable"};
       				Option[] options = buildSimpleOptions(values);

       				return new ListBoxModel(options);
        	}
        	else if (environmentType.equalsIgnoreCase("Local"))
        	{

        	   if (platformTestSuites.equalsIgnoreCase("Linux"))
        	   {
        		   String[] values = {"NotApplicable"};
       			//String[] values = {"Chrome (headless)","Firefox (headless)","Chrome (headful)","Firefox (headful)"};  //, "Windows"};
                Option[] options = buildSimpleOptions(values);

                return new ListBoxModel(options);
        	   }
        	}
        	else if (aiqUrl.length() > 0 && login.length() > 0 && Secret.toString(password).length() > 0)
        	{

   			    String[] values = {"NotApplicable"};
                Option[] options = buildSimpleOptions(values);

                return  new ListBoxModel(options);
        	}
        	else
        	{
        		return new ListBoxModel();
        	}
            return new ListBoxModel();
        }
        
        @SuppressWarnings("unused")
        @POST
        public ListBoxModel doFillDeviceNameTestcasesItems(
        		@QueryParameter String environmentTypeTestcases,@QueryParameter String platformTestCases,@QueryParameter String mobilePlatformVersionTc,@QueryParameter String aiqUrl,
                @QueryParameter String login,
                @QueryParameter Secret password,
                @QueryParameter String proxyHost,
                @QueryParameter String proxyPort,
                @QueryParameter String proxyUser,
                @QueryParameter Secret proxyPassword,
                @QueryParameter Boolean httpProxy) throws ServiceException {
        	Jenkins.get().checkPermission(Jenkins.ADMINISTER);

        	if(platformTestCases.equalsIgnoreCase("Android (Beta)")){
        		platformTestCases="Android";
              }

           if (environmentTypeTestcases.equalsIgnoreCase("Saucelabs") && platformTestCases.equalsIgnoreCase("Android"))
        	{
       				String[] values= getDevice(platformTestCases,mobilePlatformVersionTc,aiqUrl, login, password, proxyHost, proxyPort, proxyUser, proxyPassword, httpProxy);
       				Option[] options = buildSimpleOptions(values);

       				return new ListBoxModel(options);
        	}
        	else if ((environmentTypeTestcases.equalsIgnoreCase("Saucelabs")) && (platformTestCases.equalsIgnoreCase("macOS 11.00") || platformTestCases.equalsIgnoreCase("macOS 10.15")||platformTestCases.equalsIgnoreCase("Windows 10")))
        	{
       				//String[] values= getMobileversion(platformTestCases,aiqUrl, login, password, proxyHost, proxyPort, proxyUser, proxyPassword, httpProxy);
       				String[] values = {"NotApplicable"};
       				Option[] options = buildSimpleOptions(values);

       				return new ListBoxModel(options);
        	}

        	else if (environmentTypeTestcases.equalsIgnoreCase("Local"))
        	{

        	   if (platformTestCases.equalsIgnoreCase("Linux"))
        	   {
        		   String[] values = {"NotApplicable"};
       			//String[] values = {"Chrome (headless)","Firefox (headless)","Chrome (headful)","Firefox (headful)"};  //, "Windows"};
                Option[] options = buildSimpleOptions(values);

                return new ListBoxModel(options);
        	   }
        	}
        	
        	else if (aiqUrl.length() > 0 && login.length() > 0 && Secret.toString(password).length() > 0)
        	{

   			    String[] values = {"NotApplicable"};
                Option[] options = buildSimpleOptions(values);

                return  new ListBoxModel(options);
        	}
        	else
        	{
        		return new ListBoxModel();
        	}
            return new ListBoxModel();
        }
        @SuppressWarnings("unused")
        @POST
        public ListBoxModel doFillDeviceOrientationItems(@QueryParameter String environmentType,@QueryParameter String platformTestSuites,
        		@QueryParameter String aiqUrl,
                @QueryParameter String login,
                @QueryParameter Secret password,
                @QueryParameter String proxyHost,
                @QueryParameter String proxyPort,
                @QueryParameter String proxyUser,
                @QueryParameter Secret proxyPassword,
                @QueryParameter Boolean httpProxy) {
        	Jenkins.get().checkPermission(Jenkins.ADMINISTER);

        	if(platformTestSuites.equalsIgnoreCase("Android (Beta)")){
        		platformTestSuites="Android";
              }
        	if (environmentType.equalsIgnoreCase("Saucelabs") && platformTestSuites.equalsIgnoreCase("Android"))
        	{
    			  String[] values = {"Portrait","Landscape"};

    	            Option[] options = buildSimpleOptions(values);

    	            return new ListBoxModel(options);
        	}
    		else if ((environmentType.equalsIgnoreCase("Saucelabs")) && (platformTestSuites.equalsIgnoreCase("macOS 11.00") || platformTestSuites.equalsIgnoreCase("macOS 10.15")||platformTestSuites.equalsIgnoreCase("Windows 10")))
        	{
       				//String[] values= getMobileversion(platformTestCases,aiqUrl, login, password, proxyHost, proxyPort, proxyUser, proxyPassword, httpProxy);
       				String[] values = {"NotApplicable"};
       				Option[] options = buildSimpleOptions(values);

       				return new ListBoxModel(options);
        	}
    		else if (environmentType.equalsIgnoreCase("Local"))
        	{

        	   if (environmentType.equalsIgnoreCase("Linux"))
        	   {
        		   String[] values = {"NotApplicable"};
       			//String[] values = {"Chrome (headless)","Firefox (headless)","Chrome (headful)","Firefox (headful)"};  //, "Windows"};
                Option[] options = buildSimpleOptions(values);

                return new ListBoxModel(options);
        	   }
        	}
    		else if (aiqUrl.length() > 0 && login.length() > 0 && Secret.toString(password).length() > 0)
        	{
    			 String[] values = {"NotApplicable"};
                 Option[] options = buildSimpleOptions(values);

                 return  new ListBoxModel(options);
        	}
    		else
        	{
        		return new ListBoxModel();
        	}
            return new ListBoxModel();	
        }
        
        @SuppressWarnings("unused")
        @POST
        public ListBoxModel doFillDeviceOrientationTcItems(@QueryParameter String environmentTypeTestcases,@QueryParameter String platformTestCases,
        		@QueryParameter String aiqUrl,
                @QueryParameter String login,
                @QueryParameter Secret password,
                @QueryParameter String proxyHost,
                @QueryParameter String proxyPort,
                @QueryParameter String proxyUser,
                @QueryParameter Secret proxyPassword,
                @QueryParameter Boolean httpProxy) {
        	Jenkins.get().checkPermission(Jenkins.ADMINISTER);
        	if(platformTestCases.equalsIgnoreCase("Android (Beta)")){
        		platformTestCases="Android";
              }
        		if (environmentTypeTestcases.equalsIgnoreCase("Saucelabs") && platformTestCases.equalsIgnoreCase("Android"))
            	{
        			  String[] values = {"Portrait","Landscape"};

        	            Option[] options = buildSimpleOptions(values);

        	            return new ListBoxModel(options);
            	}
        		else if ((environmentTypeTestcases.equalsIgnoreCase("Saucelabs")) && (platformTestCases.equalsIgnoreCase("macOS 11.00") || platformTestCases.equalsIgnoreCase("macOS 10.15")||platformTestCases.equalsIgnoreCase("Windows 10")))
            	{
           				//String[] values= getMobileversion(platformTestCases,aiqUrl, login, password, proxyHost, proxyPort, proxyUser, proxyPassword, httpProxy);
           				String[] values = {"NotApplicable"};
           				Option[] options = buildSimpleOptions(values);

           				return new ListBoxModel(options);
            	}
        		else if (environmentTypeTestcases.equalsIgnoreCase("Local"))
            	{

            	   if (platformTestCases.equalsIgnoreCase("Linux"))
            	   {
            		   String[] values = {"NotApplicable"};
           			//String[] values = {"Chrome (headless)","Firefox (headless)","Chrome (headful)","Firefox (headful)"};  //, "Windows"};
                    Option[] options = buildSimpleOptions(values);

                    return new ListBoxModel(options);
            	   }
            	}
        		else if (aiqUrl.length() > 0 && login.length() > 0 && Secret.toString(password).length() > 0)
            	{
        			 String[] values = {"NotApplicable"};
                     Option[] options = buildSimpleOptions(values);

                     return  new ListBoxModel(options);
            	}
        		else
            	{
            		return new ListBoxModel();
            	}
                return new ListBoxModel();
        	
        }
        

        @SuppressWarnings("unused")
        @POST
        public ListBoxModel doFillEnableAnimationsItems(@QueryParameter String environmentType,@QueryParameter String platformTestSuites,@QueryParameter String aiqUrl,
                @QueryParameter String login,
                @QueryParameter Secret password,
                @QueryParameter String proxyHost,
                @QueryParameter String proxyPort,
                @QueryParameter String proxyUser,
                @QueryParameter Secret proxyPassword,
                @QueryParameter Boolean httpProxy) {
        	Jenkins.get().checkPermission(Jenkins.ADMINISTER);
        	if(platformTestSuites.equalsIgnoreCase("Android (Beta)")){
        		platformTestSuites="Android";
              }
        	if (environmentType.equalsIgnoreCase("Saucelabs") && platformTestSuites.equalsIgnoreCase("Android"))
        	{
    			   String[] values = {"false","true"};
                               Option[] options = buildSimpleOptions(values);
                                 return new ListBoxModel(options);
        	}
    		else if ((environmentType.equalsIgnoreCase("Saucelabs")) && (platformTestSuites.equalsIgnoreCase("macOS 11.00") || platformTestSuites.equalsIgnoreCase("macOS 10.15")||platformTestSuites.equalsIgnoreCase("Windows 10")))
        	{
       				//String[] values= getMobileversion(platformTestCases,aiqUrl, login, password, proxyHost, proxyPort, proxyUser, proxyPassword, httpProxy);
       				String[] values = {"NotApplicable"};
       				Option[] options = buildSimpleOptions(values);

       				return new ListBoxModel(options);
        	}
    		else if (environmentType.equalsIgnoreCase("Local"))
        	{

        	   if (platformTestSuites.equalsIgnoreCase("Linux"))
        	   {
        		   String[] values = {"NotApplicable"};
       			//String[] values = {"Chrome (headless)","Firefox (headless)","Chrome (headful)","Firefox (headful)"};  //, "Windows"};
                Option[] options = buildSimpleOptions(values);

                return new ListBoxModel(options);
        	   }
        	}
    		else if (aiqUrl.length() > 0 && login.length() > 0 && Secret.toString(password).length() > 0)
        	{
    			 String[] values = {"NotApplicable"};
                 Option[] options = buildSimpleOptions(values);

                 return  new ListBoxModel(options);
        	}
    		else
        	{
        		return new ListBoxModel();
        	}
            return new ListBoxModel(); 
        }
        
        @SuppressWarnings("unused")
        @POST
        public ListBoxModel doFillEnableAnimationsTcItems(
        		@QueryParameter String environmentTypeTestcases,@QueryParameter String platformTestCases,@QueryParameter String aiqUrl,
                @QueryParameter String login,
                @QueryParameter Secret password,
                @QueryParameter String proxyHost,
                @QueryParameter String proxyPort,
                @QueryParameter String proxyUser,
                @QueryParameter Secret proxyPassword,
                @QueryParameter Boolean httpProxy) {
        	Jenkins.get().checkPermission(Jenkins.ADMINISTER);
        	if(platformTestCases.equalsIgnoreCase("Android (Beta)")){
        		platformTestCases="Android";
              }
        	if (environmentTypeTestcases.equalsIgnoreCase("Saucelabs") && platformTestCases.equalsIgnoreCase("Android"))
        	{
    			   String[] values = {"false","true"};
                               Option[] options = buildSimpleOptions(values);
                                 return new ListBoxModel(options);
        	}
    		else if ((environmentTypeTestcases.equalsIgnoreCase("Saucelabs")) && (platformTestCases.equalsIgnoreCase("macOS 11.00") || platformTestCases.equalsIgnoreCase("macOS 10.15")||platformTestCases.equalsIgnoreCase("Windows 10")))
        	{
       				//String[] values= getMobileversion(platformTestCases,aiqUrl, login, password, proxyHost, proxyPort, proxyUser, proxyPassword, httpProxy);
       				String[] values = {"NotApplicable"};
       				Option[] options = buildSimpleOptions(values);

       				return new ListBoxModel(options);
        	}
    		else if (environmentTypeTestcases.equalsIgnoreCase("Local"))
        	{

        	   if (platformTestCases.equalsIgnoreCase("Linux"))
        	   {
        		   String[] values = {"NotApplicable"};
       			//String[] values = {"Chrome (headless)","Firefox (headless)","Chrome (headful)","Firefox (headful)"};  //, "Windows"};
                Option[] options = buildSimpleOptions(values);

                return new ListBoxModel(options);
        	   }
        	}
    		else if (aiqUrl.length() > 0 && login.length() > 0 && Secret.toString(password).length() > 0)
        	{
    			 String[] values = {"NotApplicable"};
                 Option[] options = buildSimpleOptions(values);

                 return  new ListBoxModel(options);
        	}
    		else
        	{
        		return new ListBoxModel();
        	}
            return new ListBoxModel();    	
    }

        
        @SuppressWarnings("unused")
        @POST
        public ListBoxModel doFillAutoGrantPermissionItems(@QueryParameter String environmentType,@QueryParameter String platformTestSuites,@QueryParameter String aiqUrl,
                @QueryParameter String login,
                @QueryParameter Secret password,
                @QueryParameter String proxyHost,
                @QueryParameter String proxyPort,
                @QueryParameter String proxyUser,
                @QueryParameter Secret proxyPassword,
                @QueryParameter Boolean httpProxy) {
        	Jenkins.get().checkPermission(Jenkins.ADMINISTER);
        	if(platformTestSuites.equalsIgnoreCase("Android (Beta)")){
        		platformTestSuites="Android";
              }
        	if (environmentType.equalsIgnoreCase("Saucelabs") && platformTestSuites.equalsIgnoreCase("Android"))
        	{
    			   String[] values = {"false","true"};
                               Option[] options = buildSimpleOptions(values);
                                 return new ListBoxModel(options);
        	}
    		else if ((environmentType.equalsIgnoreCase("Saucelabs")) && (platformTestSuites.equalsIgnoreCase("macOS 11.00") || platformTestSuites.equalsIgnoreCase("macOS 10.15")||platformTestSuites.equalsIgnoreCase("Windows 10")))
        	{
       				//String[] values= getMobileversion(platformTestCases,aiqUrl, login, password, proxyHost, proxyPort, proxyUser, proxyPassword, httpProxy);
       				String[] values = {"NotApplicable"};
       				Option[] options = buildSimpleOptions(values);

       				return new ListBoxModel(options);
        	}
    		else if (environmentType.equalsIgnoreCase("Local"))
        	{

        	   if (platformTestSuites.equalsIgnoreCase("Linux"))
        	   {
        		   String[] values = {"NotApplicable"};
       			//String[] values = {"Chrome (headless)","Firefox (headless)","Chrome (headful)","Firefox (headful)"};  //, "Windows"};
                Option[] options = buildSimpleOptions(values);

                return new ListBoxModel(options);
        	   }
        	}
    		else if (aiqUrl.length() > 0 && login.length() > 0 && Secret.toString(password).length() > 0)
        	{
    			 String[] values = {"NotApplicable"};
                 Option[] options = buildSimpleOptions(values);

                 return  new ListBoxModel(options);
        	}
    		else
        	{
        		return new ListBoxModel();
        	}
            return new ListBoxModel();
        }
        
        @SuppressWarnings("unused")
        @POST
        public ListBoxModel doFillAutoGrantPermissionTcItems(@QueryParameter String environmentTypeTestcases,@QueryParameter String platformTestCases,@QueryParameter String aiqUrl,
                @QueryParameter String login,
                @QueryParameter Secret password,
                @QueryParameter String proxyHost,
                @QueryParameter String proxyPort,
                @QueryParameter String proxyUser,
                @QueryParameter Secret proxyPassword,
                @QueryParameter Boolean httpProxy) {
        	Jenkins.get().checkPermission(Jenkins.ADMINISTER);
        	if(platformTestCases.equalsIgnoreCase("Android (Beta)")){
        		platformTestCases="Android";
              }
        	if (environmentTypeTestcases.equalsIgnoreCase("Saucelabs") && platformTestCases.equalsIgnoreCase("Android"))
        	{
    			   String[] values = {"false","true"};
                               Option[] options = buildSimpleOptions(values);
                                 return new ListBoxModel(options);
        	}
    		else if ((environmentTypeTestcases.equalsIgnoreCase("Saucelabs")) && (platformTestCases.equalsIgnoreCase("macOS 11.00") || platformTestCases.equalsIgnoreCase("macOS 10.15")||platformTestCases.equalsIgnoreCase("Windows 10")))
        	{
       				//String[] values= getMobileversion(platformTestCases,aiqUrl, login, password, proxyHost, proxyPort, proxyUser, proxyPassword, httpProxy);
       				String[] values = {"NotApplicable"};
       				Option[] options = buildSimpleOptions(values);

       				return new ListBoxModel(options);
        	}
    		else if (environmentTypeTestcases.equalsIgnoreCase("Local"))
        	{

        	   if (platformTestCases.equalsIgnoreCase("Linux"))
        	   {
        		   String[] values = {"NotApplicable"};
       			//String[] values = {"Chrome (headless)","Firefox (headless)","Chrome (headful)","Firefox (headful)"};  //, "Windows"};
                Option[] options = buildSimpleOptions(values);

                return new ListBoxModel(options);
        	   }
        	}
    		else if (aiqUrl.length() > 0 && login.length() > 0 && Secret.toString(password).length() > 0)
        	{
    			 String[] values = {"NotApplicable"};
                 Option[] options = buildSimpleOptions(values);

                 return  new ListBoxModel(options);
        	}
    		else
        	{
        		return new ListBoxModel();
        	}
            return new ListBoxModel();
    	
    }

        

        private Option[] getProjectOptions(String aiqUrl, String login, Secret password, String proxyHost, String proxyPort, String proxyUser, Secret proxyPassword, Boolean httpProxy) throws ServiceException {

            Option[] ret;

            try {
                ServiceAccess svc = AutonomiqBuilder.getServiceAccess(proxyHost, proxyPort, proxyUser, proxyPassword, aiqUrl, login, password, httpProxy);


                Collection<DiscoveryResponse> dataList = svc.getProjectData();

                ret = new Option[dataList.size() + 1];

                ret[0] = new Option("-- select project --", "");

                int index = 1;
                for (DiscoveryResponse data : dataList) {

                    Long discoveryId = 0L;
                    List<AutInformation> aut = data.getAutInformations();
                    if (aut != null && aut.size() > 0) {
                        discoveryId = aut.get(0).getDiscoveryId();
                    }

                    ProjectData pd = new ProjectData(data.getProjectId(), discoveryId, data.getProjectName());

                    Option op = new Option(data.getProjectName(), AiqUtil.gson.toJson(pd));
                    ret[index] = op;

                    index++;
                }


            } catch (Exception e) {
                throw new ServiceException("Exception getting project list");
            }

            return ret;
        }
//  fetching platform  dropdown values
        private String[] getplatformType(String environmentType,String aiqUrl, String login, Secret password, String proxyHost, String proxyPort, String proxyUser, Secret proxyPassword, Boolean httpProxy) throws ServiceException {
            int i =0;
        	String[] platform1= new String[100];
        	
        	//platform1[0]="--select platform--";
        		try {
                ServiceAccess svc = AutonomiqBuilder.getServiceAccess(proxyHost, proxyPort, proxyUser, proxyPassword, aiqUrl, login, password, httpProxy);
               
            	List<ExecutionEnvironment> envInfo=svc.executionEnvironment();
            	
            
				for (ExecutionEnvironment t:envInfo) {
					

	            	 Integer a=t.getaccountId();
	            
	            	 ArrayList<Environment> d=t.getenvironments();
	            	 
	            	 for (Environment t1:d)
	            	 {
	            		
	            		 String z = t1.getenvironmentType();
	            		
	            		 	if(z.equalsIgnoreCase("Saucelabs")
	            		 			|| z.equalsIgnoreCase("saucelab_devices"))
	            		 	{
	            		 		Environment2 env2=t1.getenvironment();
	            		 		
	   	            		 ArrayList<PlatformDetail> td = env2.getplatformDetails();
	   	            		
	   	            		  	 String  sdc = env2.getsauceDataCentreName();
	   	            		     String sp=env2.getsaucePassword();
	   	            		     String su=env2.getsauceUsername();
	   	            		     for(PlatformDetail pD:td) {
	   	            		    	String platform=pD.getplatform();
	   	            		    	if (platform.equalsIgnoreCase("Android")) {
	   	            		    		platform="Android (Beta)";
	   	            		    	}
	   	            		    	platform1[i]=platform;
	   	   	            		 	i++;   
	            		 	}
	                    }
	            	 }

				}
              } catch (Exception e) {
            throw new ServiceException("Exception in getting platform");
        }

            LinkedHashSet<String> lhSetColors =
                    new LinkedHashSet<String>(Arrays.asList(platform1));
            lhSetColors.remove(null);
       	 String[] newArray = lhSetColors.toArray(new String[ lhSetColors.size()]);
            return newArray;
        }

 // fetching  browser dropdown values:

        private String[] getBrowser(String environmentType,String platformTestSuites,String aiqUrl, String login, Secret password, String proxyHost, String proxyPort, String proxyUser, Secret proxyPassword, Boolean httpProxy) throws ServiceException {
            int i =0;
        	String[] Browser= new String[100];
        	//Browser[0]="--select browser--";
            try {
                ServiceAccess svc = AutonomiqBuilder.getServiceAccess(proxyHost, proxyPort, proxyUser, proxyPassword, aiqUrl, login, password, httpProxy);

            	List<ExecutionEnvironment> envInfo=svc.executionEnvironment();
				for (ExecutionEnvironment t:envInfo) {

	            	 Integer a=t.getaccountId();
	            	 ArrayList<Environment> d=t.getenvironments();

	            	 for (Environment t1:d)
	            	 {
	            		 String z = t1.getenvironmentType();
	            		 if(z.equalsIgnoreCase("Saucelabs") || z.equalsIgnoreCase("saucelab_devices") )
	            		 {
	            		 Environment2 env2=t1.getenvironment();

	            		 ArrayList<PlatformDetail> td = env2.getplatformDetails();
	            		  	 String  sdc = env2.getsauceDataCentreName();
	            		     String sp=env2.getsaucePassword();
	            		     String su=env2.getsauceUsername();
	            		     for(PlatformDetail pD:td) {
	            		    	 String platform=pD.getplatform();
	            		    	 //System.out.println("value of platform"+platform);
	            		    	 //System.out.println("value of platform nextsteps:-"+platformTestSuites);
	            		    	 if (platform.equalsIgnoreCase(platformTestSuites))
	            		    	 {

		            		    	String browser=pD.getbrowser();
		            		    	Browser[i]=browser;
		   	            		 	i++;
	            		    	 }
	            		    	 if (platformTestSuites.length()==0)
	            		    	 {
	            		    		 if (platform.equalsIgnoreCase("Windows 10"))
	            		    				 {
	            		    			 		String browser=pD.getbrowser();
	            		    			 		Browser[i]=browser;
	 		   	            		 			i++;
	            		    				 }

	            		    	 }

	            		     }

	            		     }

	            	 }

				}

            } catch (Exception e) {
                throw new ServiceException("Exception in getting browser values");
            }
            LinkedHashSet<String> lhSetColors =
                    new LinkedHashSet<String>(Arrays.asList(Browser));
            lhSetColors.remove(null);
       	 String[] newArray = lhSetColors.toArray(new String[ lhSetColors.size() ]);
            return newArray;
        }
        
// fetch  environment type dropdown values:
        private String[] getEnvironmentType(String aiqUrl, String login, Secret password, String proxyHost, String proxyPort, String proxyUser, Secret proxyPassword, Boolean httpProxy) throws ServiceException {
            int i =0;
        	String[] EnvironmentType= new String[10];
        	
        	//EnvironmentType[0]="--select environmenttype--";
            try {
                ServiceAccess svc = AutonomiqBuilder.getServiceAccess(proxyHost, proxyPort, proxyUser, proxyPassword, aiqUrl, login, password, httpProxy);
            	List<ExecutionEnvironment> envInfo=svc.executionEnvironment();
				for (ExecutionEnvironment t:envInfo) {
	            	 Integer a=t.getaccountId();
	            	 ArrayList<Environment> d=t.getenvironments();
	            	 for (Environment t1:d)
	            	 {
	            		 String z = t1.getenvironmentType();
	            		 //System.out.println("environment values:-"+z);
	            		 //EnvironmentTypefilter[i]=z;
	            		 if(!z.equalsIgnoreCase("Zalenium") && !z.equalsIgnoreCase("saucelab_devices"))
	            		 {
	            			 //z="Remote";
	            			 EnvironmentType[i]=z;
		            		 i++;
	            		 }

	            	 }

				}


            } catch (Exception e) {
                throw new ServiceException("Exception in getting environmenttype values");
            }

            List<String> list = new ArrayList<String>();

            for(String s : EnvironmentType) {
               if(s != null && s.length() > 0) {
                  list.add(s);     
                  Collections.sort(list, Collections.reverseOrder());
                  
               }
            }
            
            EnvironmentType = list.toArray(new String[list.size()]);
            return EnvironmentType;
        }
        
        
   ///////////////////     
      
///////////////////////

 // fetch browser version dropdown values:


        private String[] getBrowserVersion(String platformTestSuites,String browserTestSuites,String aiqUrl, String login, Secret password, String proxyHost, String proxyPort, String proxyUser, Secret proxyPassword, Boolean httpProxy) throws ServiceException {
            int i =0;
        	String[] BrowserVersion= new String[100];

            try {
                ServiceAccess svc = AutonomiqBuilder.getServiceAccess(proxyHost, proxyPort, proxyUser, proxyPassword, aiqUrl, login, password, httpProxy);

            	List<ExecutionEnvironment> envInfo=svc.executionEnvironment();
				for (ExecutionEnvironment executionenvironment:envInfo) {

	            	 Integer accountid=executionenvironment.getaccountId();
	            	 ArrayList<Environment> environment=executionenvironment.getenvironments();

	            	 for (Environment e:environment)
	            	 {
	            		 String environment_type = e.getenvironmentType();
	            		 if(environment_type.equalsIgnoreCase("Saucelabs"))
	            		 {
	            		 Environment2 env2=e.getenvironment();

	            		 ArrayList<PlatformDetail> platformvalues = env2.getplatformDetails();
	            		  	 String  sdc = env2.getsauceDataCentreName();
	            		     String sp	= env2.getsaucePassword();
	            		     String su = env2.getsauceUsername();

	            		     for(PlatformDetail pD:platformvalues) {
	            		    	 String browser=pD.getbrowser();
	            		    	 String platform=pD.getplatform();
	            		    	 if (browser.equalsIgnoreCase(browserTestSuites) && platform.equalsIgnoreCase(platformTestSuites))
	            		    	 {

		            		    	 String bv=pD.getbrowserVersion();
		            		    	 BrowserVersion[i]=bv;
		            		    		i++;
	            		    	 	}
	            		    	 if (browserTestSuites.length()==0)
	            		    	 {
	            		    		 if (browser.equalsIgnoreCase("chrome") && platform.equalsIgnoreCase("Windows 10"))
	            		    		 {

	            		    		 String bv=pD.getbrowserVersion();
		            		    	 BrowserVersion[i]=bv;
		            		    		i++;
	            		    		 }
	            		    	 }

	            		     	}
	            		     }
	            	 	}

					}

            } catch (Exception e) {
                throw new ServiceException("Exception in getting browserversion values");
            }
            LinkedHashSet<String> lhSetColors =
                    new LinkedHashSet<String>(Arrays.asList(BrowserVersion));
            lhSetColors.remove(null);
       	 String[] newArray = lhSetColors.toArray(new String[ lhSetColors.size() ]);
            return newArray;
        }

// fetch sauce connect dropdown values:

        private String[] getSauceconnect(String aiqUrl, String login, Secret password, String proxyHost, String proxyPort, String proxyUser, Secret proxyPassword, Boolean httpProxy) throws ServiceException {

        	String[] sauceconnect= new String[12];

            try {
                ServiceAccess svc = AutonomiqBuilder.getServiceAccess(proxyHost, proxyPort, proxyUser, proxyPassword, aiqUrl, login, password, httpProxy);
                GetSauceConnect sauceid =svc.getsauceconnect();
                
                int length=sauceid.sauce_connect_ids().length;
                int finallength=length+1;
                if ( finallength == 1)
                {
                	sauceconnect[0]="None";
                }
                else {
                	for(int j=0;j<finallength;j++)
                	{
                		if(j==0)
                		{
                			sauceconnect[j]="None";
                		}
                		if(j!=0)
                		{
	                		System.out.println(j);
	                		sauceconnect[j]=sauceid.sauce_connect_ids()[j-1];
	                		System.out.println(sauceconnect[j]); 
	                		System.out.println("length of sauceconnect"+sauceid.sauce_connect_ids().length);
                		}
                		
                	}
                }

            } catch (Exception e) {
                throw new ServiceException("Exception in getting sauceconnect values");
            }
            LinkedHashSet<String> lhSetColors =
                    new LinkedHashSet<String>(Arrays.asList(sauceconnect));
            lhSetColors.remove(null);
       	 String[] newArray = lhSetColors.toArray(new String[ lhSetColors.size() ]);
            return newArray;
        }

// mobile version Fetch      
        
        private String[] getMobileversion(String mobileplatform,String aiqUrl, String login, Secret password, String proxyHost, String proxyPort, String proxyUser, Secret proxyPassword, Boolean httpProxy) throws ServiceException {
            //System.out.println("value in mobile version"+mobileplatform);
        	int i =0;
        	String[] Mobileplatformversion= new String[100];
        	//Browser[0]="--select browser--";
            try {
                ServiceAccess svc = AutonomiqBuilder.getServiceAccess(proxyHost, proxyPort, proxyUser, proxyPassword, aiqUrl, login, password, httpProxy);

            	List<ExecutionEnvironment> envInfo=svc.executionEnvironment();
				for (ExecutionEnvironment t:envInfo) {

	            	 Integer a=t.getaccountId();
	            	 ArrayList<Environment> d=t.getenvironments();

	            	 for (Environment t1:d)
	            	 {
	            		 String z = t1.getenvironmentType();
	            		 Environment2 env2=t1.getenvironment();

	            		 ArrayList<PlatformDetail> td = env2.getplatformDetails();
	            		  	 String  sdc = env2.getsauceDataCentreName();
	            		     String sp=env2.getsaucePassword();
	            		     String su=env2.getsauceUsername();
	            		     for(PlatformDetail pD:td) {
	            		    	 String platform=pD.getplatform();
	            		    	 if (platform.equalsIgnoreCase(mobileplatform))
	            		    	 {
	            		    		 String pv=pD.getplatformVersion();
	            		    		 if (pv.contains("0")){
	            		    	           int index = pv.indexOf(".");
	            		    	        String pv1  = pv.substring(0,index);
	            		    	        Mobileplatformversion[i]=pv1;
		            		    		i++;
	            		    	           
	            		    	       }else{
	            		    	    	   Mobileplatformversion[i]=pv;
			            		    		i++;
	            		    	       }
	            		    		 
	            		    	 }
	            		    	 
	            		    	 
	            		     }
	            	 }

				}

            } catch (Exception e) {
                throw new ServiceException("Exception in getting browser values");
            }
            LinkedHashSet<String> lhSetColors =
                    new LinkedHashSet<String>(Arrays.asList(Mobileplatformversion));
            lhSetColors.remove(null);
       	 String[] newArray = lhSetColors.toArray(new String[ lhSetColors.size() ]);
            return newArray;
        }
        
  //mobile device name
        private String[] getDevice(String mobileplatform,String mobileVersion,String aiqUrl, String login, Secret password, String proxyHost, String proxyPort, String proxyUser, Secret proxyPassword, Boolean httpProxy) throws ServiceException {
           // System.out.println("value in mobileplatform:-"+mobileplatform);

        	if(mobileplatform.equalsIgnoreCase("Android (Beta)")){
        		mobileplatform="Android";
              }

            System.out.println("value in mobileVersion"+mobileVersion);
            //System.out.println("value in mobileVersion length:-"+mobileVersion.length());
        	int i =0;
        	String[] MobileDeviceName= new String[100];
        	//Browser[0]="--select browser--";
            try {
                ServiceAccess svc = AutonomiqBuilder.getServiceAccess(proxyHost, proxyPort, proxyUser, proxyPassword, aiqUrl, login, password, httpProxy);

            	List<ExecutionEnvironment> envInfo=svc.executionEnvironment();
				for (ExecutionEnvironment t:envInfo) {

	            	 Integer a=t.getaccountId();
	            	 ArrayList<Environment> d=t.getenvironments();

	            	 for (Environment t1:d)
	            	 {
	            		 String z = t1.getenvironmentType();
	            		 Environment2 env2=t1.getenvironment();

	            		 ArrayList<PlatformDetail> td = env2.getplatformDetails();
	            		  	 String  sdc = env2.getsauceDataCentreName();
	            		     String sp=env2.getsaucePassword();
	            		     String su=env2.getsauceUsername();
	            		     for(PlatformDetail pD:td) {
	            		    	 
	            		    	 String platformversion=pD.getplatformVersion();
	            		    	 String platformversion1=pD.getdevice();
	            		    	 String platform=pD.getplatform();
	            		  
	            		    	 if (platform.equalsIgnoreCase("Android") && mobileVersion.length()==0)
	            		    	 {
	            		    		 String DN=pD.getdevice();
	            		    		 MobileDeviceName[i]=DN;
	            		    		// System.out.println(MobileDeviceName[i]);
		            		    		i++;
	            		    	 }
	            		    	 if (platformversion.contains(mobileVersion))
	            		    	 {
	            		    		 
	            		    		 String DN=pD.getdevice();
	            		    		 MobileDeviceName[i]=DN;
	            		    		// System.out.println(MobileDeviceName[i]);
		            		    		i++;
	            		    	 }       		    	 
	            		     }     
	            	 }

				}

            } catch (Exception e) {
                throw new ServiceException("Exception in getting browser values");
            }
            LinkedHashSet<String> lhSetColors =
                    new LinkedHashSet<String>(Arrays.asList(MobileDeviceName));
            lhSetColors.remove(null);
       	 String[] newArray = lhSetColors.toArray(new String[ lhSetColors.size() ]);
            return newArray;
        }
        
       
        
        private Option[] buildSimpleOptions(String[] values) {

            Option[] options = new Option[values.length];

            int index = 0;
            for (String val : values) {

                Option o = new Option(val, val);
                options[index] = o;

                index++;
            }

            return options;
        }

    }

}
