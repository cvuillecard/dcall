package com.dcall.core.configuration.app.service.filetransfer;

import com.dcall.core.configuration.app.context.RuntimeContext;
import com.dcall.core.configuration.app.context.filetransfer.FileTransferContext;
import com.dcall.core.configuration.app.entity.filetransfer.FileTransfer;
import com.dcall.core.configuration.app.entity.fingerprint.FingerPrint;
import com.dcall.core.configuration.app.exception.ExceptionHolder;
import com.dcall.core.configuration.app.exception.FunctionalException;
import com.dcall.core.configuration.app.exception.TechnicalException;

import java.io.Serializable;
import java.util.List;

public interface FileTransferService extends Serializable {
    void publishWorkspace(final RuntimeContext runtimeContext) throws Exception;
    void sendFileRecursively(final RuntimeContext runtimeContext, final String uri, final FileTransfer<String> fileTransfer, final String parentPath, final String fileName, final FingerPrint<String> fingerPrint) throws Exception;
    void sendFileTransfer(final RuntimeContext runtimeContext, final String uri, final FileTransfer<String> fileTransfer, final FingerPrint<String> fingerPrint) throws Exception;
    void completeFileTransfer(final RuntimeContext runtimeContext, final String uri, final FingerPrint<String> fingerPrint, final String publicId) throws Exception;
    void storeWorkspaceTransferContext(final RuntimeContext runtimeContext, final FileTransferContext fileTransferContext) throws Exception;
}
