package io.jenkins.plugins.autonomiq.service.types;

import com.strangeberry.jmdns.tools.Browser;

import java.util.List;

public class ExecuteTaskRequest {
    private String testExecutionName;
    private List<Long> scripts;
    private String platform;
    private String browser;
    private String executionType;

    public ExecuteTaskRequest(String testExecutionName, List<Long> scripts, String platform, String browser, String executionType) {
        this.testExecutionName = testExecutionName;
        this.scripts = scripts;
        this.platform = platform;
        this.browser = browser;
        this.executionType = executionType;
    }
    @SuppressWarnings("unused")
    public String getTestExecutionName() {
        return testExecutionName;
    }
    @SuppressWarnings("unused")
    public List<Long> getScripts() {
        return scripts;
    }
    @SuppressWarnings("unused")
    public String getPlatform() {
        return platform;
    }
    @SuppressWarnings("unused")
    public String getBrowser() {
        return browser;
    }
    @SuppressWarnings("unused")
    public String getExecutionType() {
        return executionType;
    }
}

