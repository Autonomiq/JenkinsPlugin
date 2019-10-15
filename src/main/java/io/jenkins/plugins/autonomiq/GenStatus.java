package io.jenkins.plugins.autonomiq;

import io.jenkins.plugins.autonomiq.service.ServiceException;

public enum GenStatus {
    INPROGRESS,
    SUCCESS,
    FAILED;

    public static GenStatus getEnumForName(String name) throws ServiceException {
        for (GenStatus v : values()) {
            if (name.equals(v.name())) {
                return v;
            }
        }
        throw new ServiceException("Unknown generation status name: " + name);
    }
}

