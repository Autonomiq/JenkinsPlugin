package io.jenkins.plugins.autonomiq;

import io.jenkins.plugins.autonomiq.testplan.TestItem;

public class TestCaseData {
    private Long testCaseId;
    private Long testScriptId;
    private Long executionId;
    private TestItem testItem;

    public TestCaseData() {

    }

    public Long getTestCaseId() {
        return testCaseId;
    }

    public void setTestCaseId(Long testCaseId) {
        this.testCaseId = testCaseId;
    }

    public Long getTestScriptId() {
        return testScriptId;
    }

    public void setTestScriptId(Long testScriptId) {
        this.testScriptId = testScriptId;
    }

    public Long getExecutionId() {
        return executionId;
    }

    public void setExecutionId(Long executionId) {
        this.executionId = executionId;
    }

    public TestItem getTestItem() {
        return testItem;
    }

    public void setTestItem(TestItem testItem) {
        this.testItem = testItem;
    }
}

