package io.jenkins.plugins.autonomiq.service.types;

import java.util.Date;

public class NullTime {
    Date Time;
    boolean Valid;

    public NullTime(Date time, boolean valid) {
        Time = time;
        Valid = valid;
    }

    public Date getTime() {
        return Time;
    }

    public boolean isValid() {
        return Valid;
    }
}
