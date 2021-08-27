package io.jenkins.plugins.autonomiq.service.types;

import java.util.Map;
import java.util.List;

public class ExecuteTestSuiteRequest {
    private List<PlatformBrowserDetails> platformBrowserDetails;
    private String executionType;
    private String executionMode;
    private boolean isRemoteDriver;
    private String remoteDriverUrl;
    private Map<Long, String> map;

    public ExecuteTestSuiteRequest(List<PlatformBrowserDetails> browserDetails, String executionType,
                                   String executionMode, boolean isRemoteDriver, String remoteDriverUrl,
                                   Map<Long, String> map) {
        this.platformBrowserDetails = browserDetails;
        this.executionType = executionType;
        this.executionMode = executionMode;
        this.isRemoteDriver = isRemoteDriver;
        this.remoteDriverUrl = remoteDriverUrl;
        this.map = map;
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

    public List<PlatformBrowserDetails> getBrowserDetails() {
        return platformBrowserDetails;
    }
}
