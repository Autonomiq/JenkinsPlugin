package io.jenkins.plugins.autonomiq.service.types;

import java.util.Date;

public class TestScriptResponse {
    private Long testScriptId;
    private Long projectId;
    private Long testCaseId;
    private Long discoveryId;
    private Long userId;
    private String testScriptName;
    private String testScriptDownloadLink;
    private String testScriptGenerationStatus;
    private String errorMessage;
    private String stepsErrorMessages;
    private String initiatedBy;
    private Date initiatedTime;
    private Date generationTime;
    private Date lastExecutedTime;
    private Long executionId;

    public TestScriptResponse(Long testScriptId, Long projectId, Long testCaseId, Long discoveryId,
                              Long userId, String testScriptName, String testScriptDownloadLink,
                              String testScriptGenerationStatus, String errorMessage,
                              String stepsErrorMessages, String initiatedBy, Date initiatedTime,
                              Date generationTime, Date lastExecutedTime, Long executionId) {
        this.testScriptId = testScriptId;
        this.projectId = projectId;
        this.testCaseId = testCaseId;
        this.discoveryId = discoveryId;
        this.userId = userId;
        this.testScriptName = testScriptName;
        this.testScriptDownloadLink = testScriptDownloadLink;
        this.testScriptGenerationStatus = testScriptGenerationStatus;
        this.errorMessage = errorMessage;
        this.stepsErrorMessages = stepsErrorMessages;
        this.initiatedBy = initiatedBy;
        this.initiatedTime = initiatedTime;
        this.generationTime = generationTime;
        this.lastExecutedTime = lastExecutedTime;
        this.executionId = executionId;
    }
    @SuppressWarnings("unused")
    public Long getTestScriptId() {
        return testScriptId;
    }
    @SuppressWarnings("unused")
    public Long getProjectId() {
        return projectId;
    }
    @SuppressWarnings("unused")
    public Long getTestCaseId() {
        return testCaseId;
    }
    @SuppressWarnings("unused")
    public Long getDiscoveryId() {
        return discoveryId;
    }
    @SuppressWarnings("unused")
    public Long getUserId() {
        return userId;
    }
    @SuppressWarnings("unused")
    public String getTestScriptName() {
        return testScriptName;
    }
    @SuppressWarnings("unused")
    public String getTestScriptDownloadLink() {
        return testScriptDownloadLink;
    }
    @SuppressWarnings("unused")
    public String getTestScriptGenerationStatus() {
        return testScriptGenerationStatus;
    }
    @SuppressWarnings("unused")
    public String getErrorMessage() {
        return errorMessage;
    }
    @SuppressWarnings("unused")
    public String getStepsErrorMessages() {
        return stepsErrorMessages;
    }
    @SuppressWarnings("unused")
    public String getInitiatedBy() {
        return initiatedBy;
    }
    @SuppressWarnings("unused")
    public Date getInitiatedTime() {
        return initiatedTime;
    }
    @SuppressWarnings("unused")
    public Date getGenerationTime() {
        return generationTime;
    }
    @SuppressWarnings("unused")
    public Date getLastExecutedTime() {
        return lastExecutedTime;
    }
    @SuppressWarnings("unused")
    public Long getExecutionId() {
        return executionId;
    }
}

