package com.dcall.core.configuration.app.service.filetransfer;

import com.dcall.core.configuration.app.context.RuntimeContext;
import com.dcall.core.configuration.app.context.filetransfer.FileTransferContext;

import java.io.Serializable;

public interface FileTransferService extends Serializable {
    void publishWorkspace(final RuntimeContext runtimeContext) throws Exception;
    void storeWorkspaceTransferContext(final RuntimeContext runtimeContext, final FileTransferContext fileTransferContext) throws Exception;
}
