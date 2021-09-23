package io.jenkins.plugins.autonomiq.service.types;
import hudson.util.Secret;

public class AuthenticateUserBody {
    private String username;
    private Secret password;

    public AuthenticateUserBody(String username, Secret password) {
        this.username = username;
        this.password = password;
    }
    
    public String getUsername() {
        return username;
    }

    public Secret getPassword() {
        return password;
    }
}

