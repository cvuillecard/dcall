package com.dcall.core.configuration.app.service.task.filetransfer.workspace;

import com.dcall.core.configuration.app.constant.GitConstant;
import com.dcall.core.configuration.app.constant.TaskStatus;
import com.dcall.core.configuration.app.context.RuntimeContext;
import com.dcall.core.configuration.app.context.task.AbstractTaskContext;
import com.dcall.core.configuration.app.context.task.filetransfer.workspace.WorkspaceTransferTaskContext;
import com.dcall.core.configuration.app.context.task.filetransfer.workspace.WorkspaceTransferTaskContextImpl;
import com.dcall.core.configuration.app.entity.task.Task;
import com.dcall.core.configuration.app.entity.task.TaskBean;
import com.dcall.core.configuration.app.service.task.filetransfer.FileTransferTaskService;
import com.dcall.core.configuration.generic.service.task.AbstractTaskExecutor;
import com.dcall.core.configuration.generic.service.task.TaskExecutorService;
import com.dcall.core.configuration.utils.FileUtils;
import com.dcall.core.configuration.utils.SerializationUtils;
import com.dcall.core.configuration.utils.constant.FileType;
import io.vertx.core.Vertx;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;

public final class WorkspaceTransferTaskServiceImpl extends AbstractTaskExecutor implements FileTransferTaskService {
    private static final Logger LOG = LoggerFactory.getLogger(WorkspaceTransferTaskServiceImpl.class);

    public WorkspaceTransferTaskServiceImpl(final RuntimeContext runtimeContext) {
        super();
        this.init(runtimeContext, new WorkspaceTransferTaskContextImpl(runtimeContext));
    }

    @Override
    public TaskExecutorService init(final RuntimeContext runtimeContext, final AbstractTaskContext taskContext) {
       return super.init(runtimeContext, new WorkspaceTransferTaskContextImpl(runtimeContext));
    }

    @Override
    public AbstractTaskExecutor execute() {
        try {
            final WorkspaceTransferTaskContext taskContext = (WorkspaceTransferTaskContext) this.taskContext;

            sendFileRecursively(taskContext, taskContext.getGitService().getSystemRepository(), GitConstant.GIT_FILENAME);

            runtimeContext.clusterContext().taskContext().enqueue(this);
        }
        catch (Exception e) {
            final String msgError = "Failed task id = " + this.task.getId() + " - ERROR : " + e.getMessage();
            this.task.setStatus(TaskStatus.FAILED).setId(msgError);

            LOG.error(e.getMessage());
        }
        return this;
    }

    @Override
    public void sendFileRecursively(final WorkspaceTransferTaskContext taskContext, final String parentPath, final String fileName) throws Exception {
        final FileUtils fileUtils = FileUtils.getInstance();
        final String filePath = fileUtils.getFilePath(parentPath, fileName);
        final File file = new File(filePath);

        taskContext.getFileTransfer().setParentPath(parentPath).setFileName(fileName);

        if (file.isDirectory())
            for (final File f : file.listFiles())
                sendFileRecursively(taskContext, filePath, f.getName());
        else {
            final Task subTask = new TaskBean(file.getPath(), TaskStatus.RUNNING, task);
            this.addTask(subTask);
            final FileInputStream is = new FileInputStream(file);
            taskContext.getFileTransfer().setBytes(fileUtils.readAllBytes(is)).setFileType(FileType.FILE);
            is.close();
            LOG.info(" > send File : " + file.getAbsolutePath());
            sendFileTransfer(taskContext, subTask, getTaskContext().getRunURI());
        }
    }

    @Override
    public AbstractTaskExecutor sendFileTransfer(final WorkspaceTransferTaskContext taskContext, final Task task, final String uri) {
        final byte[] bytes = SerializationUtils.serialize(taskContext.getFileTransfer());
        final int nbChunk = taskContext.getMessageService().getNbChunk(bytes);

        taskContext.getMessageService().sendEncryptedChunk(runtimeContext, Vertx.currentContext().owner(), uri, bytes, nbChunk, 0, getTaskContext().getFingerPrint(), getTaskContext().getMsgTransporter(), task);

        return this;
    }

    @Override
    public AbstractTaskExecutor complete() {
        return sendFileTransfer((WorkspaceTransferTaskContext) this.taskContext, this.task, getTaskContext().getCompleteURI());
    }
}
