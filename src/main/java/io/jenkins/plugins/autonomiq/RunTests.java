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

class RunTests {


    class TestData {
        private Long testCaseId;
        private Long testScriptId;
        private Long executionId;
        private TestItem testItem;

        public TestData() {

        }

        public Long getTestCaseId() {
            return testCaseId;
        }

        public void setTestCaseId(Long testCaseId) {
            this.testCaseId = testCaseId;
        }

        public Long getTestScriptId() {
            return testScriptId;
        }

        public void setTestScriptId(Long testScriptId) {
            this.testScriptId = testScriptId;
        }

        public Long getExecutionId() {
            return executionId;
        }

        public void setExecutionId(Long executionId) {
            this.executionId = executionId;
        }

        public TestItem getTestItem() {
            return testItem;
        }

        public void setTestItem(TestItem testItem) {
            this.testItem = testItem;
        }
    }

    enum TestStepStatus {
        SUCCESS("0"),
        SUCCESS2("1"),
        WARNING("2"),
        FAILURE("3"),
        IN_PROGRESS("4"),
        NOT_YET_CHECKED("5"),
        STOPPED("6");

        String val;

        TestStepStatus(String val) {
            this.val = val;
        }

        public static TestStepStatus getEnumForName(String name) throws ServiceException {
            for (TestStepStatus v : values()) {
                if (name.equals(v.name())) {
                    return v;
                }
            }
            throw new ServiceException("Unknown test step status name: " + name);
        }

        public static TestStepStatus getEnumForValue(String val) throws ServiceException {
            for (TestStepStatus v : values()) {
                if (v.val.equals(val)) {
                    return v;
                }
            }
            throw new ServiceException("Unknown test step status value: " + val);
        }
    }

    enum ExecStatus {
        INPROGRESS,
        SUCCESS,
        ERROR;

        public static ExecStatus getEnumForName(String name) throws ServiceException {
            for (ExecStatus v : values()) {
                if (name.equals(v.name())) {
                    return v;
                }
            }
            throw new ServiceException("Unknown execution status name: " + name);
        }
    }

    enum GenStatus {
        INPROGRESS,
        SUCCESS,
        FAILED;

        public static GenStatus getEnumForName(String name) throws ServiceException {
            for (GenStatus v : values()) {
                if (name.equals(v.name())) {
                    return v;
                }
            }
            throw new ServiceException("Unknown generation status name: " + name);
        }
    }

    private static String executionType = "smoke";

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
    private Set<Long> execSucceededId;
    private Set<Long> execFailedId;
    private List<TestData> testDataList;
    private Map<Long, TestData> testDataByTestCaseId;
    private Boolean runSequential;
    TestPlan plan;


    public RunTests(ServiceAccess svc,
                    TimeStampedLogger log,
                    ProjectData pd,
                    Long pollingIntervalMs) {
        this.svc = svc;
        this.log = log;
        this.pd = pd;
        this.pollingIntervalMs = pollingIntervalMs;

        testScriptByTestCaseId = new HashMap<>();

    }

    /**
     * @param generateScripts
     * @return returns true if all tests execute successfully
     */
    public Boolean runTests(TestPlan plan, Boolean generateScripts, Boolean runTestCases,
                            String platform, String browser) {

        this.plan = plan;

        if (!(generateScripts || runTestCases)) {
            log.println("Neither generate scripts nor run test cases selected, no work to do.");
            return true;
        }

        try {
            getTestCases(plan, pd.getProjectId(), pd.getDiscoveryId());
        } catch (ServiceException e) {
            log.println(AiqUtil.getExceptionTrace(e));
            return false;
        }

        logTestCaseNames();

        if (generateScripts) {
            if (!handleScriptGeneration()) {
                return false;
            }
        }

        if (runTestCases) {
            return handleTestExecutions(generateScripts, platform, browser);
        }

        return true;
    }

    private Boolean handleScriptGeneration() {

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
        for (TestData testData : testDataList) {
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
                    }

                    List<TestScriptResponse> scripts = svc.getTestScript(pd.getProjectId(), testCaseId);

                    TestScriptResponse scriptStart = testScriptByTestCaseId.get(testCaseId);

                    for (TestScriptResponse script : scripts) {

                        if (script.getTestScriptid().equals(scriptStart.getTestScriptid())) {

                            GenStatus p = GenStatus.getEnumForName(script.getTestScriptGenerationStatus());

                            switch (p) {
                                case INPROGRESS:
//                                log.printf("Script generation for test case %s still in progress\n",
//                                        testCaseName);
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

            } catch (Exception e) {
                log.printf("Exception running test case: %s\n", testCaseName);
                log.println(AiqUtil.getExceptionTrace(e));
                ret = false;
                break;
            }
        }

        return ret;
    }

    private Boolean handleTestExecutions(Boolean generateScripts, String platform, String browser) {

        if (!generateScripts) {
            // use last test script in current list for test case
            for (TestCasesResponse r : testCasesById.values()) {
                TestScriptResponse[] scripts = r.getTestScripts();
                TestScriptResponse s = scripts[scripts.length - 1];
                TestData testData = testDataByTestCaseId.get(r.getTestCaseId());
                testData.setTestScriptId(s.getTestScriptid());
            }
        }

        log.println();
        log.printf("==== Starting test executions for project %s\n", pd.getProjectName());
        log.println();

        if (runSequential) {
            return runSequentialTestExecutions(platform, browser);
        } else {
            try {
                // runTestsData gets updated with execution ids
                runTestExecutions(testDataList, platform, browser);
            } catch (ServiceException e) {
                log.println("Exception running test executions.");
                log.println(AiqUtil.getExceptionTrace(e));
                return false;
            }

            Boolean runCheck;
            try {
                runCheck = checkRunTests(testDataList);
            } catch (ServiceException e) {
                log.println("Exception checking test executions.");
                log.println(AiqUtil.getExceptionTrace(e));
                return false;
            }

            if (runCheck) {
                log.println("All test executions succeeded.");
                log.println();
                return true;
            } else {
                log.println("Not all test executions succeeded.");
                log.println();
                return false;
            }
        }

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

    private Boolean runSequentialTestExecutions(String platform, String browser) {

        Boolean ret = true;

        ret = setInitialVariables(plan.getInitialVars());
        if (ret == false) {
            return false;
        }

        foreachtest:
        for (TestData testData : testDataList) {

            String testCaseName = testCasesById.get(testData.getTestCaseId()).getTestCaseName();

            try {
                log.println();
                log.printf("==== Starting execution of test case: %s\n", testCaseName);

                setVariables(testData.getTestItem().getSetVars());

                String testExecutionName = String.format("Jenkins_%s", testCasesById.get(testData.getTestCaseId()).getTestCaseName());
                ExecuteTaskResponse resp = svc.runTestCase(pd.getProjectId(), testData.getTestScriptId(),
                        testExecutionName,
                        platform, browser,
                        executionType);
                log.println("Execution started");

                // save execution id
                Long testExecId = resp.getExecutionId().longValue();
                testData.setExecutionId(testExecId);

                boolean done = false;

                while (!done) {

                    // pause
                    try {
                        Thread.sleep(pollingIntervalMs);
                    } catch (InterruptedException e) {
                        log.println("Check execution sleep interrupted");
                    }

                    ExecuteTaskResponse r = svc.getExecutedTask(testExecId);

                    ExecStatus stat = ExecStatus.getEnumForName(r.getExecutionStatus());

                    switch (stat) {
                        case INPROGRESS:
                            //log.println("Test execution still in progress");
                            break;
                        case SUCCESS:
                            log.printf("Test execution for test case '%s' succeeded\n", testCaseName);
                            showVariables(testData.getTestItem().getShowVars());
                            ret = validateVariables(testData.getTestItem().getValidateVars());
                            done = true;
                            break;
                        case ERROR:
                            log.printf("Test execution for test case '%s' failed", testCaseName);
                            ret = false;
                            break foreachtest;
                    }
                }

            } catch (ServiceException e) {
                log.printf("Exception during execution of test case '%s'\n", testCaseName);
                log.println(AiqUtil.getExceptionTrace(e));
                ret = false;
                break;
            }
        }

        return ret;
    }

    private void showTestStepsForCase(Long testCaseId) throws ServiceException {
        TestCasesResponse testCase = svc.getTestCase(pd.getProjectId(), pd.getDiscoveryId(),
                testCaseId);
        showTestSteps("", testCase.getTestSteps());
    }

    private void showTestSteps(String stepNumPrefix, BrokenDownInstruction[] testSteps) {
        int index = 1;
        for (BrokenDownInstruction step : testSteps) {

            String stepNumber = stepNumPrefix + index + ".";

            String statusValue = step.getStatus();
            TestStepStatus status = null;
            String err = null;
            try {
                status = TestStepStatus.getEnumForValue(statusValue);
            } catch (ServiceException e) {
                err = e.getMessage();
            }
            if (err != null) {
                log.printf("Step %s Error: %s\n", stepNumber, err);
            } else {
                switch (status) {
                    case SUCCESS:
                    case SUCCESS2:
                        log.printf("Step %s %s %s: Status %s\n", stepNumber, step.getInstruction(), step.getData(), TestStepStatus.SUCCESS.name());
                        break;
                    case WARNING:
                    case FAILURE:
                    case IN_PROGRESS:
                    case NOT_YET_CHECKED:
                    case STOPPED:
                        log.printf("Step %s %s %s: Status %s\n", stepNumber, step.getInstruction(), step.getData(), status.name());
                        break;
                }
            }

            if (step.getSubinstructions() != null && step.getSubinstructions().length > 0) {
                showTestSteps(stepNumber, step.getSubinstructions());
            }

            index++;
        }
    }

    private void logTestCaseNames() {
        if (plan != null) {
            log.printf("==== Test case sequence from test plan for project %s:\n", pd.getProjectName());

        } else {
            log.printf("==== Found these %s test cases in project %s:\n", testCasesById.size(), pd.getProjectName());

        }
        for (TestData td : testDataList) {
            log.println(testCasesById.get(td.getTestCaseId()).getTestCaseName());
        }
    }

    private void getTestCases(TestPlan plan, Long projectId, Long discoveryId) throws ServiceException {
        testCasesById = new HashMap<>();
        testCasesByName = new TreeMap<>();

        List<TestCasesResponse> tc = svc.getTestCasesForProject(projectId, discoveryId);
        for (TestCasesResponse t : tc) {
            testCasesById.put(t.getTestCaseId(), t);
            testCasesByName.put(t.getTestCaseName(), t);
        }

        testDataList = new LinkedList<>();
        testDataByTestCaseId = new HashMap<>();

        if (plan == null) {
            runSequential = false;

            for (Long testCaseId : testCasesById.keySet()) {
                TestData testData = new TestData();
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
                TestData testData = new TestData();
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
            TestData testData = testDataByTestCaseId.get(t.getTestCaseId());
            testData.setTestScriptId(t.getTestScriptid());
        }
    }

    private void printCount(int count) {
        log.printf("%d...\n", count);
    }

    private Boolean checkScriptGenerations() throws ServiceException {
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
            }

//            log.println();
//            log.printf("==== Checking %d test cases still in progress\n", testCasesInProgress.size());

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
//                                log.printf("Script generation for test case %s still in progress\n",
//                                        testCaseName);
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

//            log.printf("Test case '%s' script gen status '%s'\n", testCasesById.get(r.getTestCaseId()).getTestCaseName(),
//                                            r.getTestScriptGenerationStatus());
//            if (r.getTestScriptDownloadLink() != null) {
//                log.printf("  Script download link: %s\n", r.getTestScriptDownloadLink());
//            }
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

    private void runTestExecutions(List<TestData> runTestsData, String platform,
                                   String browser) throws ServiceException {

        for (TestData t : runTestsData) {

            String testExecutionName = String.format("Jenkins_%s", testCasesById.get(t.getTestCaseId()).getTestCaseName());

            ExecuteTaskResponse resp = svc.runTestCase(pd.getProjectId(), t.getTestScriptId(),
                    testExecutionName,
                    platform, browser,
                    executionType);

            // save execution id
            t.setExecutionId(resp.getExecutionId().longValue());
        }
    }

    private Boolean checkRunTests(List<TestData> runTestsData) throws ServiceException {

        execSucceededId = new HashSet<>();
        execFailedId = new HashSet<>();

        Map<Long, TestData> testMap = new HashMap<>();
        for (TestData r : runTestsData) {
            testMap.put(r.executionId, r);
        }

        Set<Long> testExecsInProgress = new HashSet<>(testMap.keySet());

        int lastCount = testExecsInProgress.size();
        log.printf("Number of test executions still in progress:\n");
        printCount(lastCount);

        while (testExecsInProgress.size() > 0) {

            // pause
            try {
                Thread.sleep(pollingIntervalMs);
            } catch (InterruptedException e) {
                log.println("Check test executions sleep interrupted");
            }


            Iterator<Long> i = testExecsInProgress.iterator();
            while (i.hasNext()) {

                Long testExecId = i.next();

                ExecuteTaskResponse r = svc.getExecutedTask(testExecId);

                ExecStatus stat = ExecStatus.getEnumForName(r.getExecutionStatus());

                switch (stat) {
                    case INPROGRESS:
                        //log.println("Test execution still in progress");
                        break;
                    case SUCCESS:
                        //log.println("Test execution succeeded");
                        execSucceededId.add(testExecId);
                        i.remove();
                        break;
                    case ERROR:
                        //log.println("Test execution failed");
                        execFailedId.add(testExecId);
                        i.remove();
                        break;
                }


            }

            int newCount = testExecsInProgress.size();
            if (newCount != lastCount) {
                printCount(newCount);
                lastCount = newCount;
            }
        }
        log.println();

        if (execSucceededId.size() > 0) {
            log.println("==== Test execution passed for test cases:");
            for (Long execId : execSucceededId) {
                TestData td = testMap.get(execId);
                Long tc = td.getTestCaseId();
                log.printf("%s\n", testCasesById.get(tc).getTestCaseName());
            }
            log.println();
        }

        if (execFailedId.size() > 0) {
            log.println("==== Test execution failed for test cases:");
            for (Long execId : execFailedId) {
                TestData td = testMap.get(execId);
                Long tc = td.getTestCaseId();
                log.printf("%s\n", testCasesById.get(tc).getTestCaseName());
            }
            log.println();
        }

        return execFailedId.size() == 0;
    }

}
