package io.jenkins.plugins.autonomiq.service.types;

import io.jenkins.plugins.autonomiq.service.ServiceAccess;

import java.util.Date;
import java.util.List;

public class DiscoveryResponse {
    private Long projectId;
    private String projectName;
    private List<AutInformation> autInformations;
    private Date lastActivityDate;
    private Date creationTime;
    private String lastUsedBy;
    private Integer noOfEnvironments;
    private Integer totalTestsCount;
    private Integer totalTestsFailedCount;
    private Integer totalTestsPassedCount;
    private Integer totalTestsSkippedCount;
    private Integer noOfUsers;

    public DiscoveryResponse(Long projectId, String projectName,
                             List<AutInformation> autInformations, Date lastActivityDate,
                             Date creationTime, String lastUsedBy, Integer noOfEnvironments,
                             Integer totalTestsCount, Integer totalTestsFailedCount,
                             Integer totalTestsPassedCount, Integer totalTestsSkippedCount,
                             Integer noOfUsers) {
        this.projectId = projectId;
        this.projectName = projectName;
        this.autInformations = autInformations;
        this.lastActivityDate = new Date(lastActivityDate.getTime());
        this.creationTime = new Date(creationTime.getTime());
        this.lastUsedBy = lastUsedBy;
        this.noOfEnvironments = noOfEnvironments;
        this.totalTestsCount = totalTestsCount;
        this.totalTestsFailedCount = totalTestsFailedCount;
        this.totalTestsPassedCount = totalTestsPassedCount;
        this.totalTestsSkippedCount = totalTestsSkippedCount;
        this.noOfUsers = noOfUsers;
    }
    @SuppressWarnings("unused")
    public Long getProjectId() {
        return projectId;
    }
    @SuppressWarnings("unused")
    public String getProjectName() {
        return projectName;
    }
    @SuppressWarnings("unused")
    public List<AutInformation> getAutInformations() {
        return autInformations;
    }
    @SuppressWarnings("unused")
    public Date getLastActivityDate() {
        return new Date(lastActivityDate.getTime());
    }
    @SuppressWarnings("unused")
    public Date getCreationTime() {
        return new Date(creationTime.getTime());
    }
    @SuppressWarnings("unused")
    public String getLastUsedBy() {
        return lastUsedBy;
    }
    @SuppressWarnings("unused")
    public Integer getNoOfEnvironments() {
        return noOfEnvironments;
    }
    @SuppressWarnings("unused")
    public Integer getTotalTestsCount() {
        return totalTestsCount;
    }
    @SuppressWarnings("unused")
    public Integer getTotalTestsFailedCount() {
        return totalTestsFailedCount;
    }
    @SuppressWarnings("unused")
    public Integer getTotalTestsPassedCount() {
        return totalTestsPassedCount;
    }
    @SuppressWarnings("unused")
    public Integer getTotalTestsSkippedCount() {
        return totalTestsSkippedCount;
    }
    @SuppressWarnings("unused")
    public Integer getNoOfUsers() {
        return noOfUsers;
    }
}

