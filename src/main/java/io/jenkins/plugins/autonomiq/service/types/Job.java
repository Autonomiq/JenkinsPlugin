package io.jenkins.plugins.autonomiq.service.types;

import java.util.Date;

public class Job {
    private Long jobId;
    private Long accountId;
    private String name;
    private String description;
    private Date creationTime;
    private String jobType;
    private boolean isScheduled;
    private Date startTime;
    private String timeZone;
    private NullTime lastInitiatedTime;
    private Integer scheduledInterval;
    private Integer platform;
    private Integer browser;
    private String browserInterval;
    private Long totalExecutions;
    private Long completedExecutions;
    private boolean disabledStatus;

    public Job(Long jobId, Long accountId, String name, String description, Date creationTime,
               String jobType, boolean isScheduled, Date startTime, String timeZone,
               NullTime lastInitiatedTime, Integer scheduledInterval, Integer platform,
               Integer browser, String browserInterval, Long totalExecutions,
               Long completedExecutions, boolean disabledStatus) {
        this.jobId = jobId;
        this.accountId = accountId;
        this.name = name;
        this.description = description;
        this.creationTime = new Date(creationTime.getTime());
        this.jobType = jobType;
        this.isScheduled = isScheduled;
        this.startTime = new Date(startTime.getTime());
        this.timeZone = timeZone;
        this.lastInitiatedTime = lastInitiatedTime;
        this.scheduledInterval = scheduledInterval;
        this.platform = platform;
        this.browser = browser;
        this.browserInterval = browserInterval;
        this.totalExecutions = totalExecutions;
        this.completedExecutions = completedExecutions;
        this.disabledStatus = disabledStatus;
    }

    public Long getJobId() {
        return jobId;
    }

    public Long getAccountId() {
        return accountId;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public Date getCreationTime() {
        return new Date(creationTime.getTime());
    }

    public String getJobType() {
        return jobType;
    }

    public boolean isScheduled() {
        return isScheduled;
    }

    public Date getStartTime() {
        return new Date(startTime.getTime());
    }

    public String getTimeZone() {
        return timeZone;
    }

    public NullTime getLastInitiatedTime() {
        return lastInitiatedTime;
    }

    public Integer getScheduledInterval() {
        return scheduledInterval;
    }

    public Integer getPlatform() {
        return platform;
    }

    public Integer getBrowser() {
        return browser;
    }

    public String getBrowserInterval() {
        return browserInterval;
    }

    public Long getTotalExecutions() {
        return totalExecutions;
    }

    public Long getCompletedExecutions() {
        return completedExecutions;
    }

    public boolean isDisabledStatus() {
        return disabledStatus;
    }
}