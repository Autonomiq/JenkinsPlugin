package io.jenkins.plugins.autonomiq.service.types;

import java.util.Date;

public class ExecuteTaskResponse {
    private Integer executionId;
    private String executionName;
    private String executionStatus;
    private Date initiatedOn;
    private String projectName;
    private Integer projectId;
    private String executionVideoUrl;
    private String reportUrl;
    private Long testcaseIds;

    public ExecuteTaskResponse(Integer executionId, String executionName, String executionStatus,
                               Date initiatedOn, String projectName, Integer projectId,
                               String executionVideoUrl, String reportUrl, Long testcaseIds) {
        this.executionId = executionId;
        this.executionName = executionName;
        this.executionStatus = executionStatus;
        this.initiatedOn = new Date(initiatedOn.getTime());
        this.projectName = projectName;
        this.projectId = projectId;
        this.executionVideoUrl = executionVideoUrl;
        this.reportUrl = reportUrl;
        this.testcaseIds = testcaseIds;
    }

    public Integer getExecutionId() {
        return executionId;
    }

    public String getExecutionName() {
        return executionName;
    }

    public String getExecutionStatus() {
        return executionStatus;
    }

    public Date getInitiatedOn() {
        return new Date(initiatedOn.getTime());
    }

    public String getProjectName() {
        return projectName;
    }

    public Integer getProjectId() {
        return projectId;
    }

    public String getExecutionVideoUrl() {
        return executionVideoUrl;
    }

    public String getReportUrl() {
        return reportUrl;
    }

    public Long getTestcaseIds() {
        return testcaseIds;
    }
}