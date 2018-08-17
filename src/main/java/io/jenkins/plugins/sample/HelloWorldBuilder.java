package io.jenkins.plugins.sample;

import com.cloudbees.plugins.credentials.CredentialsMatchers;
import com.cloudbees.plugins.credentials.CredentialsProvider;
import com.cloudbees.plugins.credentials.common.StandardListBoxModel;
import com.cloudbees.plugins.credentials.common.StandardUsernamePasswordCredentials;
import com.cloudbees.plugins.credentials.domains.URIRequirementBuilder;
import hudson.Launcher;
import hudson.Extension;
import hudson.FilePath;
import hudson.model.Item;
import hudson.util.FormValidation;
import hudson.model.AbstractProject;
import hudson.model.Run;
import hudson.model.TaskListener;
import hudson.tasks.Builder;
import hudson.tasks.BuildStepDescriptor;
import hudson.util.ListBoxModel;
import jenkins.model.Jenkins;
import org.apache.commons.lang.StringUtils;
import org.kohsuke.stapler.AncestorInPath;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.QueryParameter;

import javax.servlet.ServletException;
import java.io.IOException;
import jenkins.tasks.SimpleBuildStep;
import org.jenkinsci.Symbol;
import org.kohsuke.stapler.DataBoundSetter;

public class HelloWorldBuilder extends Builder implements SimpleBuildStep {

    private final String name;
    private boolean useFrench;

    @DataBoundConstructor
    public HelloWorldBuilder(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public boolean isUseFrench() {
        return useFrench;
    }

    @DataBoundSetter
    public void setUseFrench(boolean useFrench) {
        this.useFrench = useFrench;
    }

    @Override
    public void perform(Run<?, ?> run, FilePath workspace, Launcher launcher, TaskListener listener) throws InterruptedException, IOException {
        if (useFrench) {
            listener.getLogger().println("Bonjour, " + name + "!");
        } else {
            listener.getLogger().println("Hello, " + name + "!");
        }
    }

    @Symbol("greet")
    @Extension
    public static final class DescriptorImpl extends BuildStepDescriptor<Builder> {

        public FormValidation doCheckName(@QueryParameter String value, @QueryParameter boolean useFrench)
                throws IOException, ServletException {
            if (value.length() == 0)
                return FormValidation.error(Messages.HelloWorldBuilder_DescriptorImpl_errors_missingName());
            if (value.length() < 4)
                return FormValidation.warning(Messages.HelloWorldBuilder_DescriptorImpl_warnings_tooShort());
            if (!useFrench && value.matches(".*[éáàç].*")) {
                return FormValidation.warning(Messages.HelloWorldBuilder_DescriptorImpl_warnings_reallyFrench());
            }
            return FormValidation.ok();
        }

        @Override
        public boolean isApplicable(Class<? extends AbstractProject> aClass) {
            return true;
        }

        @Override
        public String getDisplayName() {
            return Messages.HelloWorldBuilder_DescriptorImpl_DisplayName();
        }

        public ListBoxModel doFillCredentialsIdItems(
                @AncestorInPath Item item,
                @QueryParameter String credentialsId) {

            StandardListBoxModel result = new StandardListBoxModel();
            if (item == null) {
                if (!Jenkins.getInstance().hasPermission(Jenkins.ADMINISTER)) {
                    return result.includeCurrentValue(credentialsId);
                }
            } else {
                if (!item.hasPermission(Item.EXTENDED_READ)
                        && !item.hasPermission(CredentialsProvider.USE_ITEM)) {
                    return result.includeCurrentValue(credentialsId);
                }
            }
            return result
                    .includeCurrentValue(credentialsId);
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
