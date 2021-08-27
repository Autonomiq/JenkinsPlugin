package io.jenkins.plugins.autonomiq.service.types;

import java.util.List;
import java.util.Map;


public class ExecuteTaskRequest {
    private String sessionId;
    private String testExecutionName;
    private List<Long> scripts;
    private String executionType;
    private List<PlatformBrowserDetails> platformBrowserDetails;
    private Boolean isRemoteDriver;
    private String RemoteDriverUrl;
    private Map<String, String[]>extraData;

    public ExecuteTaskRequest(String sessionId, String testExecutionName, List<Long> scripts,
                              String executionType, 
                              List<PlatformBrowserDetails> platformBrowserDetails, Boolean isRemoteDriver,
                              String remoteDriverUrl, Map<String, String[]> extraData) {

        this.sessionId = sessionId;
        this.testExecutionName = testExecutionName;
        this.scripts = scripts;
        this.executionType = executionType;
        this.platformBrowserDetails = platformBrowserDetails;
        this.isRemoteDriver = isRemoteDriver;
        RemoteDriverUrl = remoteDriverUrl;
        this.extraData = extraData;
    }

    public String getTestExecutionName() {
        return testExecutionName;
    }

    public List<Long> getScripts() {
        return scripts;
    }

    public String getExecutionType() {
        return executionType;
    }

    public List<PlatformBrowserDetails> getBrowserDetails() {
        return platformBrowserDetails;
    }

    public String getSessionId() {
        return sessionId;
    }

    public Boolean getRemoteDriver() {
        return isRemoteDriver;
    }

    public String getRemoteDriverUrl() {
        return RemoteDriverUrl;
    }

    public Map<String, String[]> getExtraData() {
        return extraData;
    }
}

