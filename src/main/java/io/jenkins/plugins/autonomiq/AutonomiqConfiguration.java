package io.jenkins.plugins.autonomiq;

import hudson.Extension;
import hudson.util.FormValidation;
import io.jenkins.plugins.autonomiq.service.ServiceAccess;
import io.jenkins.plugins.autonomiq.util.AiqUtil;
import jenkins.model.GlobalConfiguration;
import jenkins.model.Jenkins;
import net.sf.json.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.kohsuke.stapler.QueryParameter;
import org.kohsuke.stapler.StaplerRequest;
import hudson.util.Secret;

/**
 * Example of Jenkins global configuration.
 */
//@Extension
public class AutonomiqConfiguration extends GlobalConfiguration {

    /** @return the singleton instance */
    public static AutonomiqConfiguration get() {
        return GlobalConfiguration.all().get(AutonomiqConfiguration.class);
    }

    private static final String DEFAULT_AIQ_URL = "defaultAiqUrl";
    private static final String DEFAULT_LOGIN = "defaultLogin";
    private static final String DEFAULT_PASSWORD = "defaultPassword";

    private String defaultAiqUrl;
    private String defaultLogin;
    private Secret defaultPassword;


    public AutonomiqConfiguration() {
        // When Jenkins is restarted, load any saved configuration from disk.
        load();
    }

    public String getDisplayName() {
        return "Set default credentials for Autonomiq service";
    }

    public String getDefaultAiqUrl() {
        return defaultAiqUrl;
    }
    public String getDefaultLogin() {
        return defaultLogin;
    }
    public Secret getDefaultPassword() {
        return defaultPassword;
    }

    // On save.
    @Override
    public boolean configure(StaplerRequest req, JSONObject formData) throws FormException {
        defaultAiqUrl = formData.getString(DEFAULT_AIQ_URL);
        defaultLogin = formData.getString(DEFAULT_LOGIN);
        defaultPassword = Secret.fromString(DEFAULT_PASSWORD);

        save();

        return super.configure(req, formData);
    }

    public FormValidation doCheckDefaultAiqUrl(@QueryParameter String value) {
        if (StringUtils.isEmpty(value)) {
            return FormValidation.warning("Please specify default URL for Autonomiq service.");
        }
        if (! (value.startsWith("http://") || value.startsWith("https://"))) {
            return FormValidation.warning("Invalid URL.");
        }
        return FormValidation.ok();
    }
    public FormValidation doCheckDefaultLogin(@QueryParameter String value) {
        if (StringUtils.isEmpty(value)) {
            return FormValidation.warning("Please specify a default username.");
        }
        return FormValidation.ok();
    }
    public FormValidation doCheckDefaultPassword(@QueryParameter String value) {
        if (StringUtils.isEmpty(value)) {
            return FormValidation.warning("Please specify a default password.");
        }
        return FormValidation.ok();
    }

    // Form validation
    @SuppressWarnings({"ThrowableResultOfMethodCallIgnored", "unused"})
    public FormValidation doTestConnection(@QueryParameter(DEFAULT_AIQ_URL) final String defaultAiqUrl,
                                           @QueryParameter(DEFAULT_LOGIN) final String defaultLogin,
                                           @QueryParameter(DEFAULT_PASSWORD) final String defaultPassword) {
    	Jenkins.get().checkPermission(Jenkins.ADMINISTER);

        if (AiqUtil.isNullOrEmpty(defaultAiqUrl)) {
            return FormValidation.error("Default Autonomiq URL is empty!");
        }
        if (AiqUtil.isNullOrEmpty(defaultLogin)) {
            return FormValidation.error("Default username is empty!");
        }
        if (AiqUtil.isNullOrEmpty(defaultPassword)) {
            return FormValidation.error("Default password is empty!");
        }

        try {

            new ServiceAccess(defaultAiqUrl, defaultLogin, defaultPassword);

        } catch (Exception e) {
            return FormValidation.error("Unable to authenticate with Autonomiq service");
        }

        return FormValidation.ok("Successfully authenticated to Autonomiq service");

    }

}
