package io.jenkins.plugins.autonomiq;

import io.jenkins.plugins.autonomiq.service.ServiceException;

public enum TestStepStatus {
    SUCCESS("0"),
    SUCCESS2("1"),
    WARNING("2"),
    FAILURE("3"),
    IN_PROGRESS("4"),
    NOT_YET_CHECKED("5"),
    STOPPED("6");

    String val;

    TestStepStatus(String val) {
        this.val = val;
    }

    public static TestStepStatus getEnumForName(String name) throws ServiceException {
        for (TestStepStatus v : values()) {
            if (name.equals(v.name())) {
                return v;
            }
        }
        throw new ServiceException("Unknown test step status name: " + name);
    }

    public static TestStepStatus getEnumForValue(String val) throws ServiceException {
        for (TestStepStatus v : values()) {
            if (v.val.equals(val)) {
                return v;
            }
        }
        throw new ServiceException("Unknown test step status value: " + val);
    }
}
