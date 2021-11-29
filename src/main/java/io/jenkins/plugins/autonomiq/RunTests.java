package io.jenkins.plugins.autonomiq;

import io.jenkins.plugins.autonomiq.service.ServiceAccess;
import io.jenkins.plugins.autonomiq.util.TimeStampedLogger;

class RunTests {


    private static String executionType = "smoke";

    private ServiceAccess svc;
    private TimeStampedLogger log;
    private ProjectData pd;
    private Long pollingIntervalMs;


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
    public Boolean runTests(Boolean generateScripts,
                            Boolean runTestCases,
                            Boolean runTestSuites,
                            String platformTestCases,
                            String browserTestCases,
                            String platformTestSuites,
                            String browserTestSuites,
                            String genCaseList,
                            String runCaseList,
                            String runSuiteList,String executionMode) throws PluginException, InterruptedException {


        if (!(generateScripts || runTestCases || runTestSuites)) {
            log.println("Neither generate scripts nor run test cases nor run test suites selected, no work to do.");
            return true;
        }

        if (generateScripts) {

            RunGenScripts gen = new RunGenScripts(svc, log, pd, pollingIntervalMs);
            boolean result = gen.genScripts(genCaseList);
            if (! result) {
                return result;
            }

        }

        if (runTestCases) {

            RunTestExecutions run = new RunTestExecutions(svc, log, pd, pollingIntervalMs);
            boolean result = run.runTests(platformTestCases, browserTestCases, runCaseList);
            if (!result) {
                return result;
            }
        }

        if (runTestSuites) {

            RunSuiteExecutions run = new RunSuiteExecutions(svc, log, pd, pollingIntervalMs);
            boolean result = run.runSuites(platformTestSuites, browserTestSuites, runSuiteList,executionMode);
            if (!result) {
                return result;
            }
        }


        return true;
    }

}