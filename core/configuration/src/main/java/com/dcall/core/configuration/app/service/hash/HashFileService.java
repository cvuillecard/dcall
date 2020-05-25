package com.dcall.core.configuration.app.service.hash;

import java.util.Collection;
import java.util.List;

public interface HashFileService {

    String createRootDirectory(final String parentPath, final String salt);
    List<String> createDirectories(final String parentPath, final String salt, final String... directories);

    // utils
    String getPath(final String parentPath, final String hashFileName);
    String getFileHash(String parentPath, final String fileName, final String salt);
    String getHashPath(String parentPath, final String dirName, final String salt);

    Collection<String> list(final String dir);
    boolean exists(final String parentPath, final String salt, final String... relativePaths);

    // setters
    HashFileService setRoot(final String path);
    HashFileService setSalt(final String salt);

}
