package io.jenkins.plugins.autonomiq;

import com.google.common.collect.Lists;
import hudson.Launcher;
import hudson.Extension;
import hudson.FilePath;
import hudson.model.*;
import hudson.util.ComboBoxModel;
import hudson.util.FormValidation;
import hudson.tasks.Builder;
import hudson.tasks.BuildStepDescriptor;

import hudson.util.ListBoxModel;
import hudson.util.ListBoxModel.Option;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.QueryParameter;

import javax.servlet.ServletException;
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


import jenkins.tasks.SimpleBuildStep;
import org.jenkinsci.Symbol;
import org.kohsuke.stapler.DataBoundSetter;

public class AutonomiqBuilder extends Builder implements SimpleBuildStep {

    private String aiqUrl;
    private String login;
    private String password;
    private String project; // json of ProjectData

    @DataBoundConstructor
    public AutonomiqBuilder(String aiqUrl, String login, String password, String project) {
        this.aiqUrl = aiqUrl;
        this.login = login;
        this.password = password;
        this.project = project;
    }

    public String getAiqUrl() { return aiqUrl; }
    public String getLogin() {
        return login;
    }
    public String getPassword() {
        return password;
    }
    public String getProject() {
        return project;
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
    public void setProjectId(String project) {
        this.project = project;
    }

    @Override
    public void perform(Run<?, ?> run, FilePath workspace, Launcher launcher, TaskListener listener) throws InterruptedException, IOException {

        boolean ok = true;
        PrintStream log = listener.getLogger();

        log.printf("Logging in as user '%s' to Autonomiq service at: %s\n", login, aiqUrl);

        ServiceAccess svc = null;

        try {

             svc = new ServiceAccess(log, aiqUrl, login, password);

             ProjectData pd = AiqUtil.gson.fromJson(project, ProjectData.class);

             log.printf("Running tests from project '%s'\n", pd.projectName);

        } catch (Exception e) {
            ok = false;
            log.println("Authentication failed");
            log.println(AiqUtil.getExceptionTrace(e));
        }

        if (ok) {
            run.setResult(Result.SUCCESS);
        } else {
            run.setResult(Result.FAILURE);
        }

    }

    @Symbol("greet")
    @Extension
    public static final class DescriptorImpl extends BuildStepDescriptor<Builder> {

        public FormValidation doCheckAiqUrl(@QueryParameter String value, @QueryParameter String aiqUrl)
                throws IOException, ServletException {
            if (value.length() == 0)
                return FormValidation.error(Messages.AutonomiqBuilder_DescriptorImpl_errors_missingAiqUrl());
            if (! (value.startsWith("http://") || value.startsWith("https://")))
                return FormValidation.warning(Messages.AutonomiqBuilder_DescriptorImpl_errors_notUrl());

            return FormValidation.ok();
        }
        public FormValidation doCheckLogin(@QueryParameter String value, @QueryParameter String login)
                throws IOException, ServletException {
            if (value.length() == 0)
                return FormValidation.error(Messages.AutonomiqBuilder_DescriptorImpl_errors_missingLogin());
            if (value.length() < 4)
                return FormValidation.warning(Messages.AutonomiqBuilder_DescriptorImpl_warnings_tooShort());

            return FormValidation.ok();
        }
        public FormValidation doCheckPassword(@QueryParameter String value, @QueryParameter String password)
                throws IOException, ServletException {
            if (value.length() == 0)
                return FormValidation.error(Messages.AutonomiqBuilder_DescriptorImpl_errors_missingPassword());
            if (value.length() < 6)
                return FormValidation.warning(Messages.AutonomiqBuilder_DescriptorImpl_warnings_tooShort());

            return FormValidation.ok();
        }
        public FormValidation doCheckProject(@QueryParameter String value, @QueryParameter String project)
                throws IOException, ServletException {
            if (value.length() == 0)
                return FormValidation.error(Messages.AutonomiqBuilder_DescriptorImpl_errors_missingProject());
//            if (value.length() < 6)
//                return FormValidation.warning(Messages.AutonomiqBuilder_DescriptorImpl_warnings_tooShort());

            return FormValidation.ok();
        }

        @Override
        public boolean isApplicable(Class<? extends AbstractProject> aClass) {
            return true;
        }

        @Override
        public String getDisplayName() {
            return Messages.AutonomiqBuilder_DescriptorImpl_DisplayName();
        }

        public ListBoxModel doFillProjectItems(@QueryParameter String aiqUrl,
                                               @QueryParameter String login,
                                               @QueryParameter String password) {

            // make sure other fields have been filled in
            if (aiqUrl.length() > 0 && login.length() > 0 && password.length() > 0) {

                try {

                    Option[] options = getProjectOptions(aiqUrl, login, password);
                    //List<String> projects = getProjectList(aiqUrl, login, password);

//                    String[] vals = { "Yellow Submarine","Only a Northern Song","All You Need Is Love" };
//
//                    Option[] options = new Option[3];
//                    int index = 0;
//                    for (String val : vals) {
//                        options[index] = new Option(val, val);
//                        index++;
//                    }

                    return new ListBoxModel(options);

                    //return new ListBoxModel((String[]) projects.toArray());
                } catch (Exception e) {
                    // logger not available?
                }
            }

            return new ListBoxModel();

        }

        public Option[] getProjectOptions(String aiqUrl, String login, String password) throws ServiceException {


            Option[] ret;

            try {
                ServiceAccess svc = new ServiceAccess(null, aiqUrl, login, password);

                List<ServiceAccess.DiscoveryResponse> dataList = svc.getProjectData();

                ret = new Option[dataList.size() + 1];

                ret[0] = new Option("-- select project --", "");

                int index = 1;
                for (ServiceAccess.DiscoveryResponse data : dataList) {

                    ProjectData pd = new ProjectData(data.getProjectId(), data.getProjectName());

                    Option op = new Option(data.getProjectName(), AiqUtil.gson.toJson(pd));
                    ret[index] = op;

                    index++;
                }


            } catch (Exception e) {
                throw new ServiceException("Exception getting project list");
            }

            return ret;
        }


//        public ListBoxModel doFillCredentialsIdItems(
//                @AncestorInPath Item item,
//                @QueryParameter String credentialsId) {
//
//            StandardListBoxModel result = new StandardListBoxModel();
//            if (item == null) {
//                if (!Jenkins.getInstance().hasPermission(Jenkins.ADMINISTER)) {
//                    return result.includeCurrentValue(credentialsId);
//                }
//            } else {
//                if (!item.hasPermission(Item.EXTENDED_READ)
//                        && !item.hasPermission(CredentialsProvider.USE_ITEM)) {
//                    return result.includeCurrentValue(credentialsId);
//                }
//            }
//            return result
//                    .includeCurrentValue(credentialsId);
//        }

    }

    private static class ProjectData {
        private Long projectId;
        private String projectName;

        public ProjectData(Long projectId, String projectName) {
            this.projectId = projectId;
            this.projectName = projectName;
        }

        public Long getProjectId() {
            return projectId;
        }

        public String getProjectName() {
            return projectName;
        }
    }

//    public void xx() {
//        CredentialsProvider.listCredentials(
//                StandardUsernamePasswordCredentials.class,
//                job,
//                Jenkins.getAuthentication(),
//                URIRequirementBuilder.fromUri(scmUrl),
//                null
//        )
//    }

//    public FormValidation doCheckCredentialsId(
//            @AncestorInPath Item item,
//    @QueryParameter String value) {
//
//        if (item == null) {
//            if (!Jenkins.getInstance().hasPermission(Jenkins.ADMINISTER)) {
//                return FormValidation.ok();
//            }
//        } else {
//            if (!item.hasPermission(Item.EXTENDED_READ)
//                    && !item.hasPermission(CredentialsProvider.USE_ITEM)) {
//                return FormValidation.ok();
//            }
//        }
////        if (StringUtils.isBlank(value)) { (4)
////            return FormValidation.; (4)
////        }
////        if (value.startWith("${") && value.endsWith("}")) { (5)
////            return FormValidation.warning("Cannot validate expression based credentials");
////        }
//
//        CredentialsMatchers.firstOrNull(CredentialsProvider.lookupCredentials(…​), withId(value))
//
//        if (CredentialsProvider.listCredentials(
//    ...,
//        CredentialsMatchers.withId(value)
//  ).isEmpty()) {
//            return FormValidation.error("Cannot find currently selected credentials");
//        }
//        return FormValidation.ok();
//    }

}
