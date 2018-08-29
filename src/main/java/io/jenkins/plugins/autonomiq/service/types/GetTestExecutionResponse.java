package io.jenkins.plugins.autonomiq.service.types;

import java.util.List;

public class GetTestExecutionResponse {
    private Integer totalTestsCount;
    private Integer totalPassedCount;
    private Integer totalFailedCount;
    private Integer totalSkippedCount;
    private List<String> passedMethods;
    private List<String> failedMethods;
    private List<String> skippedMethods;
    private List<ClassForDb> classes;
    private String reportDownloadLink;

    public GetTestExecutionResponse(Integer totalTestsCount, Integer totalPassedCount, Integer totalFailedCount,
                                    Integer totalSkippedCount, List<String> passedMethods,
                                    List<String> failedMethods, List<String> skippedMethods,
                                    List<ClassForDb> classes, String reportDownloadLink) {
        this.totalTestsCount = totalTestsCount;
        this.totalPassedCount = totalPassedCount;
        this.totalFailedCount = totalFailedCount;
        this.totalSkippedCount = totalSkippedCount;
        this.passedMethods = passedMethods;
        this.failedMethods = failedMethods;
        this.skippedMethods = skippedMethods;
        this.classes = classes;
        this.reportDownloadLink = reportDownloadLink;
    }
    @SuppressWarnings("unused")
    public Integer getTotalTestsCount() {
        return totalTestsCount;
    }
    @SuppressWarnings("unused")
    public Integer getTotalPassedCount() {
        return totalPassedCount;
    }
    @SuppressWarnings("unused")
    public Integer getTotalFailedCount() {
        return totalFailedCount;
    }
    @SuppressWarnings("unused")
    public Integer getTotalSkippedCount() {
        return totalSkippedCount;
    }
    @SuppressWarnings("unused")
    public List<String> getPassedMethods() {
        return passedMethods;
    }
    @SuppressWarnings("unused")
    public List<String> getFailedMethods() {
        return failedMethods;
    }
    @SuppressWarnings("unused")
    public List<String> getSkippedMethods() {
        return skippedMethods;
    }
    @SuppressWarnings("unused")
    public List<ClassForDb> getClasses() {
        return classes;
    }
    @SuppressWarnings("unused")
    public String getReportDownloadLink() {
        return reportDownloadLink;
    }
}
