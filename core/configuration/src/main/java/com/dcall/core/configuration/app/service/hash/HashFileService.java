package com.dcall.core.configuration.app.service.hash;

import com.dcall.core.configuration.generic.entity.user.User;

import java.util.Collection;
import java.util.List;

public interface HashFileService {
    String seed(String salt);
    String createRootDirectory(final String parentPath, final String salt);
    List<String> createDirectories(final String parentPath, final String salt, final String... directories);

    // utils
    String getPath(final String parentPath, final String hashFileName);
    String getFileHash(String parentPath, final String salt, final String fileName);
    String getHashPath(String parentPath, final String salt, final String dirName);

    Collection<String> list(final String dir);
    boolean exists(final String parentPath, final String salt, final String... relativePaths);

    // setters
    HashFileService setRoot(final String path);
    HashFileService setSalt(final String salt);
}
