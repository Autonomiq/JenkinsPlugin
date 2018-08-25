package io.jenkins.plugins.autonomiq;

import io.jenkins.plugins.autonomiq.service.ServiceAccess;
import io.jenkins.plugins.autonomiq.service.ServiceException;
import io.jenkins.plugins.autonomiq.service.types.TestCasesResponse;
import io.jenkins.plugins.autonomiq.service.types.TestScriptResponse;
import io.jenkins.plugins.autonomiq.util.AiqUtil;

import java.io.PrintStream;
import java.util.*;

public class RunTests {

    enum Progress {
        INPROGRESS,
        SUCCESS,
        ERROR,
    }

    private ServiceAccess svc;
    private PrintStream log;
    private ProjectData pd;
    private Long pollingIntervalMs;

    private Map<Long, TestCasesResponse> testCasesById;
    private Map<String, TestCasesResponse> testCasesByName;
    private Map<Long, TestScriptResponse> testScriptByTestCaseId;

    public RunTests(ServiceAccess svc,
                    PrintStream log,
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
    public Boolean runAllTestsForProject(Boolean generateScripts) {

        if (generateScripts) {
            log.printf("Generating all test scripts from project '%s'\n", pd.getProjectName());
        }

        log.printf("Running all test cases from project '%s'\n", pd.getProjectName());

        try {
            getAllTestCases(pd.getProjectId(), pd.getDiscoveryId());
        } catch (ServiceException e) {
            log.println(AiqUtil.getExceptionTrace(e));
            return false;
        }

        logTestCaseNames();

        if (generateScripts) {
            try {
                startScriptGenerations();
            } catch (ServiceException e) {
                log.println(AiqUtil.getExceptionTrace(e));
                return false;
            }

            Set<Long> scriptGenSucessfullCaseIds;
            try {
                scriptGenSucessfullCaseIds = checkScriptGenerations();
            } catch (ServiceException e) {
                log.println(AiqUtil.getExceptionTrace(e));
                return false;
            }

        }

        return true;
    }

    private void logTestCaseNames() {
        log.println("==== Found these test cases:");
        for (String name : testCasesByName.keySet()) {
            log.println(name);
        }
        log.println();
    }

    private void getAllTestCases(Long projectId, Long discoveryId) throws ServiceException {
        testCasesById = new HashMap<>();
        testCasesByName = new TreeMap<>();

        List<TestCasesResponse> tc = svc.getTestCasesForProject(projectId, discoveryId);
        for (TestCasesResponse t : tc) {
            testCasesById.put(t.getTestCaseId(), t);
            testCasesByName.put(t.getTestCaseName(), t);
        }
    }

    private void startScriptGenerations() throws ServiceException {
        testScriptByTestCaseId = new HashMap<>();

        log.printf("Starting script generation for %d test cases.\n", testCasesById.size());

        List<TestScriptResponse> tsr = svc.startTestScripGeneration(pd.getProjectId(), testCasesById.keySet());
        for (TestScriptResponse t : tsr) {
            testScriptByTestCaseId.put(t.getTestCaseId(), t);
        }
    }

    private Set<Long> checkScriptGenerations() throws ServiceException {
        // copy ids
        Set<Long> testCasesInProgress = new HashSet<>(testScriptByTestCaseId.keySet());
        Set<Long> gensSucceededCaseId = new HashSet<>();

        while (testCasesInProgress.size() > 0) {

            // pause
            try {
                Thread.sleep(pollingIntervalMs);
            } catch (InterruptedException e) {
                log.println("Check scripts generation sleep interrupted");
            }

            log.println();
            log.printf("==== Checking %d test cases still in progress\n", testCasesInProgress.size());

            Iterator<Long> i = testCasesInProgress.iterator();
            while (i.hasNext()) {

                Long testCaseId = i.next();
                List<TestScriptResponse> scripts = svc.getTestScript(pd.getProjectId(), testCaseId);

                TestScriptResponse scriptStart = testScriptByTestCaseId.get(testCaseId);

                String testCaseName = testCasesById.get(testCaseId).getTestCaseName();

                for (TestScriptResponse script : scripts) {
                    if (script.getTestScriptid().equals(scriptStart.getTestScriptid())) {

                        if (Progress.INPROGRESS.name().equals(script.getTestScriptGenerationStatus())) {
                            log.printf("Script generation for test case %s still in progress\n",
                                    testCaseName);

                        } else if (Progress.ERROR.name().equals(script.getTestScriptGenerationStatus())) {

                            log.printf("Script generation for test case %s failed\n", testCaseName);
                            i.remove();

                        } else { // must be SUCCESS

                            log.printf("Script generation for test case %s succeeded\n", testCaseName);
                            gensSucceededCaseId.add(testCaseId);
                            i.remove();

                        }
                    }
                }
            }

        }

        log.println();
        return gensSucceededCaseId;

    }


}
