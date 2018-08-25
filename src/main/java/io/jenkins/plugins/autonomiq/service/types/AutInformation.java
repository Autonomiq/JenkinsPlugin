package io.jenkins.plugins.autonomiq.service.types;

public class AutInformation {
    private Long discoveryId;
    private String appUrl;
    private String appName;
    private String appLoginUsername;
    private String discoveryStatus;
    private Integer testData;
    private Integer testScripts;
    private Integer testCases;

    public AutInformation(Long discoveryId, String appUrl, String appName,
                          String appLoginUsername, String discoveryStatus,
                          Integer testData, Integer testScripts, Integer testCases) {
        this.discoveryId = discoveryId;
        this.appUrl = appUrl;
        this.appName = appName;
        this.appLoginUsername = appLoginUsername;
        this.discoveryStatus = discoveryStatus;
        this.testData = testData;
        this.testScripts = testScripts;
        this.testCases = testCases;
    }
    @SuppressWarnings("unused")
    public Long getDiscoveryId() {
        return discoveryId;
    }
    @SuppressWarnings("unused")
    public String getAppUrl() {
        return appUrl;
    }
    @SuppressWarnings("unused")
    public String getAppName() {
        return appName;
    }
    @SuppressWarnings("unused")
    public String getAppLoginUsername() {
        return appLoginUsername;
    }
    @SuppressWarnings("unused")
    public String getDiscoveryStatus() {
        return discoveryStatus;
    }
    @SuppressWarnings("unused")
    public Integer getTestData() {
        return testData;
    }
    @SuppressWarnings("unused")
    public Integer getTestScripts() {
        return testScripts;
    }
    @SuppressWarnings("unused")
    public Integer getTestCases() {
        return testCases;
    }

}
