package io.jenkins.plugins.autonomiq;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import okhttp3.*;

import javax.xml.ws.Service;
import java.io.*;
import java.sql.Time;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ServiceAccess {

    private final String authenticatePath = ":8005/authenticate/basic";
    private final String listProjectsPath = "/discovery/%s"; // accountId

    private static final MediaType JSON = MediaType.get("application/json; charset=utf-8");

    private final OkHttpClient client;
    private final Gson gson;
    private final PrintStream log;
    private final String aiqUrl;
    private Long userId;
    private Integer accountId;

    public ServiceAccess(PrintStream log,
                         String aiqUrl,
                         String login,
                         String password) throws ServiceException {

        this.log = log;
        this.aiqUrl = aiqUrl;
        gson = new Gson();

        client = new OkHttpClient();

        AuthenticateUserBody authBody = new AuthenticateUserBody(login, password);
        String authJson = gson.toJson(authBody);

        try {

            String resp = post(aiqUrl + authenticatePath, authJson);

            AuthenticateUserResponse r = gson.fromJson(resp, AuthenticateUserResponse.class);
            userId = r.getUserId();
            accountId = r.getUserAccount();

        } catch (Exception e) {
            throw new ServiceException("Exception in authentication", e);
        }

    }

    public List<String> getProjectNames() throws ServiceException {

        String url = aiqUrl + String.format(listProjectsPath, accountId);

        try {
            String resp = get(url);

            List<DiscoveryResponse> discoveryList = gson.fromJson(resp, new TypeToken<List<DiscoveryResponse>>(){}.getType());

            List<String> ret = new ArrayList<>(discoveryList.size());

            for (DiscoveryResponse discovery : discoveryList) {
                ret.add(discovery.projectName);
            }

            return ret;

        } catch (Exception e) {
            throw new ServiceException("Exception getting project list", e);
        }

    }

    String get(String url) throws ServiceException {
        Request request = new Request.Builder()
                .url(url)
                .build();

        try (Response response = client.newCall(request).execute()) {
            int code = response.code();
            if (code != 200) {
                throw new ServiceException(String.format("On request to %s got response code %d with message '%s'",
                        url, code, response.message()));
            }
            return response.body().string();
        } catch (Exception e) {
            throw new ServiceException("Exception on GET to " + url, e);
        }
    }

    private String post(String url, String json) throws ServiceException {
        RequestBody body = RequestBody.create(JSON, json);
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();
        try (Response response = client.newCall(request).execute()) {
            int code = response.code();
            if (code != 200) {
                throw new ServiceException(String.format("On request to %s got response code %d with message '%s'",
                        url, code, response.message()));
            }
            return response.body().string();
        } catch (Exception e) {
            throw new ServiceException("Exception on POST to " + url, e);
        }
    }

    class AuthenticateUserBody {
        private String username;
        private String password;

        public AuthenticateUserBody(String username, String password) {
            this.username = username;
            this.password = password;
        }

        public String getUsername() {
            return username;
        }

        public String getPassword() {
            return password;
        }
    }

    class AuthenticateUserResponse {
        private String token;
        private String role;
        private String name;
        private String email;
        private Long userId;
        private Integer userAccount;

        public AuthenticateUserResponse(String token, String role, String name, String email, Long userId, Integer userAccount) {
            this.token = token;
            this.role = role;
            this.name = name;
            this.email = email;
            this.userId = userId;
            this.userAccount = userAccount;
        }

        public String getToken() {
            return token;
        }

        public String getRole() {
            return role;
        }

        public String getName() {
            return name;
        }

        public String getEmail() {
            return email;
        }

        public Long getUserId() {
            return userId;
        }

        public Integer getUserAccount() {
            return userAccount;
        }
    }

    class AutInformation {
        private Long discoveryId;
        private String appUrl;
        private String appName;
        private String appLoginUsername;
        private String discoveryStatus;
        private Integer testData;
        private Integer testScripts;
        private Integer testCases;

        public AutInformation(Long discoveryId, String appUrl, String appName,
                              String appLoginUsername, String discoveryStatus,
                              Integer testData, Integer testScripts, Integer testCases) {
            this.discoveryId = discoveryId;
            this.appUrl = appUrl;
            this.appName = appName;
            this.appLoginUsername = appLoginUsername;
            this.discoveryStatus = discoveryStatus;
            this.testData = testData;
            this.testScripts = testScripts;
            this.testCases = testCases;
        }

        public Long getDiscoveryId() {
            return discoveryId;
        }

        public String getAppUrl() {
            return appUrl;
        }

        public String getAppName() {
            return appName;
        }

        public String getAppLoginUsername() {
            return appLoginUsername;
        }

        public String getDiscoveryStatus() {
            return discoveryStatus;
        }

        public Integer getTestData() {
            return testData;
        }

        public Integer getTestScripts() {
            return testScripts;
        }

        public Integer getTestCases() {
            return testCases;
        }

    }

    class DiscoveryResponse {
        private Long projectId;
        private String projectName;
        private List<AutInformation> autInformations;
        private Date lastActivityDate;
        private Date creationTime;
        private String lastUsedBy;
        private Integer noOfEnvironments;
        private Integer totalTestsCount;
        private Integer totalTestsFailedCount;
        private Integer totalTestsPassedCount;
        private Integer totalTestsSkippedCount;
        private Integer noOfUsers;

        public DiscoveryResponse(Long projectId, String projectName,
                                 List<AutInformation> autInformations, Date lastActivityDate,
                                 Date creationTime, String lastUsedBy, Integer noOfEnvironments,
                                 Integer totalTestsCount, Integer totalTestsFailedCount,
                                 Integer totalTestsPassedCount, Integer totalTestsSkippedCount,
                                 Integer noOfUsers) {
            this.projectId = projectId;
            this.projectName = projectName;
            this.autInformations = autInformations;
            this.lastActivityDate = lastActivityDate;
            this.creationTime = creationTime;
            this.lastUsedBy = lastUsedBy;
            this.noOfEnvironments = noOfEnvironments;
            this.totalTestsCount = totalTestsCount;
            this.totalTestsFailedCount = totalTestsFailedCount;
            this.totalTestsPassedCount = totalTestsPassedCount;
            this.totalTestsSkippedCount = totalTestsSkippedCount;
            this.noOfUsers = noOfUsers;
        }

        public Long getProjectId() {
            return projectId;
        }

        public String getProjectName() {
            return projectName;
        }

        public List<AutInformation> getAutInformations() {
            return autInformations;
        }

        public Date getLastActivityDate() {
            return lastActivityDate;
        }

        public Date getCreationTime() {
            return creationTime;
        }

        public String getLastUsedBy() {
            return lastUsedBy;
        }

        public Integer getNoOfEnvironments() {
            return noOfEnvironments;
        }

        public Integer getTotalTestsCount() {
            return totalTestsCount;
        }

        public Integer getTotalTestsFailedCount() {
            return totalTestsFailedCount;
        }

        public Integer getTotalTestsPassedCount() {
            return totalTestsPassedCount;
        }

        public Integer getTotalTestsSkippedCount() {
            return totalTestsSkippedCount;
        }

        public Integer getNoOfUsers() {
            return noOfUsers;
        }
    }

}


