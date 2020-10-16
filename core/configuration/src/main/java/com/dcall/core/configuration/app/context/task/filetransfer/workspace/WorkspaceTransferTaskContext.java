package com.dcall.core.configuration.app.context.task.filetransfer.workspace;

import com.dcall.core.configuration.app.entity.filetransfer.FileTransfer;
import com.dcall.core.configuration.app.service.environ.EnvironService;
import com.dcall.core.configuration.app.service.fingerprint.FingerPrintService;
import com.dcall.core.configuration.app.service.git.GitService;
import com.dcall.core.configuration.app.service.message.MessageService;

import java.io.Serializable;

public interface WorkspaceTransferTaskContext extends Serializable {

    // getters
    EnvironService getEnvironService();
    FingerPrintService getFingerPrintService();
    MessageService getMessageService();
    String getPublicId();
    FileTransfer<String> getFileTransfer();
    GitService getGitService();

    // setters
    WorkspaceTransferTaskContext setEnvironService(final EnvironService environService);
    WorkspaceTransferTaskContext setFingerPrintService(final FingerPrintService fingerPrintService);
    WorkspaceTransferTaskContext setMessageService(final MessageService messageService);
    WorkspaceTransferTaskContext setPublicId(final String publicId);
    WorkspaceTransferTaskContext setFileTransfer(final FileTransfer<String> fileTransfer);
    WorkspaceTransferTaskContext setGitService(final GitService gitService);
}
