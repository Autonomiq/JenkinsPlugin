package io.jenkins.plugins.autonomiq.service.types;

public class AuthenticateUserBody {
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

