package com.dcall.core.configuration.app.entity.task;

import com.dcall.core.configuration.app.constant.TaskStatus;

public final class TaskBean implements Task<String> {
    private String id;
    private TaskStatus status;
    private Task<String> parent;

    public TaskBean() { this.status = TaskStatus.PENDING; }
    public TaskBean(final TaskStatus taskStatus) { this.status = taskStatus; }

    public TaskBean(final String id, final TaskStatus status) {
        this.id = id;
        this.status = status;
    }

    public TaskBean(final String id, final TaskStatus status, final Task parent) {
        this.id = id;
        this.status = status;
        this.parent = parent;
    }

    // getters
    @Override public String getId() { return this.id; }
    @Override public TaskStatus getStatus() { return status; }
    @Override public Task<String> getParent() { return parent; }

    // setters
    @Override public Task<String> setId(final String id) { this.id = id; return this; }
    @Override public Task<String> setStatus(final TaskStatus status) { this.status = status; return this; }
    @Override public Task<String> setParent(final Task<String> parent) { this.parent = parent; return this; }

    @Override
    public String toString() {
        return (this.getParent() != null ? ("Task : " + this.getParent().getId()) + " (" + this.getParent().getStatus().name() + ") > " : "")
                + "subTask : id = " + this.id + " - " + this.getStatus().name();
    }
}
