package io.jenkins.plugins.autonomiq.service.types;

public class ExecutedTaskResponse {
    ExecuteTaskResponse[] tasks;
    Integer totalExecs;

    @SuppressWarnings("unused")
    public ExecutedTaskResponse(ExecuteTaskResponse[] tasks,
                                Integer totalExecs) {
        this.tasks = tasks;
        this.totalExecs = totalExecs;
    }
    @SuppressWarnings("unused")
    public ExecuteTaskResponse[] getTasks() {
        return tasks;
    }
    @SuppressWarnings("unused")
    public Integer getTotalExecs() {
        return totalExecs;
    }
}
