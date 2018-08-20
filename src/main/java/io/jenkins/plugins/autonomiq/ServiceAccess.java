package io.jenkins.plugins.autonomiq;

import com.google.gson.Gson;
import okhttp3.*;
import java.io.*;

public class ServiceAccess {

    private final String authenticatePath = ":8005/authenticate/basic";
    private static final MediaType JSON = MediaType.get("application/json; charset=utf-8");

    private final OkHttpClient client;
    private final Gson gson;
    private final PrintStream log;

    public ServiceAccess(PrintStream log,
                         String aiqUrl,
                         String login,
                         String password) throws ServiceException {

        this.log = log;
        gson = new Gson();

        client = new OkHttpClient();

        AuthenticateUserBody authBody = new AuthenticateUserBody(login, password);
        String authJson = gson.toJson(authBody);

        try {


            String resp = post(aiqUrl + authenticatePath, authJson);

            log.println("Login Response:");
            log.println(resp);

        } catch (Exception e) {
            throw new ServiceException("Exception in authentication", e);
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

}


