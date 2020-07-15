package com.dcall.core.configuration.app.entity.filetransfer;

import com.dcall.core.configuration.app.entity.Entity;
import com.dcall.core.configuration.utils.constant.FileType;

public interface FileTransfer<ID> extends Entity<ID> {
    // getter
    String getParentPath();
    String getFileName();
    FileType getFileType();
    byte[] getBytes();

    // setter
    FileTransfer<ID> setParentPath(final String parentPath);
    FileTransfer<ID> setFileName(final String fileName);
    FileTransfer<ID> setFileType(final FileType fileType);
    FileTransfer<ID> setBytes(final byte[] bytes);
}
