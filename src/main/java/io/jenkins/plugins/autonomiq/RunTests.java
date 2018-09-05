package io.jenkins.plugins.autonomiq;

import io.jenkins.plugins.autonomiq.service.ServiceAccess;
import io.jenkins.plugins.autonomiq.service.ServiceException;
import io.jenkins.plugins.autonomiq.service.types.ExecuteTaskResponse;
import io.jenkins.plugins.autonomiq.service.types.ExecutedTaskResponse;
import io.jenkins.plugins.autonomiq.service.types.TestCasesResponse;
import io.jenkins.plugins.autonomiq.service.types.TestScriptResponse;
import io.jenkins.plugins.autonomiq.util.AiqUtil;
import io.jenkins.plugins.autonomiq.util.TimeStampedLogger;

import javax.xml.ws.Service;
import java.io.PrintStream;
import java.util.*;

public class RunTests {

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
            } else {
                log.println("Not all test scripts generations succeeded, ending run.");
                return false;
            }

        }

        if (runTestCases) {

            List<Long> testScriptIds = new ArrayList<>();

            if (generateScripts) {
                // use new generated scripts
                for (TestScriptResponse r : testScriptByTestCaseId.values()) {
                    testScriptIds.add(r.getTestScriptid());
                }
            } else {
                for (TestCasesResponse r : testCasesById.values()) {
                    TestScriptResponse[] scripts = r.getTestScripts();
                    TestScriptResponse s = scripts[scripts.length - 1];
                    testScriptIds.add(s.getTestScriptid());
                }
            }

            String testExecutionName = "Jenkins run project " + pd.getProjectName();
            log.printf("==== Starting test execution named '%s'", testExecutionName);
            Integer executionId;
            try {
                executionId = runTestExecutions(testScriptIds, testExecutionName, platform, browser);
            } catch (ServiceException e) {
                log.println("Exception running test executions.");
                log.println(AiqUtil.getExceptionTrace(e));
                return false;
            }

            Boolean runCheck;
            try {
                runCheck = checkRunTests(pd.getProjectId(), executionId);
            } catch (ServiceException e) {
                log.println("Exception checking test executions.");
                log.println(AiqUtil.getExceptionTrace(e));
                return false;
            }

            return runCheck;

        }

//        log.println();
//        log.printf("Running all test cases from project '%s'\n", pd.getProjectName());


        return true;
    }

    private void logTestCaseNames() {
        log.printf("==== Found these %s test cases:\n", testCasesById.size());
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

        log.println();
        log.printf("==== Starting script generation for %d test cases.\n", testCasesById.size());

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
                case FAILED:
                    fail.add(r);
                default:
            }

//            log.printf("Test case '%s' script gen status '%s'\n", testCasesById.get(r.getTestCaseId()).getTestCaseName(),
//                                            r.getTestScriptGenerationStatus());
//            if (r.getTestScriptDownloadLink() != null) {
//                log.printf("  Script download link: %s\n", r.getTestScriptDownloadLink());
//            }
        }

        log.println("Test script generation passed for test cases:");
        for (TestScriptResponse r : pass) {
            log.printf("%s\n", testCasesById.get(r.getTestCaseId()).getTestCaseName());
        }

        log.println();

        log.println("Test script generation failed for test cases:");
        for (TestScriptResponse r : fail) {
            log.printf("%s\n", testCasesById.get(r.getTestCaseId()).getTestCaseName());
        }

        log.println();
        return gensFailedCaseId.size() == 0;

    }

    private Integer runTestExecutions(List<Long> scriptIds, String testExecutionName, String platform,
                                      String browser) throws ServiceException {

        ExecuteTaskResponse execResp = svc.runTestCases(pd.getProjectId(), scriptIds,
                testExecutionName, platform, browser, executionType);

        Integer executionId = execResp.getExecutionId();

        return executionId;

    }

    private Boolean checkRunTests(Long projectId, Integer executionId) throws ServiceException {

        boolean done = false;

        while (!done) {

            // pause
            try {
                Thread.sleep(pollingIntervalMs);
            } catch (InterruptedException e) {
                log.println("Check run tests sleep interrupted");
            }

            log.println();
            log.printf("==== Checking test execution\n");

            boolean foundOngoing = false;

            // search ongoing tasks
            ExecutedTaskResponse resp = svc.getExecutedTask(projectId);
            ExecuteTaskResponse[] tasks = resp.getTasks();
            if (tasks.length != 1) {
                log.printf("Bad number of tasks in response %d", tasks.length);
            }
            ExecuteTaskResponse r = tasks[0];

            ExecStatus stat = ExecStatus.getEnumForName(r.getExecutionStatus());

            switch (stat) {
                case INPROGRESS:
                    log.println("Test execution still in progress");
                    break;
                case SUCCESS:
                    log.println("Test execution succeeded");
                    return true;
                case ERROR:
                    log.println("Test execution failed");
                    return false;
            }

        }

        return null;

    }

}
