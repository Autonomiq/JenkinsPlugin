package io.jenkins.plugins.autonomiq.service.types;

import java.util.Map;
import java.util.List;

public class ExecuteTestSuiteRequest {
    private String platform;
    private List<BrowserDetails> browserDetails;
    private String executionType;
    private String executionMode;
    private boolean isRemoteDriver;
    private String remoteDriverUrl;
    private Map<Long, String> map;

    public ExecuteTestSuiteRequest(String platform, List<BrowserDetails> browserDetails, String executionType,
                                   String executionMode, boolean isRemoteDriver, String remoteDriverUrl,
                                   Map<Long, String> map) {
        this.platform = platform;
        this.browserDetails = browserDetails;
        this.executionType = executionType;
        this.executionMode = executionMode;
        this.isRemoteDriver = isRemoteDriver;
        this.remoteDriverUrl = remoteDriverUrl;
        this.map = map;
    }

    public String getPlatform() {
        return platform;
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

    public List<BrowserDetails> getBrowserDetails() {
        return browserDetails;
    }
}
