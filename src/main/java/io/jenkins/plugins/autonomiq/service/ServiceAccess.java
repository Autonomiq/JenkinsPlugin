package io.jenkins.plugins.autonomiq.service;

import com.google.gson.reflect.TypeToken;
import hudson.util.Secret;
import io.jenkins.plugins.autonomiq.util.AiqUtil;
import io.jenkins.plugins.autonomiq.service.types.*;
import io.jenkins.plugins.autonomiq.util.WebClient;
import io.jenkins.plugins.autonomiq.util.WebsocketData;

import java.util.*;

public class ServiceAccess {

    private static final String authenticatePath = "%s/platform/v1/auth";
    private static final String listProjectsPath = "%s/platform/v1/getprojects";
    private static final String getTestCasesPath = "%s/platform/v1/projects/%d/testcases"; // projectId
    private static final String getTestCasePath = "%s/platform/v1/projects/%d/testcases/%d/gettestcase"; // projectId, testcaseId
    private static final String genTestScriptsPath = "%s/platform/v1/projects/%d/generate"; // projectId
    private static final String getTestScriptsPath = "%s/platform/v1/projects/%d/testcases/%d/scripts"; // projectId, testCaseId
    private static final String runTestCasesPath = "%s/platform/v1/projects/%s/execute"; // projectId
    private static final String getTestExecutionPath = "%s/platform/testScriptExecutions/%d/executions"; // executionId
    private static final String getUserVariablePath = "%s/platform/uservariable/find/%d/%d/%s"; // accountId, projectId, key
    private static final String saveUserVariablePath = "%s/platform/uservariable/save";
    private static final String getTestCaseInfoPath = "%s/platform/testCases/getTestCaseInfo/%d/%d"; // testCaseId, type
    private static final String getTestSuitesPath = "%s/platform/testSuites/%d/%d/getTestSuites"; // accountId, projectId
    private static final String executeTestSuitePath = "%s/platform/v1/testsuite/%s/execute"; // testSuiteId
    private static final String getJobPath = "%s/platform/v1/jobs/%d/get_job"; // jobId

    private static final String websocketPath = "%s/ws?accountId=%d";

    private final String aiqUrl;
    private Integer accountId;
    private WebClient web;
    private String token;

    public ServiceAccess(String proxyHost, String proxyPort, String proxyUser, Secret proxyPassword, String aiqUrl, String login, Secret password) throws ServiceException {
    	this.aiqUrl = aiqUrl;
        web = new WebClient(proxyHost, proxyPort, proxyUser, proxyPassword);

        authenticate(aiqUrl, login, password);

    }

    public ServiceAccess(String aiqUrl,
                         String login,
                         Secret password) throws ServiceException {
        this.aiqUrl = aiqUrl;
        web = new WebClient();

        authenticate(aiqUrl, login, password);

    }
    
    private void authenticate(String aiqUrl,
            String login,
            Secret password) throws ServiceException {
        try {

            String resp = web.post(String.format(authenticatePath, aiqUrl), constructAuthJson(login, password), null);

            AuthenticateUserResponse r = AiqUtil.gson.fromJson(resp, AuthenticateUserResponse.class);
            accountId = r.getUserAccount();
            token = r.getToken();

        } catch (Exception e) {
            throw new ServiceException("Exception in authentication", e);
        }
    }

    public TestCaseInfo getTestCaseInfo(Long testCaseId, TestCaseInfoType type) throws ServiceException {

        String url = String.format(getTestCaseInfoPath, aiqUrl, testCaseId, type.getVal());

        try {
            String resp = web.get(url, token);
            TestCaseInfo caseInfo = AiqUtil.gson.fromJson(resp, TestCaseInfo.class);
            return caseInfo;
        } catch (Exception e) {
            throw new ServiceException("Exception getting test case info ", e);
        }
    }

    public Collection<DiscoveryResponse> getProjectData() throws ServiceException {

        String url = String.format(listProjectsPath, aiqUrl);

        try {

            String resp = web.get(url, token);
            List<DiscoveryResponse> discoveryList = AiqUtil.gson.fromJson(resp,
                    new TypeToken<List<DiscoveryResponse>>() {
                    }.getType());

            // build sorted map by lower case project name
            Map<String, DiscoveryResponse> map = new TreeMap<>();
            for (DiscoveryResponse d : discoveryList) {
                map.put(d.getProjectName().toLowerCase(), d);
            }

            // return the sorted values
            return map.values();

        } catch (Exception e) {
            throw new ServiceException("Exception getting project list", e);
        }

    }

    public ExecuteTaskResponse runTestCases(Long projectId, List<Long> scriptIds,
                                            String testExecutionName,
                                            String platform, String browser,
                                            String executionType) throws ServiceException {

        String sessionId = createSession();

        String url = String.format(runTestCasesPath, aiqUrl, projectId);

        List<PlatformBrowserDetails> browserDetails = new LinkedList<>();
        browserDetails.add(new PlatformBrowserDetails(browser, null, platform, null, null, null, null));
        ExecuteTaskRequest body = new ExecuteTaskRequest(sessionId, testExecutionName, scriptIds, executionType,
                browserDetails, false, null, null);
        String json = AiqUtil.gson.toJson(body);

        try {

            String resp = web.post(url, json, token);

            ExecuteTaskResponse execResp = AiqUtil.gson.fromJson(resp, ExecuteTaskResponse.class);

            return execResp;

        } catch (Exception e) {
            throw new ServiceException("Exception running test cases", e);
        }
    }

    public ExecutedTaskResponse runTestCase(Long projectId, Long scriptId,
                                            String testExecutionName,
                                            String platform, String browser,
                                            String executionType) throws ServiceException {

        String sessionId = createSession();

        String url = String.format(runTestCasesPath, aiqUrl, projectId);

        List<Long> scriptList = listForItem(scriptId);

        List<PlatformBrowserDetails> browserDetails = new LinkedList<>();
        browserDetails.add(new PlatformBrowserDetails(browser, null, platform, null, null, null, null));
        ExecuteTaskRequest body = new ExecuteTaskRequest(sessionId, testExecutionName, scriptList, executionType,
                browserDetails, false, null, null);

        String json = AiqUtil.gson.toJson(body);

        try {

            String resp = web.post(url, json, token);

            ExecutedTaskResponse respExec = AiqUtil.gson.fromJson(resp, ExecutedTaskResponse.class);

            return respExec;

        } catch (Exception e) {
            throw new ServiceException("Exception running test case", e);
        }
    }

    public ExecuteSuiteResponse runTestSuite(Long testSuiteId,
                                             String platform, String browser,
                                             String browserVersion, String executionType,
                                             String executionMode, boolean isRemoteDriver,
                                             String remoteDriverUrl,
                                             Map<Long, String> caseSessionMap) throws ServiceException {

        String url = String.format(executeTestSuitePath, aiqUrl, testSuiteId);

        List<PlatformBrowserDetails> details = new LinkedList<>();
        details.add(new PlatformBrowserDetails(browser, null, platform, null, null, null, null));
        ExecuteTestSuiteRequest body = new ExecuteTestSuiteRequest(
                details, executionType, executionMode,
                isRemoteDriver, remoteDriverUrl, caseSessionMap);

        String json = AiqUtil.gson.toJson(body);

        try {

            String resp = web.post(url, json, token);

            ExecuteSuiteResponse respExec = AiqUtil.gson.fromJson(resp, ExecuteSuiteResponse.class);

            return respExec;

        } catch (Exception e) {
            throw new ServiceException(String.format("Exception running test suite id %d", testSuiteId), e);
        }
    }

    private <T> List<T> listForItem(T item) {
        List<T> l = new LinkedList<>();
        l.add(item);
        return l;
    }

    public String createSession() throws ServiceException {

        String wsUrl =  String.format(websocketPath, aiqUrl, accountId);
        String sessionId = null;

        try {

            WebsocketData wsd = web.createWebsocket(wsUrl);

            SessionCreateMsg createMsg = new SessionCreateMsg(token);
            String createMsgJson = AiqUtil.gson.toJson(createMsg);

            BasicTransportMessage transMsg = new BasicTransportMessage("",
                    TransportMsgType.MSG_CREATE_SESSION.ordinal(),
                    createMsgJson);
            String transMsgJson = AiqUtil.gson.toJson(transMsg);

            wsd.getSocket().send(transMsgJson);

            for (Integer i = 0; i < 10; i++) {
                Thread.sleep(500);
                String msg = wsd.getListener().getMsg();
                if (msg != null) {
                    BasicTransportMessage recMsg = AiqUtil.gson.fromJson(msg, BasicTransportMessage.class);
                    if (recMsg.getMsgType() == TransportMsgType.MSG_CREATE_SESSION.ordinal()) {
                        sessionId = recMsg.getSessionId();
                        break;
                    }
                }
            }

        } catch (Exception e) {
            throw new ServiceException("Exception getting session id for script generation", e);
        }

        if (sessionId == null) {
            throw new ServiceException("Did not receive session id from websocket");
        }

        return sessionId;

    }

    public List<TestScriptResponse> startTestScripGeneration(Long projectId, Collection<Long> testCaseIds) throws ServiceException {

        String sessionId = createSession();

        String genUrl = String.format(genTestScriptsPath, aiqUrl, projectId);

        GenerateScriptRequestBody body = new GenerateScriptRequestBody(testCaseIds, sessionId, "",
                false, true);
        String json = AiqUtil.gson.toJson(body);

        try {

            String resp = web.post(genUrl, json, token);
            List<TestScriptResponse> tsResponses = AiqUtil.gson.fromJson(resp,
                    new TypeToken<List<TestScriptResponse>>() {
                    }.getType());

            return tsResponses;

        } catch (Exception e) {
            throw new ServiceException("Exception starting test script generation", e);
        }
    }

    public List<TestCasesResponse> getTestCasesForProject(Long projectId) throws ServiceException {
        String url = String.format(getTestCasesPath, aiqUrl, projectId);

        try {

            String resp = web.get(url, token);
            List<TestCasesResponse> testCaseList = AiqUtil.gson.fromJson(resp,
                    new TypeToken<List<TestCasesResponse>>() {
                    }.getType());

            return testCaseList;

        } catch (Exception e) {
            throw new ServiceException("Exception getting test cases for project " + projectId, e);
        }
    }

    public List<GetTestSuitesResponse> getTestSuitesForProject(Long projectId) throws ServiceException {
        String url = String.format(getTestSuitesPath, aiqUrl, accountId, projectId);

        try {

            String resp = web.get(url, token);
            List<GetTestSuitesResponse> testSuiteList = AiqUtil.gson.fromJson(resp,
                    new TypeToken<List<GetTestSuitesResponse>>() {
                    }.getType());

            return testSuiteList;

        } catch (Exception e) {
            throw new ServiceException("Exception getting test suites for project " + projectId, e);
        }

    }

    public TestCasesResponse getTestCase(Long projectId, Long testcaseId) throws ServiceException {
        String url = String.format(getTestCasePath, aiqUrl, projectId, testcaseId);

        try {

            String resp = web.get(url, token);
            TestCasesResponse testCaseResp = AiqUtil.gson.fromJson(resp,
                    TestCasesResponse.class);

            return testCaseResp;

        } catch (Exception e) {
            throw new ServiceException("Exception getting test case " + testcaseId, e);
        }
    }

    public List<TestScriptResponse> getTestScript(Long projectId, Long testCaseId) throws ServiceException {
        String url = String.format(getTestScriptsPath, aiqUrl, projectId, testCaseId);

        try {
            String resp = web.get(url, token);
            List<TestScriptResponse> testScriptList = AiqUtil.gson.fromJson(resp,
                    new TypeToken<List<TestScriptResponse>>() {
                    }.getType());
            return testScriptList;
        } catch (Exception e) {
            throw new ServiceException("Exception getting test script for case " + testCaseId, e);
        }
    }

    public ExecuteTaskResponse getExecutedTask(Long executionId) throws ServiceException {
        String url = String.format(getTestExecutionPath, aiqUrl, executionId);

        try {
            String resp = web.get(url, token);
            ExecuteTaskResponse execResp = AiqUtil.gson.fromJson(resp, ExecuteTaskResponse.class);
            return execResp;
        } catch (Exception e) {
            throw new ServiceException("Exception getting executed task by id", e);
        }
    }

    public Job getJob(Long jobId) throws ServiceException {
        String url = String.format(getJobPath, aiqUrl, jobId);

        try {
            String resp = web.get(url, token);
            Job job = AiqUtil.gson.fromJson(resp, Job.class);
            return job;
        } catch (Exception e) {
            throw new ServiceException("Exception getting job", e);
        }
    }

    public UserVariable getUserVariable(Long projectId, String key) throws ServiceException {
        String url = String.format(getUserVariablePath, aiqUrl, accountId, projectId, key);

        try {
            String resp =  web.get(url, token);
            UserVariable var = AiqUtil.gson.fromJson(resp, UserVariable.class);
            return var;
        } catch (Exception e) {
            throw new ServiceException("Exception getting user variable", e);
        }
    }

    public void saveUserVariable(Long projectId, String key, String value) throws ServiceException {
        String url = String.format(saveUserVariablePath, aiqUrl);

        String json = AiqUtil.gson.toJson(new UserVariable(accountId, projectId, key, value));

        try {
            web.post(url, json, token);
        } catch (Exception e) {
            throw new ServiceException("Exception starting test script generation", e);
        }
    }
    
    private String constructAuthJson(String login, Secret password) {
    	return  "{" + "\"" +  "username" + "\""+ ":" + "\"" + login +"\""+ ","+
    "\""+"password"+"\""+ ":" +"\""+ Secret.toString(password)+"\"" +"}";
    }

}
