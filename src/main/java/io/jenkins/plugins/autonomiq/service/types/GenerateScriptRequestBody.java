package io.jenkins.plugins.autonomiq.service.types;

import java.util.Collection;

public class GenerateScriptRequestBody {
    private Collection<Long> testCases;
    private String sessionId;

    public GenerateScriptRequestBody(Collection<Long> testCaseIds, String sessionId) {
        this.testCases = testCaseIds;
        this.sessionId = sessionId;
    }
    @SuppressWarnings("unused")
    public Collection<Long> getTestCases() {
        return testCases;
    }
    @SuppressWarnings("unused")
    public String getSessionId() {
        return sessionId;
    }
}
