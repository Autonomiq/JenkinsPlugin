package io.jenkins.plugins.autonomiq.service.types;

public class ExceptionForDb {
    private String exceptionClass;
    private String stacktrace;

    public ExceptionForDb(String exceptionClass, String stacktrace) {
        this.exceptionClass = exceptionClass;
        this.stacktrace = stacktrace;
    }
    @SuppressWarnings("unused")
    public String getExceptionClass() {
        return exceptionClass;
    }
    @SuppressWarnings("unused")
    public String getStacktrace() {
        return stacktrace;
    }
}
