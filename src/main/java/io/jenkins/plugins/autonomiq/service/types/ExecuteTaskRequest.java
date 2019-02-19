package io.jenkins.plugins.autonomiq.service.types;

import com.strangeberry.jmdns.tools.Browser;

import java.util.List;


public class ExecuteTaskRequest {
    private String testExecutionName;
    private List<Long> scripts;
    private String executionType;
    private String platform;
    private List<BrowserDetails> browserDetails;

    public ExecuteTaskRequest(String testExecutionName, List<Long> scripts, String executionType,
                              String platform, List<BrowserDetails> browserDetails) {
        this.testExecutionName = testExecutionName;
        this.scripts = scripts;
        this.executionType = executionType;
        this.platform = platform;
        this.browserDetails = browserDetails;
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
}

