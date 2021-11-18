package io.jenkins.plugins.autonomiq;

public class PluginException extends Exception {
//    public ServiceException () {
//
//    }

    public PluginException(String message) {
        super (message);
    }

//    public ServiceException (Throwable cause) {
//        super (cause);
//    }

    public PluginException(String message, Throwable cause) {
        super (message, cause);
    }

}