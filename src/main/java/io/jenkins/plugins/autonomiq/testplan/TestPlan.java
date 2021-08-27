package io.jenkins.plugins.autonomiq.testplan;

import java.util.List;

public class TestPlan {
    private List<TestPlanParser.Variable> initialVars;
    private List<TestItem> seq;

    public TestPlan(List<TestPlanParser.Variable> initialVars, List<TestItem> seq) {
        this.initialVars = initialVars;
        this.seq = seq;
    }

    public List<TestPlanParser.Variable> getInitialVars() {
        return initialVars;
    }

    public List<TestItem> getSeq() {
        return seq;
    }
}