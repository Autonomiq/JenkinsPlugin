package io.jenkins.plugins.autonomiq.service.types;

import java.util.Date;

public class NullTime {
    Date Time;
    boolean Valid;

    public NullTime(Date time, boolean valid) {
        Time = new Date(time.getTime());
        Valid = valid;
    }

    public Date getTime() {
        return new Date(Time.getTime());
    }

    public boolean isValid() {
        return Valid;
    }
}
