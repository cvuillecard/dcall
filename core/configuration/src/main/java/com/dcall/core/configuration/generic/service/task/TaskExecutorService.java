package com.dcall.core.configuration.generic.service.task;

import com.dcall.core.configuration.app.context.RuntimeContext;
import com.dcall.core.configuration.app.context.WithRuntimeContext;
import com.dcall.core.configuration.app.context.task.AbstractTaskContext;
import com.dcall.core.configuration.app.entity.task.Task;

import java.util.List;

public interface TaskExecutorService extends WithRuntimeContext {
    // main
    TaskExecutorService init(final RuntimeContext context, final AbstractTaskContext taskContext);
    TaskExecutorService run();

    // getters
    Task getTask();
    AbstractTaskContext getTaskContext();
    List<Task> getChildren();

    // setters
    TaskExecutorService setTask(final Task task);
    TaskExecutorService setTaskContext(final AbstractTaskContext taskContext);
    TaskExecutorService setChildren(final List<Task> children);
    TaskExecutorService addTask(final Task task);
}
