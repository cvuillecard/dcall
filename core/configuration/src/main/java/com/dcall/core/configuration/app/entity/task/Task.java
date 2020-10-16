package com.dcall.core.configuration.app.entity.task;

import com.dcall.core.configuration.app.constant.TaskStatus;
import com.dcall.core.configuration.app.entity.Entity;

public interface Task<ID> extends Entity<ID> {
    // getters
    TaskStatus getStatus();
    Task<ID> getParent();

    // setters
    Task<ID> setStatus(final TaskStatus status);
    Task<ID> setParent(final Task<ID> parent);
}
