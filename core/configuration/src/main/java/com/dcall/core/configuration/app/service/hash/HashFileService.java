package com.dcall.core.configuration.app.service.hash;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

public interface HashFileService extends Serializable {
    String seed(final String salt);
    String createRootDirectory(final String parentPath, final String salt);
    List<String> createDirectories(final String parentPath, final String salt, final String... directories);

    // utils
    String getPath(final String parentPath, final String hashFileName);
    String getFileHash(final String parentPath, final String md5Salt, final String fileName);
    String getHashPath(final String parentPath, final String md5Salt, final String dirName);

    Collection<String> list(final String dir);
    boolean exists(final String parentPath, final String md5Salt, final String... relativePaths);

    // setters
    HashFileService setRoot(final String path);
    HashFileService setSalt(final String salt);
}
