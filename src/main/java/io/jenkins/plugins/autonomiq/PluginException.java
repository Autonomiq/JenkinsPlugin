package io.jenkins.plugins.autonomiq;

public class PluginException extends Exception {

    public PluginException(String message) {
        super (message);
    }


    public PluginException(String message, Throwable cause) {
        super (message, cause);
    }

}