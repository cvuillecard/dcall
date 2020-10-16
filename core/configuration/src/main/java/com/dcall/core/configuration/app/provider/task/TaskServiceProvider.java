package com.dcall.core.configuration.app.provider.task;

import com.dcall.core.configuration.app.context.RuntimeContext;
import com.dcall.core.configuration.app.service.task.filetransfer.workspace.WorkspaceTransferTaskServiceImpl;
import com.dcall.core.configuration.generic.service.task.AbstractTaskExecutor;

import java.io.Serializable;

public final class TaskServiceProvider implements Serializable {
    private final RuntimeContext runtimeContext;

    public TaskServiceProvider(final RuntimeContext runtimeContext) {
        this.runtimeContext = runtimeContext;
    }

    public AbstractTaskExecutor workspaceTransferService() { return new WorkspaceTransferTaskServiceImpl(this.runtimeContext); }
}
