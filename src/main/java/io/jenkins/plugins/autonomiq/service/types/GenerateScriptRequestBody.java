package io.jenkins.plugins.autonomiq.service.types;

import java.util.Collection;

public class GenerateScriptRequestBody {
    private Collection<Long> testCases;
    private String sessionId;
    private String alertInfo;
    private Boolean skipRetry;
    private Boolean cacheXpathsOverride;

    public GenerateScriptRequestBody(Collection<Long> testCases, String sessionId, String alertInfo, Boolean skipRetry, Boolean cacheXpathsOverride) {
        this.testCases = testCases;
        this.sessionId = sessionId;
        this.alertInfo = alertInfo;
        this.skipRetry = skipRetry;
        this.cacheXpathsOverride = cacheXpathsOverride;
    }

    public Collection<Long> getTestCases() {
        return testCases;
    }

    public String getSessionId() {
        return sessionId;
    }

    public String getAlertInfo() {
        return alertInfo;
    }

    public Boolean getSkipRetry() {
        return skipRetry;
    }

    public Boolean getCacheXpathsOverride() {
        return cacheXpathsOverride;
    }
}
