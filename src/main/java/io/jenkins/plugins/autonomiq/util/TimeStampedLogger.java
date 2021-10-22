package io.jenkins.plugins.autonomiq.util;

import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.Date;

public class TimeStampedLogger {

    private final SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private static final String separator = "| ";

    private final PrintStream log;

    public TimeStampedLogger(PrintStream log) {
        this.log = log;
    }

    public void println() {
        log.println();
    }

    public void println(String val) {
        log.println(prefix() + val);
    }

    public void printf(String format, Object... args) {
        log.print(prefix() + String.format(format, args));
    }

    private String prefix() {
        return df.format(new Date()) + separator;
    }
}
