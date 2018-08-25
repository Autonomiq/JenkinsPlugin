package io.jenkins.plugins.autonomiq;

public class ProjectData {
    private Long projectId;
    private Long discoveryId;
    private String projectName;

    public ProjectData(Long projectId, Long discoveryId, String projectName) {
        this.projectId = projectId;
        this.discoveryId = discoveryId;
        this.projectName = projectName;
    }

    public Long getProjectId() {
        return projectId;
    }

    public Long getDiscoveryId() {
        return discoveryId;
    }

    public String getProjectName() {
        return projectName;
    }

}