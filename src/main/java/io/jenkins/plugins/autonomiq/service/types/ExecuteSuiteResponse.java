package io.jenkins.plugins.autonomiq.service.types;

public class ExecuteSuiteResponse {
    private Long[] job_ids;

    public void setJob_ids(Long[] job_ids) {
    	this.job_ids = new Long[job_ids.length];
    	for (int i = 0 ; i < job_ids.length ; i ++) {
    		this.job_ids[i] = job_ids[i];
    	}
    }

    public Long[] getJob_ids() {
        return job_ids;
    }
}

