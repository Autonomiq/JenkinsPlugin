package io.jenkins.plugins.autonomiq;

import io.jenkins.plugins.autonomiq.service.ServiceException;

public enum ExecStatus {
    INPROGRESS,
    SUCCESS,
    ERROR;

    public static ExecStatus getEnumForName(String name) throws ServiceException {
        for (ExecStatus v : values()) {
            if (name.equals(v.name())) {
                return v;
            }
        }
        throw new ServiceException("Unknown execution status name: " + name);
    }
}
