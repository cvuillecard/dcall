package com.dcall.core.configuration.app.service.filetransfer;

import com.dcall.core.configuration.app.context.RuntimeContext;
import com.dcall.core.configuration.app.context.filetransfer.FileTransferContext;
import com.dcall.core.configuration.app.entity.filetransfer.FileTransfer;
import com.dcall.core.configuration.app.entity.fingerprint.FingerPrint;
import com.dcall.core.configuration.app.exception.FunctionalException;

public interface FileTransferService {
    void publishWorkspace(final RuntimeContext runtimeContext);
    void sendFileRecursively(final RuntimeContext runtimeContext, final FileTransfer<String> fileTransfer, final String parentPath, final String fileName, final FingerPrint<String> fingerPrint);
    void sendFileTransfer(final RuntimeContext runtimeContext, final FileTransfer<String> fileTransfer, final FingerPrint<String> fingerPrint) throws FunctionalException;
    void completeFileTransfer(final RuntimeContext runtimeContext, final FingerPrint<String> fingerPrint);
    void storeWorkspaceTransferContext(final RuntimeContext runtimeContext, final FileTransferContext fileTransferContext);
}
