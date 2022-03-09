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

class RunTestExecutions {


    private static String executionType = "smoke";

    private ServiceAccess svc;
    private TimeStampedLogger log;
    private ProjectData pd;
    private Long pollingIntervalMs;

    private Map<Long, TestCasesResponse> testCasesById;
    private Map<String, TestCasesResponse> testCasesByName;
    private Set<Long> execSucceededId;
    private Set<Long> execFailedId;
    private List<TestCaseData> testDataList;
    private Map<Long, TestCaseData> testDataByTestCaseId;
    private Boolean runSequential;
    TestPlan plan;


    public RunTestExecutions(ServiceAccess svc,
                             TimeStampedLogger log,
                             ProjectData pd,
                             Long pollingIntervalMs) {
        this.svc = svc;
        this.log = log;
        this.pd = pd;
        this.pollingIntervalMs = pollingIntervalMs;
    }

    public Boolean runTests(String platform,
                            String browser,
                            String runCaseList) throws PluginException, InterruptedException {


        AiqUtil.ItemListFromString itemsObj = AiqUtil.getItemListFromString(runCaseList);

        if (itemsObj.getError() != null) {
            log.printf("Error getting item list from run test case list '%s'", itemsObj.getError());
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

        return handleTestExecutions(platform, browser);

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


    private Boolean handleTestExecutions(String platform, String browser) throws PluginException, InterruptedException {

        // use last test script in current list for test case
        for (TestCasesResponse r : testCasesById.values()) {
            TestScriptResponse[] scripts = r.getTestScripts();
            if (scripts.length <= 0) {
            	continue;
            }
            TestScriptResponse s = scripts[scripts.length - 1];
            TestCaseData testData = testDataByTestCaseId.get(r.getTestCaseId());
            if (testData == null) {
            	continue;
            }
            testData.setTestScriptId(s.getTestScriptid());
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

    private Boolean runSequentialTestExecutions(String platform, String browser) throws PluginException, InterruptedException {

        Boolean ret = true;

        ret = setInitialVariables(plan.getInitialVars());
        if (ret == false) {
            return false;
        }

        foreachtest:
        for (TestCaseData testData : testDataList) {

            String testCaseName = testCasesById.get(testData.getTestCaseId()).getTestCaseName();

            try {
                log.println();
                log.printf("==== Starting execution of test case: %s\n", testCaseName);

                setVariables(testData.getTestItem().getSetVars());

                String testExecutionName = String.format("Jenkins_%s", testCasesById.get(testData.getTestCaseId()).getTestCaseName());
                ExecutedTaskResponse resp = svc.runTestCase(pd.getProjectId(), testData.getTestScriptId(),
                        testExecutionName,
                        platform, browser,
                        executionType);
                log.println("Execution started");

                // save execution id
                if (resp.getTasks().length != 1) {
                    throw new PluginException("Unexpected test execution response list length: " + resp.getTasks().length);
                }
                ExecuteTaskResponse first = resp.getTasks()[0];
                Long testExecId = first.getExecutionId().longValue();
                testData.setExecutionId(testExecId);

                boolean done = false;

                while (!done) {

                    // pause
                    try {
                        Thread.sleep(pollingIntervalMs);
                    } catch (InterruptedException e) {
                        log.println("Check execution sleep interrupted");
                        throw e;
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

    private void logTestCaseNames() {
        if (plan != null) {
            log.printf("==== Test case sequence from test case list:\n");

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

    private void printCount(int count) {
        log.printf("%d...\n", count);
    }

    private void runTestExecutions(List<TestCaseData> runTestsData, String platform,
                                   String browser) throws PluginException, ServiceException {

        for (TestCaseData t : runTestsData) {

            String testExecutionName = String.format("Jenkins_%s", testCasesById.get(t.getTestCaseId()).getTestCaseName());

            ExecutedTaskResponse resp = svc.runTestCase(pd.getProjectId(), t.getTestScriptId(),
                    testExecutionName,
                    platform, browser,
                    executionType);

            // save execution id
            if (resp.getTasks().length != 1) {
                throw new PluginException("Unexpected test execution response list length: " + resp.getTasks().length);
            }
            ExecuteTaskResponse first = resp.getTasks()[0];
            t.setExecutionId(first.getExecutionId().longValue());
        }
    }

    private Boolean checkRunTests(List<TestCaseData> runTestsData) throws ServiceException, InterruptedException {

        execSucceededId = new HashSet<>();
        execFailedId = new HashSet<>();

        Map<Long, TestCaseData> testMap = new HashMap<>();
        for (TestCaseData r : runTestsData) {
            testMap.put(r.getExecutionId(), r);
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
                throw e;
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
                TestCaseData td = testMap.get(execId);
                Long tc = td.getTestCaseId();
                log.printf("%s\n", testCasesById.get(tc).getTestCaseName());
            }
            log.println();
        }

        if (execFailedId.size() > 0) {
            log.println("==== Test execution failed for test cases:");
            for (Long execId : execFailedId) {
                TestCaseData td = testMap.get(execId);
                Long tc = td.getTestCaseId();
                log.printf("%s\n", testCasesById.get(tc).getTestCaseName());
            }
            log.println();
        }

        return execFailedId.size() == 0;
    }

}
