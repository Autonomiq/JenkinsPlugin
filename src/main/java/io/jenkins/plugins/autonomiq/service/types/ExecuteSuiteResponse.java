package io.jenkins.plugins.autonomiq.service.types;

public class ExecuteSuiteResponse {
    private Long[] job_id;

    public void setJob_id(Long[] job_id) {
//    	this.job_ids = new Long[job_ids.length];
//    	for (int i = 0 ; i < job_ids.length ; i ++) {
//    		this.job_ids[i] = job_ids[i];
//    	}
    	this.job_id = job_id;
    }

    public Long[] getJob_id() {
        return job_id;
    }
}

