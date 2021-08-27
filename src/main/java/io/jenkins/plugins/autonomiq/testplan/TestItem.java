package io.jenkins.plugins.autonomiq.testplan;

import java.util.List;

public class TestItem {
    private List<TestPlanParser.Variable> setVars;
    private String caseName;
    private List<TestPlanParser.Variable> showVars;
    private List<TestPlanParser.Variable> validateVars;

    public TestItem(List<TestPlanParser.Variable> setVars, String caseName, List<TestPlanParser.Variable> showVars, List<TestPlanParser.Variable> validateVars) {
        this.setVars = setVars;
        this.caseName = caseName;
        this.showVars = showVars;
        this.validateVars = validateVars;
    }

    public List<TestPlanParser.Variable> getSetVars() {
        return setVars;
    }

    public String getCaseName() {
        return caseName;
    }

    public List<TestPlanParser.Variable> getShowVars() {
        return showVars;
    }

    public List<TestPlanParser.Variable> getValidateVars() {
        return validateVars;
    }
}
