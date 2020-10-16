package com.dcall.core.configuration.app.service.task.filetransfer;

import com.dcall.core.configuration.app.context.task.filetransfer.workspace.WorkspaceTransferTaskContext;
import com.dcall.core.configuration.app.entity.task.Task;
import com.dcall.core.configuration.generic.service.task.AbstractTaskExecutor;

import java.io.Serializable;

public interface FileTransferTaskService extends Serializable {
    void sendFileRecursively(final WorkspaceTransferTaskContext taskContext, final String parentPath, final String fileName) throws Exception;
    AbstractTaskExecutor sendFileTransfer(final WorkspaceTransferTaskContext taskContext, final Task task, final String uri);
}
