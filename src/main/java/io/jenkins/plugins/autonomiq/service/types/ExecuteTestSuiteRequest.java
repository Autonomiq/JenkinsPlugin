package io.jenkins.plugins.autonomiq.service.types;

import java.util.Map;

public class ExecuteTestSuiteRequest {
    private String platform;
    private String browser;
    private String browserVersion;
    private String executionType;
    private String executionMode;
    private boolean isRemoteDriver;
    private String remoteDriverUrl;
    private Map<Long, String> map;

    public ExecuteTestSuiteRequest(String platform, String browser, String browserVersion, String executionType,
                                   String executionMode, boolean isRemoteDriver, String remoteDriverUrl,
                                   Map<Long, String> map) {
        this.platform = platform;
        this.browser = browser;
        this.browserVersion = browserVersion;
        this.executionType = executionType;
        this.executionMode = executionMode;
        this.isRemoteDriver = isRemoteDriver;
        this.remoteDriverUrl = remoteDriverUrl;
        this.map = map;
    }

    public String getPlatform() {
        return platform;
    }

    public String getBrowser() {
        return browser;
    }

    public String getBrowserVersion() {
        return browserVersion;
    }

    public String getExecutionType() {
        return executionType;
    }

    public String getExecutionMode() {
        return executionMode;
    }

    public boolean isRemoteDriver() {
        return isRemoteDriver;
    }

    public String getRemoteDriverUrl() {
        return remoteDriverUrl;
    }

    public Map<Long, String> getMap() {
        return map;
    }
}
