package io.jenkins.plugins.autonomiq.service.types;

import java.util.Arrays;

public class ExecuteSuiteResponse {
    private Long[] job_id;

    public void setJob_id(Long[] job_id) {
    	this.job_id = Arrays.copyOf(job_id,job_id.length);
    }

    public Long[] getJob_id() {
        return Arrays.copyOf(job_id, job_id.length);
    }
}