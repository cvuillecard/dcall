package com.dcall.core.configuration.app.service.hash;

import java.util.Collection;
import java.util.List;

public interface HashFileService {

    String createRootDirectory(final String parentPath, final String salt);
    String createDirectory(final String parentPath, final String dirName, final String salt);
    List<String> createDirectories(String parentPath, String salt, String... directories);

    // utils
    String getPath(String parentPath, String hashFileName);
    String getFileHash(String parentPath, String fileName, String salt);
    String getHashPath(String parentPath, String dirName, String salt);

    Collection<String> list(final String dir);
    boolean exists(final String parentPath, final String fileName, final String salt);

    // setters
    HashFileService setRoot(final String path);
    HashFileService setSalt(final String salt);
}
