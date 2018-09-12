package io.jenkins.plugins.autonomiq;

import io.jenkins.plugins.autonomiq.service.ServiceAccess;
import io.jenkins.plugins.autonomiq.service.ServiceException;
import io.jenkins.plugins.autonomiq.service.types.ExecuteTaskResponse;
import io.jenkins.plugins.autonomiq.service.types.TestCasesResponse;
import io.jenkins.plugins.autonomiq.service.types.TestScriptResponse;
import io.jenkins.plugins.autonomiq.util.AiqUtil;
import io.jenkins.plugins.autonomiq.util.TimeStampedLogger;

import java.util.*;

public class RunTests {


    class RunTestData {
        private Long testCaseId;
        private Long testScriptId;
        private Long executionId;

        public RunTestData(Long testCaseId, Long testScriptId) {
            this.testCaseId = testCaseId;
            this.testScriptId = testScriptId;
        }

        public Long getTestCaseId() {
            return testCaseId;
        }

        public Long getTestScriptId() {
            return testScriptId;
        }

        public Long getExecutionId() {
            return executionId;
        }

        public void setExecutionId(Long executionId) {
            this.executionId = executionId;
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
    //private Map<String, TestCasesResponse> testCasesByName;
    private Map<Long, TestScriptResponse> testScriptByTestCaseId;
    private Set<Long> gensSucceededCaseId;
    private Set<Long> gensFailedCaseId;
    private Map<Long, TestScriptResponse> scriptGenResponses;
    private Set<Long> execSucceededId;
    private Set<Long> execFailedId;

    public RunTests(ServiceAccess svc,
                    TimeStampedLogger log,
                    ProjectData pd,
                    Long pollingIntervalMs) {
        this.svc = svc;
        this.log = log;
        this.pd = pd;
        this.pollingIntervalMs = pollingIntervalMs;
    }

    /**
     * @param generateScripts
     * @return returns true if all tests execute successfully
     */
    public Boolean runAllTestsForProject(Boolean generateScripts, Boolean runTestCases, String platform, String browser) {

        if (!(generateScripts || runTestCases)) {
            log.println("Neither generate scripts nor run test cases selected, no work to do.");
            return true;
        }

        try {
            getAllTestCases(pd.getProjectId(), pd.getDiscoveryId());
        } catch (ServiceException e) {
            log.println(AiqUtil.getExceptionTrace(e));
            return false;
        }

        logTestCaseNames();

        Boolean allGensSuccessful;

        if (generateScripts) {
            log.println();
            log.printf("==== Starting script generations for project %s\n", pd.getProjectName());
            log.println();

            try {
                startScriptGenerations();
            } catch (ServiceException e) {
                log.println(AiqUtil.getExceptionTrace(e));
                return false;
            }


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

        if (runTestCases) {

            List<RunTestData> runTestsData = new ArrayList<>();

            if (generateScripts) {
                // use new generated scripts
                for (TestScriptResponse r : testScriptByTestCaseId.values()) {
                    runTestsData.add(new RunTestData(r.getTestCaseId(), r.getTestScriptid()));
                }
            } else {
                for (TestCasesResponse r : testCasesById.values()) {
                    TestScriptResponse[] scripts = r.getTestScripts();
                    TestScriptResponse s = scripts[scripts.length - 1];
                    runTestsData.add(new RunTestData(r.getTestCaseId(), s.getTestScriptid()));
                }
            }

            log.println();
            log.printf("==== Starting test executions for project %s\n", pd.getProjectName());
            log.println();

            try {
                // runTestsData gets updated with execution ids
                runTestExecutions(runTestsData, platform, browser);
            } catch (ServiceException e) {
                log.println("Exception running test executions.");
                log.println(AiqUtil.getExceptionTrace(e));
                return false;
            }

            Boolean runCheck;
            try {
                runCheck = checkRunTests(pd.getProjectId(), runTestsData);
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

        return true;
    }

    private void logTestCaseNames() {
        log.printf("==== Found these %s test cases in project %s:\n", testCasesById.size(), pd.getProjectName());
        for (TestCasesResponse r : testCasesById.values()) {
            log.println(r.getTestCaseName());
        }
    }

    private void getAllTestCases(Long projectId, Long discoveryId) throws ServiceException {
        testCasesById = new HashMap<>();
        //testCasesByName = new TreeMap<>();

        List<TestCasesResponse> tc = svc.getTestCasesForProject(projectId, discoveryId);
        for (TestCasesResponse t : tc) {
            testCasesById.put(t.getTestCaseId(), t);
            //testCasesByName.put(t.getTestCaseName(), t);
        }
    }

    private void startScriptGenerations() throws ServiceException {
        testScriptByTestCaseId = new HashMap<>();

        List<TestScriptResponse> tsr = svc.startTestScripGeneration(pd.getProjectId(), testCasesById.keySet());
        for (TestScriptResponse t : tsr) {
            testScriptByTestCaseId.put(t.getTestCaseId(), t);
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

                String testCaseName = testCasesById.get(testCaseId).getTestCaseName();

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

    private void runTestExecutions(List<RunTestData> runTestsData, String platform,
                                      String browser) throws ServiceException {

        for (RunTestData t : runTestsData) {

            String testExecutionName = String.format("Jenkins_%s", testCasesById.get(t.getTestCaseId()).getTestCaseName());

            ExecuteTaskResponse resp = svc.runTestCase(pd.getProjectId(), t.getTestScriptId(),
                    testExecutionName,
                    platform, browser,
                    executionType);

            // save execution id
            t.setExecutionId(resp.getExecutionId().longValue());
        }
    }

    private Boolean checkRunTests(Long projectId, List<RunTestData> runTestsData) throws ServiceException {

        execSucceededId = new HashSet<>();
        execFailedId = new HashSet<>();

        Map<Long, RunTestData> testMap = new HashMap<>();
        for (RunTestData r : runTestsData) {
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
                log.println("Check scripts generation sleep interrupted");
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
                RunTestData td = testMap.get(execId);
                Long tc = td.getTestCaseId();
                log.printf("%s\n", testCasesById.get(tc).getTestCaseName());
            }
            log.println();
        }

        if (execFailedId.size() > 0) {
            log.println("==== Test execution failed for test cases:");
            for (Long execId : execFailedId) {
                RunTestData td = testMap.get(execId);
                Long tc = td.getTestCaseId();
                log.printf("%s\n", testCasesById.get(tc).getTestCaseName());
            }
            log.println();
        }

        return execFailedId.size() == 0;
    }

}
