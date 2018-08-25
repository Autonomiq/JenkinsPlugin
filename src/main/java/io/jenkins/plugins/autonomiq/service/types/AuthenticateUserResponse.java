package io.jenkins.plugins.autonomiq.service.types;

public class AuthenticateUserResponse {
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
    @SuppressWarnings("unused")
    public String getToken() {
        return token;
    }
    @SuppressWarnings("unused")
    public String getRole() {
        return role;
    }
    @SuppressWarnings("unused")
    public String getName() {
        return name;
    }
    @SuppressWarnings("unused")
    public String getEmail() {
        return email;
    }
    @SuppressWarnings("unused")
    public Long getUserId() {
        return userId;
    }
    @SuppressWarnings("unused")
    public Integer getUserAccount() {
        return userAccount;
    }
}

