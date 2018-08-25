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
import io.jenkins.plugins.autonomiq.service.types.TestCasesResponse;
import io.jenkins.plugins.autonomiq.service.types.TestScriptResponse;
import io.jenkins.plugins.autonomiq.util.AiqUtil;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.QueryParameter;

import javax.servlet.ServletException;
import java.io.*;
import java.util.ArrayList;
import java.util.List;


import jenkins.tasks.SimpleBuildStep;
import org.jenkinsci.Symbol;
import org.kohsuke.stapler.DataBoundSetter;

public class AutonomiqBuilder extends Builder implements SimpleBuildStep {

    private String aiqUrl;
    private String login;
    private String password;
    private String project; // json of ProjectData class

    private Long pollingIntervalMs = 5000L;

    @DataBoundConstructor
    public AutonomiqBuilder(String aiqUrl, String login, String password, String project) {

        this.aiqUrl = aiqUrl;
        this.login = login;
        this.password = password;
        this.project = project;
    }

    @SuppressWarnings("unused")
    public String getAiqUrl() {
        return aiqUrl;
    }

    @SuppressWarnings("unused")
    public String getLogin() {
        return login;
    }

    @SuppressWarnings("unused")
    public String getPassword() {
        return password;
    }

    @SuppressWarnings("unused")
    public String getProject() {
        return project;
    }

    @SuppressWarnings("unused")
    public String getAiqUrlValueOrDefault() {
        if (aiqUrl != null) {
            return aiqUrl;
        } else {
            return AutonomiqConfiguration.get().getDefaultAiqUrl();
        }
    }

    @SuppressWarnings("unused")
    public String getLoginValueOrDefault() {
        if (login != null) {
            return login;
        } else {
            return AutonomiqConfiguration.get().getDefaultLogin();
        }
    }

    @SuppressWarnings("unused")
    public String getPasswordValueOrDefault() {
        if (password != null) {
            return password;
        } else {
            return AutonomiqConfiguration.get().getDefaultPassword();
        }
    }

    @DataBoundSetter
    public void setAiqUrl(String aiqUrl) {
        this.aiqUrl = aiqUrl;
    }

    @DataBoundSetter
    public void setLogin(String login) {
        this.login = login;
    }

    @DataBoundSetter
    public void setPassword(String password) {
        this.password = password;
    }

    @DataBoundSetter
    public void setProject(String project) {
        this.project = project;
    }

    @Override
    public void perform(Run<?, ?> run, FilePath workspace, Launcher launcher, TaskListener listener) throws InterruptedException, IOException {

        Boolean generateScripts = true;

        boolean ok = true;
        PrintStream log = listener.getLogger();

        AiqUtil.gson.fromJson(project, ProjectData.class);

        log.printf("Logging in as user '%s' to Autonomiq service at: %s\n", login, aiqUrl);

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

        RunTests rt = new RunTests(svc, log, pd, pollingIntervalMs);

        ok = rt.runAllTestsForProject(generateScripts);

        if (ok) {
            run.setResult(Result.SUCCESS);
        } else {
            run.setResult(Result.FAILURE);
        }
    }

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

    }

}
