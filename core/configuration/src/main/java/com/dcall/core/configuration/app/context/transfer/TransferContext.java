package com.dcall.core.configuration.app.context.transfer;

import com.dcall.core.configuration.app.context.filetransfer.FileTransferContext;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public final class TransferContext implements Serializable {
    private Map<String, FileTransferContext> fileTransfersContext = new HashMap<>();

    public Map<String, FileTransferContext> getFileTransfersContext() { return fileTransfersContext; }
}
