package io.jenkins.plugins.autonomiq.service.types;

import com.strangeberry.jmdns.tools.Browser;

import java.sql.Time;
import java.util.Date;
import java.util.List;

public class ExecuteTaskResponse {
    private Integer executionId;
    private String executionName;
    private String executionStatus;
    private Date initiatedOn;
    private Date completedOn;
    private List<TestScriptResponse> testScripts;
    private String browser;
    private String platform;
    private String initiatedBy;
    private String projectName;
    private String executionVideoUrl;

    public ExecuteTaskResponse(Integer executionId, String executionName, String executionStatus,
                               Date initiatedOn, Date completedOn, List<TestScriptResponse> testScripts,
                               String browser, String platform, String initiatedBy, String projectName,
                               String executionVideoUrl) {
        this.executionId = executionId;
        this.executionName = executionName;
        this.executionStatus = executionStatus;
        this.initiatedOn = initiatedOn;
        this.completedOn = completedOn;
        this.testScripts = testScripts;
        this.browser = browser;
        this.platform = platform;
        this.initiatedBy = initiatedBy;
        this.projectName = projectName;
        this.executionVideoUrl = executionVideoUrl;
    }
    @SuppressWarnings("unused")
    public Integer getExecutionId() {
        return executionId;
    }
    @SuppressWarnings("unused")
    public String getExecutionName() {
        return executionName;
    }
    @SuppressWarnings("unused")
    public String getExecutionStatus() {
        return executionStatus;
    }
    @SuppressWarnings("unused")
    public Date getInitiatedOn() {
        return initiatedOn;
    }
    @SuppressWarnings("unused")
    public Date getCompletedOn() {
        return completedOn;
    }
    @SuppressWarnings("unused")
    public List<TestScriptResponse> getTestScripts() {
        return testScripts;
    }
    @SuppressWarnings("unused")
    public String getBrowser() {
        return browser;
    }
    @SuppressWarnings("unused")
    public String getPlatform() {
        return platform;
    }
    @SuppressWarnings("unused")
    public String getInitiatedBy() {
        return initiatedBy;
    }
    @SuppressWarnings("unused")
    public String getProjectName() {
        return projectName;
    }
    @SuppressWarnings("unused")
    public String getExecutionVideoUrl() {
        return executionVideoUrl;
    }
}
