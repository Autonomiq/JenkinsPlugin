package io.jenkins.plugins.autonomiq.service.types;

import com.strangeberry.jmdns.tools.Browser;

import java.util.List;
import java.util.Map;


public class ExecuteTaskRequest {
    private String sessionId;
    private String testExecutionName;
    private List<Long> scripts;
    private String executionType;
    private String platform;
    private List<BrowserDetails> browserDetails;
    private Boolean isRemoteDriver;
    private String RemoteDriverUrl;
    private Map<String, String[]>extraData;

    public ExecuteTaskRequest(String sessionId, String testExecutionName, List<Long> scripts,
                              String executionType, String platform,
                              List<BrowserDetails> browserDetails, Boolean isRemoteDriver,
                              String remoteDriverUrl, Map<String, String[]> extraData) {

        this.sessionId = sessionId;
        this.testExecutionName = testExecutionName;
        this.scripts = scripts;
        this.executionType = executionType;
        this.platform = platform;
        this.browserDetails = browserDetails;
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

    public String getPlatform() {
        return platform;
    }

    public List<BrowserDetails> getBrowserDetails() {
        return browserDetails;
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

