package io.jenkins.plugins.autonomiq;

import io.jenkins.plugins.autonomiq.service.ServiceAccess;
import io.jenkins.plugins.autonomiq.service.ServiceException;
import io.jenkins.plugins.autonomiq.service.types.*;
import io.jenkins.plugins.autonomiq.testplan.TestItem;
import io.jenkins.plugins.autonomiq.testplan.TestPlan;
import io.jenkins.plugins.autonomiq.testplan.TestPlanParser;
import io.jenkins.plugins.autonomiq.util.AiqUtil;
import io.jenkins.plugins.autonomiq.util.TimeStampedLogger;

import java.util.*;

public class RunGenScripts {

    private ServiceAccess svc;
    private TimeStampedLogger log;
    private ProjectData pd;
    private Long pollingIntervalMs;

    private Map<Long, TestCasesResponse> testCasesById;
    private Map<String, TestCasesResponse> testCasesByName;
    private Map<Long, TestScriptResponse> testScriptByTestCaseId;
    private Set<Long> gensSucceededCaseId;
    private Set<Long> gensFailedCaseId;
    private Map<Long, TestScriptResponse> scriptGenResponses;
    private List<TestCaseData> testDataList;
    private Map<Long, TestCaseData> testDataByTestCaseId;
    private Boolean runSequential;
    TestPlan plan;


    public RunGenScripts(ServiceAccess svc,
                        TimeStampedLogger log,
                        ProjectData pd,
                        Long pollingIntervalMs) {

        this.svc = svc;
        this.log = log;
        this.pd = pd;
        this.pollingIntervalMs = pollingIntervalMs;

        testScriptByTestCaseId = new HashMap<>();

    }

    public Boolean genScripts(String genCaseList) throws PluginException, InterruptedException {

        AiqUtil.ItemListFromString itemsObj = AiqUtil.getItemListFromString(genCaseList);

        if (itemsObj.getError() != null) {
            log.printf("Error getting item list from generate script case list '%s'", itemsObj.getError());
            return false;

        } else if (itemsObj.getItemList().size() > 0) {
            plan = testCaseListToPlan(itemsObj.getItemList());
        }

        try {
            getTestCases(plan, pd.getProjectId());
        } catch (ServiceException e) {
            log.println(AiqUtil.getExceptionTrace(e));
            return false;
        }

        logTestCaseNames();

        if (!handleScriptGeneration()) {
            return false;
        }

         return true;
    }

    private TestPlan testCaseListToPlan(List<String> caseList) {

        List<TestPlanParser.Variable> initialVars = new LinkedList<>();
        List<TestItem> seq = new LinkedList<>();

        for (String caseName : caseList) {
            TestItem item = new TestItem(new LinkedList<>(), caseName, new LinkedList<>(), new LinkedList<>());
            seq.add(item);
        }

        if (seq.size() == 0) {
            return null;
        } else {
            TestPlan plan = new TestPlan(initialVars, seq);
            return plan;
        }
    }

    private Boolean handleScriptGeneration() throws InterruptedException {

        log.println();
        log.printf("==== Starting script generations for project %s\n", pd.getProjectName());
        log.println();

        if (runSequential) {
            return runSequentialScriptGenerations();
        } else {

            try {
                startAllScriptGenerations();
            } catch (ServiceException e) {
                log.println(AiqUtil.getExceptionTrace(e));
                return false;
            }

            Boolean allGensSuccessful;

            try {
                allGensSuccessful = checkScriptGenerations();
            } catch (ServiceException e) {
                log.println(AiqUtil.getExceptionTrace(e));
                return false;
            }

            if (allGensSuccessful) {
                log.println("All test script generations succeeded.");
                log.println();
            } else {
                log.println("Not all test scripts generations succeeded, ending run.");
                log.println();
                return false;
            }
        }

        return true;
    }

    private void setVariables(List<TestPlanParser.Variable> vars) throws ServiceException {
        for (TestPlanParser.Variable v : vars) {
            log.printf("Setting variable '%s' to value '%s'\n", v.getName(), v.getValue());
            svc.saveUserVariable(pd.getProjectId(), v.getName(), v.getValue());
        }
    }

    private void showVariables(List<TestPlanParser.Variable> vars) throws ServiceException {
        for (TestPlanParser.Variable v : vars) {
            log.printf("Getting variable '%s'\n", v.getName());
            UserVariable gotVar = svc.getUserVariable(pd.getProjectId(), v.getName());
            log.printf("Variable '%s' value is '%s'\n", gotVar.getKey(), gotVar.getValue());
        }
    }

    private Boolean validateVariables(List<TestPlanParser.Variable> vars) throws ServiceException {
        Boolean ret = true;

        for (TestPlanParser.Variable v : vars) {
            log.printf("Getting variable '%s'\n", v.getName());
            UserVariable gotVar = svc.getUserVariable(pd.getProjectId(), v.getName());
            if (v.getValue().equals(gotVar.getValue())) {
                log.printf("Variable '%s' validate successful '%s'\n", gotVar.getKey(), gotVar.getValue());
            } else {
                log.printf("Variable '%s' validate failed, expected '%s' got '%s'\n",
                        v.getName(), v.getValue(), gotVar.getValue());
                ret = false;
            }
        }

        return ret;
    }

    private Boolean runSequentialScriptGenerations() {

        Boolean ret = true;

        ret = setInitialVariables(plan.getInitialVars());
        if (ret == false) {
            return false;
        }

        foreachtest:
        for (TestCaseData testData : testDataList) {
            Long testCaseId = testData.getTestCaseId();
            String testCaseName = testCasesById.get(testCaseId).getTestCaseName();

            try {
                log.println();
                log.printf("=== Starting script generation for test case: %s\n", testCaseName);
                setVariables(testData.getTestItem().getSetVars());

                // start generation
                List<Long> caseList = new LinkedList<>();
                caseList.add(testCaseId);
                List<TestScriptResponse> tsr = svc.startTestScripGeneration(pd.getProjectId(), caseList);
                log.println("Script generation started");

                TestScriptResponse resp = tsr.get(0);
                testScriptByTestCaseId.put(testCaseId, resp);
                testData.setTestScriptId(resp.getTestScriptid());

                boolean done = false;

                while (!done) {

                    // pause
                    try {
                        Thread.sleep(pollingIntervalMs);
                    } catch (InterruptedException e) {
                        log.println("Check scripts generation sleep interrupted");
                        throw e;
                    }

                    List<TestScriptResponse> scripts = svc.getTestScript(pd.getProjectId(), testCaseId);

                    TestScriptResponse scriptStart = testScriptByTestCaseId.get(testCaseId);

                    for (TestScriptResponse script : scripts) {

                        if (script.getTestScriptid().equals(scriptStart.getTestScriptid())) {

                            GenStatus p = GenStatus.getEnumForName(script.getTestScriptGenerationStatus());

                            switch (p) {
                                case INPROGRESS:

                                    break;
                                case SUCCESS:
                                    log.printf("Script generation for test case '%s' succeeded\n",
                                            testCaseName);
                                    done = true;

                                    showTestStepsForCase(testData.getTestCaseId());

                                    showVariables(testData.getTestItem().getShowVars());
                                    ret = validateVariables(testData.getTestItem().getValidateVars());

                                    break;
                                case FAILED:
                                    log.printf("Script generation for test case '%s' failed\n",
                                            testCaseName);

                                    showTestStepsForCase(testData.getTestCaseId());

                                    ret = false;
                                    break foreachtest;

                            }

                        }
                    }
                }

                // TODO run variable shows/validates
            } catch (RuntimeException e) {
                throw e;

            } catch (Exception e) {
                log.printf("Exception running test case: %s\n", testCaseName);
                log.println(AiqUtil.getExceptionTrace(e));
                ret = false;
                break;
            }
        }

        return ret;
    }

    private Boolean setInitialVariables(List<TestPlanParser.Variable> vars) {
        try {
            log.println("Setting any initial variables.");
            setVariables(plan.getInitialVars());
        } catch (ServiceException e) {
            log.printf("Exception setting initial variables\n");
            log.println(AiqUtil.getExceptionTrace(e));
            return false;
        }
        return true;
    }

    private void showTestStepsForCase(Long testCaseId) throws ServiceException {

        TestCaseInfo caseInfo = svc.getTestCaseInfo(testCaseId, TestCaseInfoType.REGULAR_STEPS);

        BrokenDownInstruction[] testSteps = AiqUtil.gson.fromJson(caseInfo.getTest_steps(),
                BrokenDownInstruction[].class);
        showTestSteps(testSteps);
    }

    private String stepFormat = "Step %s '%s' - '%s': Status %s\n";
    private void showTestSteps(BrokenDownInstruction[] testSteps) {
        for (BrokenDownInstruction step : testSteps) {

            String statusValue = step.getStatus();
            TestStepStatus status = null;
            String err = null;
            try {
                status = TestStepStatus.getEnumForValue(statusValue);
            } catch (ServiceException e) {
                err = e.getMessage();
            }
            if (err != null) {
                log.printf("Step %s Error: %v\n", step.getInstrNum(), err);
            } else {
                switch (status) {
                    case SUCCESS:
                    case SUCCESS2:
                        log.printf(stepFormat, step.getInstrNum(), step.getInstr(), step.getData(), TestStepStatus.SUCCESS.name());
                        break;
                    case WARNING:
                    case FAILURE:
                    case IN_PROGRESS:
                    case NOT_YET_CHECKED:
                    case STOPPED:
                        log.printf(stepFormat, step.getInstrNum(), step.getInstr(), step.getData(), status.name());
                        break;
                }
            }

            if (step.getSubInstructions() != null && step.getSubInstructions().length > 0) {
                showTestSteps(step.getSubInstructions());
            }

        }
    }

    private void logTestCaseNames() {
        if (plan != null) {
            log.printf("==== Generate script sequence from generate script list\n", pd.getProjectName());

        } else {
            log.printf("==== Found these %s test cases in project %s:\n", testCasesById.size(), pd.getProjectName());

        }
        for (TestCaseData td : testDataList) {
            log.println(testCasesById.get(td.getTestCaseId()).getTestCaseName());
        }
    }

    private void getTestCases(TestPlan plan, Long projectId) throws ServiceException {
        testCasesById = new HashMap<>();
        testCasesByName = new TreeMap<>();

        List<TestCasesResponse> tc = svc.getTestCasesForProject(projectId);
        for (TestCasesResponse t : tc) {
            testCasesById.put(t.getTestCaseId(), t);
            testCasesByName.put(t.getTestCaseName(), t);
        }

        testDataList = new LinkedList<>();
        testDataByTestCaseId = new HashMap<>();

        if (plan == null) {
            runSequential = false;

            for (Long testCaseId : testCasesById.keySet()) {
                TestCaseData testData = new TestCaseData();
                testData.setTestCaseId(testCaseId);
                testDataList.add(testData);
                testDataByTestCaseId.put(testCaseId, testData);
            }
        } else {
            runSequential = true;

            testDataList = new LinkedList<>();
            for (TestItem item : plan.getSeq()) {
                TestCasesResponse testCase = testCasesByName.get(item.getCaseName());
                if (testCase == null) {
                    throw new ServiceException(String.format("No test case found for case name from test plan: %s", item.getCaseName()));
                }
                TestCaseData testData = new TestCaseData();
                testData.setTestCaseId(testCase.getTestCaseId());
                testData.setTestItem(item);
                testDataList.add(testData);
                testDataByTestCaseId.put(testCase.getTestCaseId(), testData);
            }
        }

    }

    private void startAllScriptGenerations() throws ServiceException {

        List<TestScriptResponse> tsr = svc.startTestScripGeneration(pd.getProjectId(), testCasesById.keySet());
        for (TestScriptResponse t : tsr) {
            testScriptByTestCaseId.put(t.getTestCaseId(), t);
            TestCaseData testData = testDataByTestCaseId.get(t.getTestCaseId());
            testData.setTestScriptId(t.getTestScriptid());
        }
    }

    private void printCount(int count) {
        log.printf("%d...\n", count);
    }

    private Boolean checkScriptGenerations() throws ServiceException, InterruptedException {
        // copy ids
        Set<Long> testCasesInProgress = new HashSet<>(testScriptByTestCaseId.keySet());
        gensSucceededCaseId = new HashSet<>();
        gensFailedCaseId = new HashSet<>();
        scriptGenResponses = new HashMap<>();

        int lastCount = testCasesInProgress.size();
        log.printf("Number of test script generations still in progress:\n");
        printCount(lastCount);

        while (testCasesInProgress.size() > 0) {

            // pause
            try {
                Thread.sleep(pollingIntervalMs);
            } catch (InterruptedException e) {
                log.println("Check scripts generation sleep interrupted");
                throw e;
            }


            Iterator<Long> i = testCasesInProgress.iterator();
            while (i.hasNext()) {

                Long testCaseId = i.next();
                List<TestScriptResponse> scripts = svc.getTestScript(pd.getProjectId(), testCaseId);

                TestScriptResponse scriptStart = testScriptByTestCaseId.get(testCaseId);

                for (TestScriptResponse script : scripts) {

                    if (script.getTestScriptid().equals(scriptStart.getTestScriptid())) {

                        GenStatus p = GenStatus.getEnumForName(script.getTestScriptGenerationStatus());

                        switch (p) {
                            case INPROGRESS:
                                break;
                            case FAILED:
                                //log.printf("Script generation for test case '%s' failed\n", testCaseName);
                                scriptGenResponses.put(testCaseId, script);
                                gensFailedCaseId.add(testCaseId);
                                i.remove();
                                break;
                            case SUCCESS:
                                //log.printf("Script generation for test case '%s' succeeded\n", testCaseName);
                                scriptGenResponses.put(testCaseId, script);
                                gensSucceededCaseId.add(testCaseId);
                                i.remove();
                                break;
                            default:
                                throw new ServiceException(String.format("Unknown script generation status '%s'",
                                        script.getTestScriptGenerationStatus()));
                        }

                    }
                }
            }

            int newCount = testCasesInProgress.size();
            if (newCount != lastCount) {
                printCount(newCount);
                lastCount = newCount;
            }

        }
        log.println();

        List<TestScriptResponse> pass = new ArrayList<>();
        List<TestScriptResponse> fail = new ArrayList<>();

        for (TestScriptResponse r : scriptGenResponses.values()) {

            GenStatus stat = GenStatus.getEnumForName(r.getTestScriptGenerationStatus());
            switch (stat) {
                case SUCCESS:
                    pass.add(r);
                    break;
                case FAILED:
                    fail.add(r);
                    break;
                default:
            }

        }

        if (pass.size() > 0) {
            log.println("==== Test script generation passed for test cases:");
            for (TestScriptResponse r : pass) {
                log.printf("%s\n", testCasesById.get(r.getTestCaseId()).getTestCaseName());
            }
            log.println();
        }

        if (fail.size() > 0) {
            log.println("==== Test script generation failed for test cases:");
            for (TestScriptResponse r : fail) {
                log.printf("%s\n", testCasesById.get(r.getTestCaseId()).getTestCaseName());
            }
            log.println();
        }

        return gensFailedCaseId.size() == 0;
    }

}
