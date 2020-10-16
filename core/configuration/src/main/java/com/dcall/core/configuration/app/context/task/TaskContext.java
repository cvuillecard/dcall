package com.dcall.core.configuration.app.context.task;

import com.dcall.core.configuration.app.constant.TaskStatus;
import com.dcall.core.configuration.generic.service.task.AbstractTaskExecutor;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public final class TaskContext implements Serializable {
    private final List<AbstractTaskExecutor> queue = new ArrayList<>();
    private AbstractTaskExecutor current;

    // main
    public void enqueue(final AbstractTaskExecutor taskExecutor) {
        if (current == null) {
            this.current = taskExecutor;
            this.current.getTask().setStatus(TaskStatus.RUNNING);
        }
        else
            taskExecutor.getTask().setStatus(TaskStatus.PENDING);

        this.queue.add(taskExecutor);
    }

    // getters
    public List<AbstractTaskExecutor> getQueue() { return queue; }
    public boolean isCurrentTask() { return this.current != null; }
    public AbstractTaskExecutor getCurrent() { return current; }

    // setters
    public TaskContext setCurrent(final AbstractTaskExecutor current) { this.current = current; return this; }
}
