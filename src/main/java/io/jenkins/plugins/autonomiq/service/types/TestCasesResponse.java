package io.jenkins.plugins.autonomiq.service.types;

import java.util.Arrays;
import java.util.Date;

public class TestCasesResponse {
    private String testSuiteName;
    private Long testCaseId;
    private String testCaseName;
    private Date createdTime;
    private String s3URL;
    private Long discoveryId;
    private BrokenDownInstruction testSteps[];
    private Instruction recoverTestSteps[];
    private TestScriptResponse testScripts[];
    private Boolean unmetDependency;
    private String dependentFiles[];
    private String src;

    public TestCasesResponse(String testSuiteName, Long testCaseId, String testCaseName,
                             Date createdTime, String s3URL, Long discoveryId,
                             BrokenDownInstruction[] testSteps, Instruction[] recoverTestSteps,
                             TestScriptResponse[] testScripts, Boolean unmetDependency,
                             String dependentFiles[], String src) {
        this.testSuiteName = testSuiteName;
        this.testCaseId = testCaseId;
        this.testCaseName = testCaseName;
        this.createdTime = new Date(createdTime.getTime());
        this.s3URL = s3URL;
        this.discoveryId = discoveryId;
        this.testSteps = Arrays.copyOf(testSteps, testSteps.length);
        this.recoverTestSteps = Arrays.copyOf(recoverTestSteps, recoverTestSteps.length);
        this.testScripts = Arrays.copyOf(testScripts, testScripts.length);
        this.unmetDependency = unmetDependency;
        this.dependentFiles = Arrays.copyOf(dependentFiles, dependentFiles.length);
        this.src = src;
    }
    @SuppressWarnings("unused")
    public String getTestSuiteName() {
        return testSuiteName;
    }
    @SuppressWarnings("unused")
    public Long getTestCaseId() {
        return testCaseId;
    }
    @SuppressWarnings("unused")
    public String getTestCaseName() {
        return testCaseName;
    }
    @SuppressWarnings("unused")
    public Date getCreatedTime() {
        return new Date(createdTime.getTime());
    }
    @SuppressWarnings("unused")
    public String getS3URL() {
        return s3URL;
    }
    @SuppressWarnings("unused")
    public Long getDiscoveryId() {
        return discoveryId;
    }
    @SuppressWarnings("unused")
    public BrokenDownInstruction[] getTestSteps() {
        return Arrays.copyOf(testSteps, testSteps.length);
    }
    @SuppressWarnings("unused")
    public Instruction[] getRecoverTestSteps() {
        return Arrays.copyOf(recoverTestSteps, recoverTestSteps.length);
    }
    @SuppressWarnings("unused")
    public TestScriptResponse[] getTestScripts() {
        return Arrays.copyOf(testScripts, testScripts.length);
    }
    @SuppressWarnings("unused")
    public Boolean getUnmetDependency() {
        return unmetDependency;
    }
    @SuppressWarnings("unused")
    public String[] getDependentFiles() {
        return Arrays.copyOf(dependentFiles, dependentFiles.length);
    }
    @SuppressWarnings("unused")
    public String getSrc() {
        return src;
    }
}

