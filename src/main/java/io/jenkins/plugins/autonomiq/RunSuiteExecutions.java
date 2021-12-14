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

class RunSuiteExecutions {


    private static String executionType = "smoke";

    private ServiceAccess svc;
    private TimeStampedLogger log;
    private ProjectData pd;
    private Long pollingIntervalMs;

    private Map<Long, GetTestSuitesResponse> testSuitesById;
    private Map<String, GetTestSuitesResponse> testSuitesByName;
    private List<TestSuiteData> testDataList;
    private Map<Long, TestSuiteData> testDataByTestSuiteId;
    TestPlan plan;


    public RunSuiteExecutions(ServiceAccess svc,
                              TimeStampedLogger log,
                              ProjectData pd,
                              Long pollingIntervalMs) {
        this.svc = svc;
        this.log = log;
        this.pd = pd;
        this.pollingIntervalMs = pollingIntervalMs;

    }

    public Boolean runSuites(String platform,
                             String browser,
                             String runSuiteList,String executionMode,String environmentType) throws PluginException, InterruptedException {


        AiqUtil.ItemListFromString itemsObj = AiqUtil.getItemListFromString(runSuiteList);

        if (itemsObj.getError() != null) {
            log.printf("Error getting item list from run test suite list '%s'\n", itemsObj.getError());
            return false;

        } else if (itemsObj.getItemList().size() > 0) {
            plan = testCaseListToPlan(itemsObj.getItemList());
        }


        try {
            getTestSuites(plan, pd.getProjectId());
        } catch (ServiceException e) {
            log.println(AiqUtil.getExceptionTrace(e));
            return false;
        }

        logTestSuiteNames();

        return handleSuiteExecutions(platform, browser,executionMode,environmentType);

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

    private Boolean handleSuiteExecutions(String platform, String browser,String executionMode,String environmentType) throws PluginException, InterruptedException {

        log.println();
        log.printf("==== Starting suite executions for project %s\n", pd.getProjectName());
        log.println();

        try {
            // runTestsData gets updated with execution ids
            runSuiteExecutions(testDataList, platform, browser,executionMode,environmentType);
        } catch (ServiceException e) {
            log.println("Exception running test executions.");
            log.println(AiqUtil.getExceptionTrace(e));
            return false;
        }

        try {
            checkRunSuites(testDataList);
        } catch (ServiceException e) {
            log.println("Exception checking test executions.");
            log.println(AiqUtil.getExceptionTrace(e));
            return false;
        }
        return true;

    }

    private void logTestSuiteNames() {
        if (plan != null) {
            log.printf("==== Test suites from test suite list:\n");

        } else {
            log.printf("==== Found these %s test suites in project %s:\n", testSuitesById.size(), pd.getProjectName());

        }
        for (TestSuiteData td : testDataList) {
            log.println(testSuitesById.get(td.getTestSuiteId()).getTestSuiteName());
        }
    }

    private void getTestSuites(TestPlan plan, Long projectId) throws ServiceException {
        testSuitesById = new HashMap<>();
        testSuitesByName = new TreeMap<>();

        List<GetTestSuitesResponse> tc = svc.getTestSuitesForProject(projectId);
        for (GetTestSuitesResponse t : tc) {
            testSuitesById.put(t.getTestSuiteId(), t);
            testSuitesByName.put(t.getTestSuiteName(), t);
        }

        testDataList = new LinkedList<>();
        testDataByTestSuiteId = new HashMap<>();

        if (plan == null) {

            for (Long testSuiteId : testSuitesById.keySet()) {
                TestSuiteData testData = new TestSuiteData();
                testData.setTestSuiteId(testSuiteId);
                testDataList.add(testData);
                testDataByTestSuiteId.put(testSuiteId, testData);
            }
        } else {

            testDataList = new LinkedList<>();
            for (TestItem item : plan.getSeq()) {
                GetTestSuitesResponse testSuite = testSuitesByName.get(item.getCaseName());
                if (testSuite == null) {
                    throw new ServiceException(String.format("No test suite found for suite name from list: %s", item.getCaseName()));
                }
                TestSuiteData testData = new TestSuiteData();
                testData.setTestSuiteId(testSuite.getTestSuiteId());
                testData.setTestItem(item);
                testDataList.add(testData);
                testDataByTestSuiteId.put(testSuite.getTestSuiteId(), testData);
            }
        }

    }

    private void printCount(int count) {
        log.printf("%d...\n", count);
    }

    private void runSuiteExecutions(List<TestSuiteData> runTestsData, String platform,
                                   String browser,String executionMode,String environmentType) throws PluginException, ServiceException {

        for (TestSuiteData t : runTestsData) {

            ExecuteSuiteResponse resp = svc.runTestSuite(t.getTestSuiteId(),
                    platform,  browser,
                    null, executionType,
                    executionMode, false,
                    null,
                    new HashMap<>(),environmentType);

            //since we are running in only 1 browser platform combination we need to set only 1 job id here.
            t.setJobId(resp.getJob_id()[0]);
        }
    }

    private Boolean checkRunSuites(List<TestSuiteData> runSuitesData) throws ServiceException, InterruptedException {

        Map<Long, TestSuiteData> testMap = new HashMap<>();
        for (TestSuiteData r : runSuitesData) {
            testMap.put(r.getJobId(), r);
        }

        Set<Long> testSuitesInProgress = new HashSet<>(testMap.keySet());

        int lastCount = testSuitesInProgress.size();
        log.printf("Number of test suites still in progress:\n");
        printCount(lastCount);

        while (testSuitesInProgress.size() > 0) {

            // pause
            try {
                Thread.sleep(pollingIntervalMs);
            } catch (InterruptedException e) {
                log.println("Check test suites sleep interrupted");
                throw e;
            }


            Iterator<Long> i = testSuitesInProgress.iterator();
            while (i.hasNext()) {

                Long jobId = i.next();

                Job r = svc.getJob(jobId);

                if (r.getCompletedExecutions() > 0) {
                    i.remove();
                }

            }

            int newCount = testSuitesInProgress.size();
            if (newCount != lastCount) {
                printCount(newCount);
                lastCount = newCount;
            }
        }

        log.println();

        log.println("All test suite executions completed");

        log.println();

        return true;
    }

}
