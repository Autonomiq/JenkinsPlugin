package io.jenkins.plugins.autonomiq.service.types;

public class TestCaseInfo {
    private Integer case_id;
    private String orig_steps;
    private String recover_steps;
    private String test_steps;

    public TestCaseInfo(Integer case_id, String orig_steps, String recover_steps, String test_steps) {
        this.case_id = case_id;
        this.orig_steps = orig_steps;
        this.recover_steps = recover_steps;
        this.test_steps = test_steps;
    }

    public Integer getCase_id() {
        return case_id;
    }

    public String getOrig_steps() {
        return orig_steps;
    }

    public String getRecover_steps() {
        return recover_steps;
    }

    public String getTest_steps() {
        return test_steps;
    }
}
