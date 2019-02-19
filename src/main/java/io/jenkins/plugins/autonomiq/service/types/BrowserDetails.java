package io.jenkins.plugins.autonomiq.service.types;

public class BrowserDetails {
    private String browser;
    private String browserVersion;

    public BrowserDetails(String browser, String browserVersion) {
        this.browser = browser;
        this.browserVersion = browserVersion;
    }

    public String getBrowser() {
        return browser;
    }

    public String getBrowserVersion() {
        return browserVersion;
    }
}
