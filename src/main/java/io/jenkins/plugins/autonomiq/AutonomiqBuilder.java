package io.jenkins.plugins.autonomiq;

import hudson.Launcher;
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

import io.jenkins.plugins.autonomiq.service.types.GetTestSuitesResponse;
import io.jenkins.plugins.autonomiq.service.types.TestCasesResponse;
import io.jenkins.plugins.autonomiq.testplan.TestPlan;
import io.jenkins.plugins.autonomiq.testplan.TestPlanParser;
import io.jenkins.plugins.autonomiq.util.AiqUtil;
import io.jenkins.plugins.autonomiq.util.TimeStampedLogger;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.QueryParameter;
import org.kohsuke.stapler.verb.POST;

import javax.servlet.ServletException;
import java.io.*;

import java.util.Collection;
import java.util.HashSet;
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
    private String platformTestCases;
    private String browserTestCases;
    private String platformTestSuites;
    private String browserTestSuites;
    private String genCaseList;
    private String runCaseList;
    private String runSuiteList;
    private String proxyHost;
    private String proxyPort;
    private String proxyUser;
    private Secret proxyPassword;
    private Boolean httpProxy;
    private String executionMode;
    private String environmentType;
    private String platformVersion;
    private String browserVersion;
    private String sauceConnectProxy;
    private static Long pollingIntervalMs = 10000L;

    @DataBoundConstructor
    public AutonomiqBuilder(String aiqUrl, String login, Secret password, String project,
                            Boolean genScripts,
                            Boolean runTestCases,
                            Boolean runTestSuites,
                            String platformTestCases, String browserTestCases,
                            String platformTestSuites, String browserTestSuites,
                            String genCaseList,
                            String runCaseList,
                            String runSuiteList,
                            String proxyHost,
                            String proxyPort,
                            String proxyUser,
                            Secret proxyPassword,
                            Boolean httpProxy,
                            String executionMode,
                            String environmentType,
                            String platformVersion,
                            String browserVersion,
                            String sauceConnectProxy
    ) {

        this.aiqUrl = aiqUrl;
        this.login = login;
        this.password = password;
        this.project = project;
        this.genScripts = genScripts;
        this.runTestCases = runTestCases;
        this.runTestSuites = runTestSuites;
        this.platformTestCases = platformTestCases;
        this.browserTestCases = browserTestCases;
        this.platformTestSuites = platformTestSuites;
        this.browserTestSuites = browserTestSuites;
        this.genCaseList = genCaseList;
        this.runCaseList = runCaseList;
        this.runSuiteList = runSuiteList;
        this.proxyHost = proxyHost;
        this.proxyPort = proxyPort;
        this.proxyUser = proxyUser;
        this.proxyPassword = proxyPassword;
        this.httpProxy = httpProxy;
        this.executionMode=executionMode;
        this.platformVersion=platformVersion;
        this.browserVersion=browserVersion;
        this.environmentType=environmentType;
        this.sauceConnectProxy=sauceConnectProxy;
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
        return runTestCases;
    }

    @SuppressWarnings("unused")
    @DataBoundSetter
    public void setRunTestSuites(Boolean runTestSuites) {
        this.runTestSuites = runTestSuites;
    }

    @SuppressWarnings("unused")
    public Boolean getRunTestSuites() {
        return runTestSuites;
    }


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
        return environmentType;
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
    public void setSauceConnectProxyType(String sauceConnectProxy) {
        this.sauceConnectProxy = sauceConnectProxy;
    }

    @SuppressWarnings("unused")
    public String getSauceConnectProxyType() {
        return sauceConnectProxy;
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
                ok = rt.runTests(genScripts, runTestCases, runTestSuites,
                        platformTestCases, browserTestCases,
                        platformTestSuites, browserTestSuites,
                        genCaseList, runCaseList, runSuiteList,executionMode,environmentType,browserVersion,platformVersion,sauceConnectProxy);
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
        public ListBoxModel doFillProjectItems(@QueryParameter String aiqUrl,
                                               @QueryParameter String login,
                                               @QueryParameter Secret password,
                                               @QueryParameter String proxyHost,
                                               @QueryParameter String proxyPort,
                                               @QueryParameter String proxyUser,
                                               @QueryParameter Secret proxyPassword,
                                               @QueryParameter Boolean httpProxy) {

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
        public ListBoxModel doFillPlatformTestCasesItems() {

            String[] values = {"Linux"};  //, "Windows"};

            Option[] options = buildSimpleOptions(values);

            return new ListBoxModel(options);
        }
        @SuppressWarnings("unused")
        public ListBoxModel doFillPlatformTestSuitesItems() {

            String[] values = {"Linux","Windows"};  //, "Windows"};

            Option[] options = buildSimpleOptions(values);

            return new ListBoxModel(options);
        }


        @SuppressWarnings("unused")
        public ListBoxModel doFillBrowserTestCasesItems() {

            String[] values = {"Chrome", "Firefox"};

            Option[] options = buildSimpleOptions(values);

            return new ListBoxModel(options);
        }
        
        @SuppressWarnings("unused")
        public ListBoxModel doFillBrowserTestSuitesItems() {

            String[] values = {"Chrome", "Firefox","safari","MicrosoftEdge"};

            Option[] options = buildSimpleOptions(values);

            return new ListBoxModel(options);
        }
        
        @SuppressWarnings("unused")
        public ListBoxModel doFillExecutionModeItems() {

            String[] values = {"serial", "parallel"};

            Option[] options = buildSimpleOptions(values);

            return new ListBoxModel(options);
        }
        
        @SuppressWarnings("unused")
        public ListBoxModel doFillEnvironmentTypeItems() {

            String[] values = {"local", "zalenium","saucelabs"};

            Option[] options = buildSimpleOptions(values);

            return new ListBoxModel(options);
        }
      //String platformVersion,
        //String browserVersion
        @SuppressWarnings("unused")
        public ListBoxModel doFillPlatformVersionItems() {

            String[] values = {"10",""};

            Option[] options = buildSimpleOptions(values);

            return new ListBoxModel(options);
        }
        
        @SuppressWarnings("unused")
        public ListBoxModel doFillBrowserVersionItems() {

            String[] values = {"0.0","1.0","89","90","91","92"};

            Option[] options = buildSimpleOptions(values);

            return new ListBoxModel(options);
        }
        
        @SuppressWarnings("unused")
        public ListBoxModel doFillSauceConnectProxyItems() {

            String[] values = {"Shared_Tunnel","b1f5d964b5524f169ecef8bd7ebafac6","amitshared"};

            Option[] options = buildSimpleOptions(values);

            return new ListBoxModel(options);
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
