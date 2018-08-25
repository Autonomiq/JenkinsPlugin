package io.jenkins.plugins.autonomiq.service;

import com.google.gson.reflect.TypeToken;
import io.jenkins.plugins.autonomiq.util.AiqUtil;
import io.jenkins.plugins.autonomiq.service.types.*;
import io.jenkins.plugins.autonomiq.util.WebClient;

import java.util.Collection;
import java.util.List;

public class ServiceAccess {

    private static final String authenticatePath = "%s:8005/authenticate/basic";
    private static final String listProjectsPath = "%s:8005/discovery/%d"; // accountId
    private static final String getTestCasesPath = "%s:8005/discovery/%d/%d/%d/testcases"; // userId, projectId, discoveryId
    private static final String genTestScriptsPath = "%s:8005/testScript/project/%d"; // projectId
    private static final String getTestScriptsPath = "%s:8005/testScript/userId/%d/project/%d/testCase/%d/executable"; // userId, projectId, testCaseId

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
            throw new ServiceException("Exception getting test script for case " + testCaseId);
        }
    }


}
