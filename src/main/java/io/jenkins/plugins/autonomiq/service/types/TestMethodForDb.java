package io.jenkins.plugins.autonomiq.service.types;

import java.sql.Time;
import java.util.Date;

public class TestMethodForDb {
    private String name;
    private Integer durationMs;
    private Date startedAt;
    private Date finishedAt;
    private String status;
    private ExceptionForDb exception;

    public TestMethodForDb(String name, Integer durationMs, Date startedAt, Date finishedAt, String status, ExceptionForDb exception) {
        this.name = name;
        this.durationMs = durationMs;
        this.startedAt = startedAt;
        this.finishedAt = finishedAt;
        this.status = status;
        this.exception = exception;
    }
    @SuppressWarnings("unused")
    public String getName() {
        return name;
    }
    @SuppressWarnings("unused")
    public Integer getDurationMs() {
        return durationMs;
    }
    @SuppressWarnings("unused")
    public Date getStartedAt() {
        return startedAt;
    }
    @SuppressWarnings("unused")
    public Date getFinishedAt() {
        return finishedAt;
    }
    @SuppressWarnings("unused")
    public String getStatus() {
        return status;
    }
    @SuppressWarnings("unused")
    public ExceptionForDb getException() {
        return exception;
    }
}
