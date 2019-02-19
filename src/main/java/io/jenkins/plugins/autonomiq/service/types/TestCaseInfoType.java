package io.jenkins.plugins.autonomiq.service.types;

public enum TestCaseInfoType {
    ALL_TYPES(-1),
    ORIGINAL_STEPS(1),
    RECOVER_STEPS(2),
    REGULAR_STEPS(3);

    private Integer val;

    TestCaseInfoType(Integer val) {
        this.val = val;
    }

    public Integer getVal() {
        return this.val;
    }

}
