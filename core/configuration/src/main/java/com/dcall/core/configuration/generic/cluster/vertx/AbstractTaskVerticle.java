package com.dcall.core.configuration.generic.cluster.vertx;

import com.dcall.core.configuration.app.constant.TaskStatus;
import com.dcall.core.configuration.app.context.task.TaskContext;
import com.dcall.core.configuration.app.entity.task.Task;
import com.dcall.core.configuration.generic.service.task.AbstractTaskExecutor;

import java.util.List;

public abstract class AbstractTaskVerticle extends AbstractContextVerticle {
    protected final long DELAY_MS = 300l;
    protected TaskContext taskContext;
    protected int taskIdx = 0;

    @Override
    public void start() throws Exception {
        taskContext = runtimeContext.clusterContext().taskContext();

        vertx.setPeriodic(DELAY_MS, h -> handle());
    }

    protected void handle() {
        if (taskContext.isCurrentTask()) {
            final Task currTask = taskContext.getCurrent().getTask();
            final TaskStatus status = currTask.getStatus();

            if (status.ordinal() == TaskStatus.RUNNING.ordinal())
                updateQueue();
            else if (status.ordinal() == TaskStatus.COMPLETED.ordinal()) {
                onComplete();
                removeCurrentTask();
            }
            else if (status.ordinal() == TaskStatus.FAILED.ordinal()) {
                onFail();
                removeCurrentTask();
            }
        }
    }

    protected void updateQueue() {
        final List<Task> tasks = taskContext.getCurrent().getChildren();

        while (taskIdx < tasks.size() && tasks.get(taskIdx).getStatus().ordinal() > TaskStatus.RUNNING.ordinal())
            onUpdate(tasks.get(taskIdx++));

        if (tasks.size() > 0 && taskIdx == tasks.size() && !taskContext.getCurrent().getTask().getStatus().equals(TaskStatus.COMPLETED))
            taskContext.getCurrent().complete();
    }

    private void removeCurrentTask() {
        taskContext.getQueue().remove(taskContext.getCurrent());
        taskContext.setCurrent(null);
        this.taskIdx = 0;

        if (taskContext.getQueue().size() > 0) {
            final AbstractTaskExecutor next = taskContext.getQueue().get(0);
            next.getTask().setStatus(TaskStatus.RUNNING);
            taskContext.setCurrent(next);
        }

        onRemove();
    }

    // abstract
    protected abstract void onUpdate(final Task subTask);
    protected abstract void onRemove();
    protected abstract void onComplete();
    protected abstract void onFail();
}
