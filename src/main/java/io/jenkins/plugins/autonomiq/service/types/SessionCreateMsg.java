package io.jenkins.plugins.autonomiq.service.types;

public class SessionCreateMsg {

    String authorizationToken;

    public SessionCreateMsg(String authorizationToken) {
        this.authorizationToken = authorizationToken;
    }

    public String getAuthorizationToken() {
        return authorizationToken;
    }

}
