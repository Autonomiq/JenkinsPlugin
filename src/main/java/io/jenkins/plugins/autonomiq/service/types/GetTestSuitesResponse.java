package io.jenkins.plugins.autonomiq.service.types;

import java.util.Date;

public class GetTestSuitesResponse {
    private Long projectId;
    private String projectName;
    private Long testSuiteId;
    private String testSuiteName;
    private Date creationTime;
    private Date lastRunTime;
    private TestCasesResponse[] testCases;
    private Boolean disabledStatus;
    private Integer interval;
    private Long latestJobId;
    private String executionStatus;
    private Date scheduleStartTime;
    private Boolean isAiqExecution;

    public GetTestSuitesResponse(Long projectId, String projectName, Long testSuiteId, String testSuiteName,
                                 Date creationTime, Date lastRunTime, TestCasesResponse[] testCases,
                                 Boolean disabledStatus, Integer interval, Long latestJobId,
                                 String executionStatus, Date scheduleStartTime, Boolean isAiqExecution) {
        this.projectId = projectId;
        this.projectName = projectName;
        this.testSuiteId = testSuiteId;
        this.testSuiteName = testSuiteName;
        this.creationTime = creationTime;
        this.lastRunTime = lastRunTime;
        this.testCases = testCases;
        this.disabledStatus = disabledStatus;
        this.interval = interval;
        this.latestJobId = latestJobId;
        this.executionStatus = executionStatus;
        this.scheduleStartTime = scheduleStartTime;
        this.isAiqExecution = isAiqExecution;
    }

    public Long getProjectId() {
        return projectId;
    }

    public String getProjectName() {
        return projectName;
    }

    public Long getTestSuiteId() {
        return testSuiteId;
    }

    public String getTestSuiteName() {
        return testSuiteName;
    }

    public Date getCreationTime() {
        return creationTime;
    }

    public Date getLastRunTime() {
        return lastRunTime;
    }

    public TestCasesResponse[] getTestCases() {
        return testCases;
    }

    public Boolean getDisabledStatus() {
        return disabledStatus;
    }

    public Integer getInterval() {
        return interval;
    }

    public Long getLatestJobId() {
        return latestJobId;
    }

    public String getExecutionStatus() {
        return executionStatus;
    }

    public Date getScheduleStartTime() {
        return scheduleStartTime;
    }

    public Boolean getAiqExecution() {
        return isAiqExecution;
    }
}
