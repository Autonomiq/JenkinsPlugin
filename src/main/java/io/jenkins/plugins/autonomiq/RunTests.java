package io.jenkins.plugins.autonomiq;

import io.jenkins.plugins.autonomiq.service.ServiceAccess;
import io.jenkins.plugins.autonomiq.util.TimeStampedLogger;

class RunTests {


    private static String executionType = "smoke";

    private ServiceAccess svc;
    private TimeStampedLogger log;
    private ProjectData pd;
    private Long pollingIntervalMs;


    public RunTests(ServiceAccess svc,
                    TimeStampedLogger log,
                    ProjectData pd,
                    Long pollingIntervalMs) {
        this.svc = svc;
        this.log = log;
        this.pd = pd;
        this.pollingIntervalMs = pollingIntervalMs;

    }

    /**
     * @param generateScripts
     * @return returns true if all tests execute successfully
     */
    public Boolean runTests(Boolean generateScripts,
                            Boolean runTestCases,
                            Boolean runTestSuites,
                            Boolean crossBrowser,
                            Boolean mobileDevice,
                            Boolean crossBrowserTestcases,
                            Boolean mobileDeviceTestcases,
                            Boolean crossBrowsergenScripts,
                            Boolean mobileDevicegenScripts,
                            String platformTestCases,
                            String browserTestCases,
                            String platformTestSuites,
                            String browserTestSuites,
                            String genCaseList,
                            String runCaseList,
                            String runSuiteList,String executionMode,String environmentType,String browserVersion,String platformVersion,String sauceConnectProxy,String environmentTypeTestcases,String browserVersionTestcases,String sauceConnectProxyTestcases,String mobileplatformTestSuites,String mobilePlatformVersion,String deviceName,String mobileSauceConnectProxy,String mobileExecutionMode,String deviceOrientation,String enableAnimations,String autoGrantPermission,String mobileRunSuiteList,String mobileplatformTestcases,String mobilePlatformVersionTc,String deviceNameTestcases,String mobileSauceConnectProxyTc,String deviceOrientationTc,String enableAnimationsTc,String autoGrantPermissionTc,String mobileRunTestcaseList) throws PluginException, InterruptedException {
    	
  
    	crossBrowserTestcases=false;
    	mobileDeviceTestcases=false;
    	
    	if(sauceConnectProxyTestcases.equalsIgnoreCase("None"))
    	{
    		sauceConnectProxyTestcases="";
    		
    	}
    	
    	if(sauceConnectProxy.equalsIgnoreCase("None"))
    	{
    		sauceConnectProxy="";
    		
    	}
    	
    	
    	
    	if (platformTestCases.equalsIgnoreCase("macOS 11.00") || platformTestCases.equalsIgnoreCase("macOS 10.15")||platformTestCases.equalsIgnoreCase("Windows 10") ||platformTestCases.equalsIgnoreCase("Linux"))
    	{
    		crossBrowserTestcases=true;
    	}
    	if (platformTestCases.equalsIgnoreCase("Android"))
    	{
    		mobileDeviceTestcases=true;
    	}

    	if(platformTestCases.equalsIgnoreCase("Android (Beta)")){
        	  platformTestCases="Android";
          }
          System.out.println("in runtestcases platform testcases"+platformTestCases);
          if(platformTestSuites.equalsIgnoreCase("Android (Beta)")){
          	platformTestSuites="Android";
          }

        if (!(generateScripts || runTestCases || runTestSuites)) {
            log.println("Neither generate scripts nor run test cases nor run test suites selected, no work to do");
            return true;
        }

        if (generateScripts) {

            RunGenScripts gen = new RunGenScripts(svc, log, pd, pollingIntervalMs);
            boolean result = gen.genScripts(genCaseList);
            if (! result) {
                return result;
            }

        }
        if (runTestCases) {

            RunTestExecutions run = new RunTestExecutions(svc, log, pd, pollingIntervalMs);
            environmentTypeTestcases=environmentTypeTestcases.toLowerCase(); 
            String value= "cross";
            System.out.println("run tests"+browserTestCases);
            boolean result = run.runTests(platformTestCases, browserTestCases, runCaseList,environmentTypeTestcases,browserVersionTestcases,sauceConnectProxyTestcases,mobileplatformTestcases,mobilePlatformVersionTc,deviceNameTestcases,mobileSauceConnectProxyTc,deviceOrientationTc,enableAnimationsTc,autoGrantPermissionTc,mobileDeviceTestcases,crossBrowserTestcases,value);
            if (!result) {
                return result;
            }
        }
        

        if (runTestSuites) {

            RunSuiteExecutions run = new RunSuiteExecutions(svc, log, pd, pollingIntervalMs);
            environmentType=environmentType.toLowerCase();
           
            String value= "cross";
            boolean result = run.runSuites(platformTestSuites, browserTestSuites, runSuiteList,executionMode,environmentType,browserVersion,platformVersion,sauceConnectProxy,mobileplatformTestSuites,mobilePlatformVersion,deviceName,mobileSauceConnectProxy,mobileExecutionMode,deviceOrientation,enableAnimations,autoGrantPermission,mobileDevice,crossBrowser,value);
            
            if (!result) {
                return result;
            }
        }

        return true;
    }

}