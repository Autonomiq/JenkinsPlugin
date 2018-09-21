package io.jenkins.plugins.autonomiq.service;

import com.google.gson.reflect.TypeToken;
import io.jenkins.plugins.autonomiq.util.AiqUtil;
import io.jenkins.plugins.autonomiq.service.types.*;
import io.jenkins.plugins.autonomiq.util.WebClient;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

public class ServiceAccess {

    private static final String authenticatePath = "%s:8005/authenticate/basic";
    private static final String listProjectsPath = "%s:8005/discovery/%d"; // accountId
    private static final String getTestCasesPath = "%s:8005/discovery/%d/%d/%d/testcases"; // userId, projectId, discoveryId
    private static final String genTestScriptsPath = "%s:8005/testScript/project/%d"; // projectId
    private static final String getTestScriptsPath = "%s:8005/testScript/userId/%d/project/%d/testCase/%d/executable"; // userId, projectId, testCaseId
    private static final String runTestCasesPath = "%s:8005/testScriptExecutions/%s/%s/run"; // userId, projectId
    private static final String getTestExecutionPath = "%s:8005/testScriptExecutions/%d/executions"; // executionId
    private static final String getUserVariablePath = "%s:8005/uservariable/find/%d/%d/%s"; // accountId, projectId, key
    private static final String saveUserVariablePath = "%s:8005/uservariable/save";

    private final String aiqUrl;
    private Long userId;
    private Integer accountId;
    private WebClient web;

    public ServiceAccess(String aiqUrl,
                         String login,
                         String password) throws ServiceException {

        this.aiqUrl = aiqUrl;

        web = new WebClient();

        AuthenticateUserBody authBody = new AuthenticateUserBody(login, password);
        String authJson = AiqUtil.gson.toJson(authBody);

        try {

            String resp = web.post(String.format(authenticatePath, aiqUrl), authJson);

            AuthenticateUserResponse r = AiqUtil.gson.fromJson(resp, AuthenticateUserResponse.class);
            userId = r.getUserId();
            accountId = r.getUserAccount();

        } catch (Exception e) {
            throw new ServiceException("Exception in authentication", e);
        }

    }

    public List<DiscoveryResponse> getProjectData() throws ServiceException {

        String url = String.format(listProjectsPath, aiqUrl, accountId);

        try {

            String resp = web.get(url);
            List<DiscoveryResponse> discoveryList = AiqUtil.gson.fromJson(resp,
                    new TypeToken<List<DiscoveryResponse>>() {
                    }.getType());

            return discoveryList;

        } catch (Exception e) {
            throw new ServiceException("Exception getting project list", e);
        }

    }

    public ExecuteTaskResponse runTestCases(Long projectId, List<Long> scriptIds,
                                            String testExecutionName,
                                            String platform, String browser,
                                            String executionType) throws ServiceException {

        String url = String.format(runTestCasesPath, aiqUrl, userId, projectId);

        ExecuteTaskRequest body = new ExecuteTaskRequest(testExecutionName, scriptIds, platform,
                browser, executionType);
        String json = AiqUtil.gson.toJson(body);

        try {

            String resp = web.post(url, json);

            ExecuteTaskResponse execResp = AiqUtil.gson.fromJson(resp, ExecuteTaskResponse.class);

            return execResp;

        } catch (Exception e) {
            throw new ServiceException("Exception running test cases", e);
        }
    }

    public ExecuteTaskResponse runTestCase(Long projectId, Long scriptId,
                                            String testExecutionName,
                                            String platform, String browser,
                                            String executionType) throws ServiceException {

        String url = String.format(runTestCasesPath, aiqUrl, userId, projectId);

        List<Long> scriptList = listForItem(scriptId);

        ExecuteTaskRequest body = new ExecuteTaskRequest(testExecutionName, scriptList, platform,
                browser, executionType);
        String json = AiqUtil.gson.toJson(body);

        try {

            String resp = web.post(url, json);

            ExecuteTaskResponse execResp = AiqUtil.gson.fromJson(resp, ExecuteTaskResponse.class);

            return execResp;

        } catch (Exception e) {
            throw new ServiceException("Exception running test case", e);
        }
    }

    private <T> List<T> listForItem(T item) {
        List<T> l = new LinkedList<>();
        l.add(item);
        return l;
    }

    public List<TestScriptResponse> startTestScripGeneration(Long projectId, Collection<Long> testCaseIds) throws ServiceException {

        String url = String.format(genTestScriptsPath, aiqUrl, projectId);

        GenerateScriptRequestBody body = new GenerateScriptRequestBody(testCaseIds, "");
        String json = AiqUtil.gson.toJson(body);

        try {

            String resp = web.post(url, json);
            List<TestScriptResponse> tsResponses = AiqUtil.gson.fromJson(resp,
                    new TypeToken<List<TestScriptResponse>>() {
                    }.getType());

            return tsResponses;

        } catch (Exception e) {
            throw new ServiceException("Exception starting test script generation", e);
        }
    }

    public List<TestCasesResponse> getTestCasesForProject(Long projectId, Long discoveryId) throws ServiceException {
        String url = String.format(getTestCasesPath, aiqUrl, userId, projectId, discoveryId);

        try {

            String resp = web.get(url);
            List<TestCasesResponse> testCaseList = AiqUtil.gson.fromJson(resp,
                    new TypeToken<List<TestCasesResponse>>() {
                    }.getType());

            return testCaseList;

        } catch (Exception e) {
            throw new ServiceException("Exception getting test cases for project " + projectId);
        }
    }


    public List<TestScriptResponse> getTestScript(Long projectId, Long testCaseId) throws ServiceException {
        String url = String.format(getTestScriptsPath, aiqUrl, userId, projectId, testCaseId);

        try {
            String resp = web.get(url);
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
            String resp = web.get(url);
            ExecuteTaskResponse execResp = AiqUtil.gson.fromJson(resp, ExecuteTaskResponse.class);
            return execResp;
        } catch (Exception e) {
            throw new ServiceException("Exception getting executed tasks by project", e);
        }
    }

    public UserVariable getUserVariable(Long projectId, String key) throws ServiceException {
        String url = String.format(getUserVariablePath, aiqUrl, accountId, projectId, key);

        try {
            String resp =  web.get(url);
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
            web.post(url, json);
        } catch (Exception e) {
            throw new ServiceException("Exception starting test script generation", e);
        }
    }

}
