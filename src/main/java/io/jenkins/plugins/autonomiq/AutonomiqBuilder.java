package io.jenkins.plugins.autonomiq;

import com.cloudbees.plugins.credentials.CredentialsProvider;
import com.cloudbees.plugins.credentials.common.StandardListBoxModel;

import hudson.Launcher;
import hudson.Extension;
import hudson.FilePath;
import hudson.model.*;
import hudson.util.FormValidation;
import hudson.tasks.Builder;
import hudson.tasks.BuildStepDescriptor;
import hudson.util.ListBoxModel;
import jenkins.model.Jenkins;
import org.kohsuke.stapler.AncestorInPath;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.QueryParameter;

import javax.servlet.ServletException;
import java.io.*;


import jenkins.tasks.SimpleBuildStep;
import org.jenkinsci.Symbol;
import org.kohsuke.stapler.DataBoundSetter;

public class AutonomiqBuilder extends Builder implements SimpleBuildStep {

    private String aiqUrl;
    private String login;
    private String password;
    private String project;

    private final String authenticatePath = ":8005/authenticate/basic";

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

//    public boolean isUseFrench() {
//        return useFrench;
//    }

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

        boolean ok = true;
        PrintStream log = listener.getLogger();

        log.println("Logging in to Autonomiq service: " + aiqUrl);
        log.println("as user: " + login);

        ServiceAccess svc = null;

        try {

             svc = new ServiceAccess(log, aiqUrl, login, password);

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
