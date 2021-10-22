package io.jenkins.plugins.autonomiq.service.types;

import java.util.Arrays;

public class ExecutedTaskResponse {
    ExecuteTaskResponse[] tasks;
    Integer totalExecs;

    @SuppressWarnings("unused")
    public ExecutedTaskResponse(ExecuteTaskResponse[] tasks,
                                Integer totalExecs) {
        this.tasks = Arrays.copyOf(tasks,tasks.length);
        this.totalExecs = totalExecs;
    }
    @SuppressWarnings("unused")
    public ExecuteTaskResponse[] getTasks() {
        return Arrays.copyOf(tasks, tasks.length);
    }
    @SuppressWarnings("unused")
    public Integer getTotalExecs() {
        return totalExecs;
    }
}
