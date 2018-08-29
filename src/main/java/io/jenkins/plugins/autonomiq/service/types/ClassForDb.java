package io.jenkins.plugins.autonomiq.service.types;

import java.util.List;

public class ClassForDb {
    private String className;
    private List<TestMethodForDb> testMethods;

    public ClassForDb(String className, List<TestMethodForDb> testMethods) {
        this.className = className;
        this.testMethods = testMethods;
    }
    @SuppressWarnings("unused")
    public String getClassName() {
        return className;
    }
    @SuppressWarnings("unused")
    public List<TestMethodForDb> getTestMethods() {
        return testMethods;
    }
}
