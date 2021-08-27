package io.jenkins.plugins.autonomiq.service.types;

public class UserVariable {
    private Integer accountId;
    private Long projectId;
    private String key;
    private String value;

    public UserVariable(Integer accountId, Long projectId, String key, String value) {
        this.accountId = accountId;
        this.projectId = projectId;
        this.key = key;
        this.value = value;
    }

    public Integer getAccountId() {
        return accountId;
    }

    public Long getProjectId() {
        return projectId;
    }

    public String getKey() {
        return key;
    }

    public String getValue() {
        return value;
    }
}
