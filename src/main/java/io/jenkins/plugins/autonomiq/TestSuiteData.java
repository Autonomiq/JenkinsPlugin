package io.jenkins.plugins.autonomiq;

import io.jenkins.plugins.autonomiq.testplan.TestItem;

public class TestSuiteData {
    private Long testSuiteId;
    private Long jobId;
    private TestItem testItem;

    public TestSuiteData() {

    }

    public Long getTestSuiteId() {
        return testSuiteId;
    }

    public void setTestSuiteId(Long testSuiteId) {
        this.testSuiteId = testSuiteId;
    }

    public Long getJobId() {
        return jobId;
    }

    public void setJobId(Long jobId) {
        this.jobId = jobId;
    }

    public TestItem getTestItem() {
        return testItem;
    }

    public void setTestItem(TestItem testItem) {
        this.testItem = testItem;
    }
}

