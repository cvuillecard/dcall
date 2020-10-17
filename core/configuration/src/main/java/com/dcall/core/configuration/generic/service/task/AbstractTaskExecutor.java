package com.dcall.core.configuration.generic.service.task;

import com.dcall.core.configuration.app.constant.TaskStatus;
import com.dcall.core.configuration.app.context.RuntimeContext;
import com.dcall.core.configuration.app.context.task.AbstractTaskContext;
import com.dcall.core.configuration.app.entity.task.Task;
import com.dcall.core.configuration.app.entity.task.TaskBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractTaskExecutor implements TaskExecutorService {
    private static final Logger LOG = LoggerFactory.getLogger(AbstractTaskExecutor.class);
    protected RuntimeContext runtimeContext;
    protected AbstractTaskContext taskContext;
    protected Task task;
    protected List<Task> children = new ArrayList<>();

    // main
    @Override
    public TaskExecutorService init(final RuntimeContext context, final AbstractTaskContext taskContext) {
        this.runtimeContext = context;
        this.task = new TaskBean().setId(this.getClass().getSimpleName());

        if (taskContext != null)
            this.taskContext = taskContext.setRuntimeContext(this.runtimeContext);

        return this;
    }

    @Override
    public TaskExecutorService run() throws Exception {
        if (runtimeContext == null)
            throw new IllegalStateException("Missing initialization of runtimeContext");
        else if (taskContext == null)
            throw new IllegalStateException("Missing initialization of taskContext");

        taskContext.init();
        execute();

        return this;
    }

    public abstract AbstractTaskExecutor execute() throws Exception;
    public abstract AbstractTaskExecutor complete();

    // getters
    @Override public RuntimeContext getRuntimeContext() { return this.runtimeContext; }
    @Override public AbstractTaskContext getTaskContext() { return this.taskContext; }
    @Override public Task getTask() { return this.task; }
    @Override public List<Task> getChildren() { return children; }

    // setters
    @Override public AbstractTaskExecutor setRuntimeContext(final RuntimeContext context) { this.runtimeContext = context; return this; }
    @Override public TaskExecutorService setTaskContext(final AbstractTaskContext taskContext) { this.taskContext = taskContext; return this; }
    @Override public AbstractTaskExecutor setTask(final Task task) { this.task = task; return this; }
    @Override public AbstractTaskExecutor setChildren(final List<Task> children) { this.children = children; return this; }
    @Override public AbstractTaskExecutor addTask(final Task task) { this.children.add(task); return this; }
}
