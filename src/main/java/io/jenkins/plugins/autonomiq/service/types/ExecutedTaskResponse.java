package io.jenkins.plugins.autonomiq.service.types;

public class ExecutedTaskResponse {
    ExecuteTaskResponse[] tasks;
    @SuppressWarnings("unused")
    public ExecutedTaskResponse(ExecuteTaskResponse[] tasks) {
        this.tasks = tasks;
    }
    @SuppressWarnings("unused")
    public ExecuteTaskResponse[] getTasks() {
        return tasks;
    }
}
