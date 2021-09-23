package io.jenkins.plugins.autonomiq.util;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang.StringUtils;
import hudson.util.Secret;
import io.jenkins.plugins.autonomiq.service.ServiceException;
import okhttp3.*;

public class WebClient {

    private static final MediaType JSON = MediaType.get("application/json; charset=utf-8");
    private final OkHttpClient client;
    
    private int responseCount(Response response) {
    	  int result = 1;
    	  while ((response = response.priorResponse()) != null) {
    	    result++;
    	  }
    	  return result;
    	}
    
    public WebClient(String proxyHost, String proxyPort, String proxyUser, Secret proxyPassword) {
    	int proxyPortInt = Integer.parseInt(proxyPort);
    	if (!StringUtils.isEmpty(proxyUser) && !StringUtils.isEmpty(Secret.toString(proxyPassword))) {
	    	Authenticator proxyAuthenticator = new Authenticator() {
	    		int invalidCredentialAttempts = 0;
	    		  @Override public Request authenticate(Route route, Response response) throws IOException {
	    		       String credential = Credentials.basic(proxyUser, Secret.toString(proxyPassword));
	    		       if (credential.equals(response.request().header("Proxy-Authorization"))) {
	    		    	   if (invalidCredentialAttempts > 100) {
	    		    		   return null; // If we already failed with these credentials 100 times, don't retry.
	    		    	   } else {
	    		    		   invalidCredentialAttempts++;
	    		    	   }
	    		       } else {
	    		    	   //reset counter after successful attempt as we want to count continuous
	    		    	   //unsuccessful 1000 attempts only
	    		    	   invalidCredentialAttempts = 0;
	    		       }
	    		       if (responseCount(response) >= 100) {
	    		    	    return null; // If we've failed 100 times, give up.
	    		    	  }
	    		       return response.request().newBuilder()
	    		           .header("Proxy-Authorization", credential)
	    		           .build();
	    		  }
	    		};
	    		Proxy proxyTest = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(proxyHost, proxyPortInt));
	    		client = new OkHttpClient.Builder().connectTimeout(30, TimeUnit.SECONDS).writeTimeout(30, TimeUnit.SECONDS)
	    			    .readTimeout(30, TimeUnit.SECONDS).retryOnConnectionFailure(false).
	    				proxy(proxyTest).proxyAuthenticator(proxyAuthenticator).build();
	    		
	    	} else {
	    		Proxy proxyTest = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(proxyHost, proxyPortInt));
	    		client = new OkHttpClient.Builder().proxy(proxyTest).build();
	    	}
    }

    public WebClient() {
        client = new OkHttpClient();
    }

    public String get(String url, String token) throws ServiceException {
        Request request = new Request.Builder()
                .url(url)
                .addHeader("Authorization", token == null ? "" : "Bearer " + token)
                .build();

        try (Response response = client.newCall(request).execute()) {
        	if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
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

    public String post(String url, String json, String token) throws ServiceException {
        RequestBody body = RequestBody.create(JSON, json);
        Request request = new Request.Builder()
                .url(url)
                .addHeader("Authorization", token == null ? "" : "Bearer " + token)
                .post(body)
                .build();
        try (Response response = client.newCall(request).execute()) {
        	if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
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

    public WebsocketData createWebsocket(String url) throws ServiceException {

        Request request = new Request.Builder()
                .url(url)
                .build();
        WebsocketListener listener = new WebsocketListener();
        WebSocket ws = client.newWebSocket(request, listener);

        return new WebsocketData(ws, listener);
    }
}
