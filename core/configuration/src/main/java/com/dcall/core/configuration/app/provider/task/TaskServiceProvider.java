package com.dcall.core.configuration.app.provider.task;

import com.dcall.core.configuration.app.service.task.TaskService;
import com.dcall.core.configuration.app.service.task.TaskServiceImpl;

import java.io.Serializable;

public final class TaskServiceProvider implements Serializable {
    private final TaskService taskService;

    public TaskServiceProvider() {
        this.taskService = new TaskServiceImpl();
    }

    public TaskService taskService() { return taskService; }
}
