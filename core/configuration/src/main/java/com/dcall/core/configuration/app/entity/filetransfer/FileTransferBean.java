package com.dcall.core.configuration.app.entity.filetransfer;

import com.dcall.core.configuration.utils.constant.FileType;

public final class FileTransferBean implements FileTransfer<String> {
    private String id;
    private String parentPath;
    private String fileName;
    private FileType fileType;
    private byte[] bytes;

    @Override public String getId() { return id; }
    @Override public String getParentPath() { return parentPath; }
    @Override public String getFileName() { return fileName; }
    @Override public FileType getFileType() { return fileType; }
    @Override public byte[] getBytes() { return bytes; }

    @Override public FileTransfer<String> setId(final String id) { this.id = id; return this; }
    @Override public FileTransfer<String> setParentPath(final String parentPath) { this.parentPath = parentPath; return this; }
    @Override public FileTransfer<String> setFileName(final String fileName) { this.fileName = fileName; return this; }
    @Override public FileTransfer<String> setFileType(final FileType fileType) { this.fileType = fileType; return this; }
    @Override public FileTransfer<String> setBytes(final byte[] bytes) { this.bytes = bytes; return this; }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        final String nullStr = "null";

        sb.append(this.getClass().getSimpleName() + " [id = " + id);
        sb.append(", parentPath = " + (parentPath != null ? parentPath : nullStr));
        sb.append(", fileName = " + (fileName != null ? fileName : nullStr));
        sb.append(", fileType = " + (fileType != null ? fileType.name() : nullStr) + ']');

        return sb.toString();
    }
}
