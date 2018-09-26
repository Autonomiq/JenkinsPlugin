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

import io.jenkins.plugins.autonomiq.testplan.TestPlan;
import io.jenkins.plugins.autonomiq.testplan.TestPlanParser;
import io.jenkins.plugins.autonomiq.util.AiqUtil;
import io.jenkins.plugins.autonomiq.util.TimeStampedLogger;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.QueryParameter;

import javax.servlet.ServletException;
import java.io.*;

import java.util.List;


import jenkins.tasks.SimpleBuildStep;
import org.jenkinsci.Symbol;
import org.kohsuke.stapler.DataBoundSetter;

public class AutonomiqBuilder extends Builder implements SimpleBuildStep {

    private String aiqUrl;
    private String login;
    private String password;
    private String project; // json of ProjectData class
    private Boolean genScripts;
    private Boolean runTestCases;
    private String platform;
    private String browser;
    private String testPlan;

    private static Long pollingIntervalMs = 10000L;

    @DataBoundConstructor
    public AutonomiqBuilder(String aiqUrl, String login, String password, String project,
                            Boolean genScripts,
                            Boolean runTestCases,
                            String platform, String browser, String testPlan
    ) {

        this.aiqUrl = aiqUrl;
        this.login = login;
        this.password = password;
        this.project = project;
        this.genScripts = genScripts;
        this.runTestCases = runTestCases;
        this.platform = platform;
        this.browser = browser;
        this.testPlan = testPlan;
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
    public void setPassword(String password) {
        this.password = password;
    }

    @SuppressWarnings("unused")
    public String getPassword() {
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
    public void setPlatform(String platform) {
        this.platform = platform;
    }

    @SuppressWarnings("unused")
    public String getPlatform() {
        return platform;
    }

    @SuppressWarnings("unused")
    @DataBoundSetter
    public void setBrowser(String browser) {
        this.browser = browser;
    }

    @SuppressWarnings("unused")
    public String getBrowser() {
        return browser;
    }

    @SuppressWarnings("unused")
    public String getTestPlan() {
        return testPlan;
    }

    @SuppressWarnings("unused")
    @DataBoundSetter
    public void setTestPlan(String testPlan) {
        this.testPlan = testPlan;
    }

    @Override
    public void perform(Run<?, ?> run, FilePath workspace, Launcher launcher,
                        TaskListener listener) throws InterruptedException, IOException {
        
        TimeStampedLogger log = new TimeStampedLogger(listener.getLogger());

        boolean ok = true;

        TestPlan plan = null;

        String trimmedTestPlan = null;
        if (testPlan != null) {
            trimmedTestPlan = testPlan.trim();
        }

        if (trimmedTestPlan == null || trimmedTestPlan.length() == 0) {
            log.println();
            log.println("No test plan specified. All tests from project will run in parallel");
        } else {
            InputStream is = new ByteArrayInputStream(trimmedTestPlan.getBytes());

            TestPlanParser parser;
            try {
                log.println("Found a test plan and parsing the file");
                parser = new TestPlanParser(is, log);
                plan = parser.parseTestSequence();

                log.println("Test plan parsing completed");
                //parser.dumpTest(seq);
            } catch (PluginException e) {
                log.println("Parsing test plan file failed");
                log.println(AiqUtil.getExceptionTrace(e));
                run.setResult(Result.FAILURE);
                return;
            } finally {
                is.close();
            }
        }

        AiqUtil.gson.fromJson(project, ProjectData.class);

        log.println();
        log.printf("Logging in as user '%s' to Autonomiq service at: %s\n", login, aiqUrl);
        log.println();

        ProjectData pd;
        try {
            pd = AiqUtil.gson.fromJson(project, ProjectData.class);
        } catch (Exception e) {
            throw new IOException("Exception unpacking project data", e);
        }

        ServiceAccess svc = null;
        try {
            svc = new ServiceAccess(aiqUrl, login, password);
        } catch (Exception e) {
            ok = false;
            log.println("Authentication with Autonomiq service failed");
            log.println(AiqUtil.getExceptionTrace(e));
        }

        if (ok) {

            RunTests rt = new RunTests(svc, log, pd, pollingIntervalMs);
            ok = rt.runTests(plan, genScripts, runTestCases, platform, browser);

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
        public FormValidation doCheckAiqUrl(@QueryParameter String value, @QueryParameter String aiqUrl)
                throws IOException, ServletException {
            if (value.length() == 0)
                return FormValidation.error(Messages.AutonomiqBuilder_DescriptorImpl_errors_missingAiqUrl());
            if (!(value.startsWith("http://") || value.startsWith("https://")))
                return FormValidation.warning(Messages.AutonomiqBuilder_DescriptorImpl_errors_notUrl());

            return FormValidation.ok();
        }

        @SuppressWarnings("unused")
        public FormValidation doCheckLogin(@QueryParameter String value, @QueryParameter String login)
                throws IOException, ServletException {
            if (value.length() == 0)
                return FormValidation.error(Messages.AutonomiqBuilder_DescriptorImpl_errors_missingLogin());
            if (value.length() < 4)
                return FormValidation.warning(Messages.AutonomiqBuilder_DescriptorImpl_warnings_tooShort());

            return FormValidation.ok();
        }

        @SuppressWarnings("unused")
        public FormValidation doCheckPassword(@QueryParameter String value, @QueryParameter String password)
                throws IOException, ServletException {
            if (value.length() == 0)
                return FormValidation.error(Messages.AutonomiqBuilder_DescriptorImpl_errors_missingPassword());
            if (value.length() < 6)
                return FormValidation.warning(Messages.AutonomiqBuilder_DescriptorImpl_warnings_tooShort());

            return FormValidation.ok();
        }

        @SuppressWarnings("unused")
        public FormValidation doCheckProject(@QueryParameter String value, @QueryParameter String project)
                throws IOException, ServletException {
            if (value.length() == 0)
                return FormValidation.error(Messages.AutonomiqBuilder_DescriptorImpl_errors_missingProject());
//            if (value.length() < 6)
//                return FormValidation.warning(Messages.AutonomiqBuilder_DescriptorImpl_warnings_tooShort());

            return FormValidation.ok();
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
        public String getDefaultPassword() {
            String ret = AutonomiqConfiguration.get().getDefaultPassword();
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
                                               @QueryParameter String password) {

            // make sure other fields have been filled in
            if (aiqUrl.length() > 0 && login.length() > 0 && password.length() > 0) {

                try {

                    Option[] options = getProjectOptions(aiqUrl, login, password);

                    return new ListBoxModel(options);

                } catch (Exception e) {
                    //
                }
            }

            return new ListBoxModel();

        }

        @SuppressWarnings("unused")
        public ListBoxModel doFillPlatformItems() {

            String[] values = {"Linux", "Windows"};

            Option[] options = buildSimpleOptions(values);

            return new ListBoxModel(options);
        }

        @SuppressWarnings("unused")
        public ListBoxModel doFillBrowserItems() {

            String[] values = {"Chrome", "Firefox"};

            Option[] options = buildSimpleOptions(values);

            return new ListBoxModel(options);
        }

        private Option[] getProjectOptions(String aiqUrl, String login, String password) throws ServiceException {

            Option[] ret;

            try {
                ServiceAccess svc = new ServiceAccess(aiqUrl, login, password);

                List<DiscoveryResponse> dataList = svc.getProjectData();

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
