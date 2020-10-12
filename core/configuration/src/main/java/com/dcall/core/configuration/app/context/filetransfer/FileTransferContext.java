package com.dcall.core.configuration.app.context.filetransfer;

import com.dcall.core.configuration.app.entity.filetransfer.FileTransfer;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public final class FileTransferContext implements Serializable {
    private Map<String, FileTransfer<String>> fileTransfers = new HashMap<>();

    public Map<String, FileTransfer<String>> getFileTransfers() { return fileTransfers; }
}
