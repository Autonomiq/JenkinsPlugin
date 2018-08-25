package io.jenkins.plugins.autonomiq.util;

import io.jenkins.plugins.autonomiq.service.ServiceException;
import okhttp3.*;

public class WebClient {

    private static final MediaType JSON = MediaType.get("application/json; charset=utf-8");
    private final OkHttpClient client;

    public WebClient() {
        client = new OkHttpClient();
    }

    public String get(String url) throws ServiceException {
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

    public String post(String url, String json) throws ServiceException {
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

}
