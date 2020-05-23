package com.dcall.core.configuration.app.service.hash;

import com.dcall.core.configuration.app.exception.TechnicalException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import com.dcall.core.configuration.app.security.hash.HashProvider;

public final class HashFileServiceImpl implements HashFileService {
    private static final Logger LOG = LoggerFactory.getLogger(HashFileServiceImpl.class);
    private String root = null;
    private String salt = null;

    @Override
    public String createRootDirectory(final String parentPath, final String salt) {
        setRoot(createDirectory(parentPath, "root", HashProvider.seedMd5(salt.getBytes())));
        return this.root;
    }

    @Override
    public String createDirectory(final String parentPath, final String dirName, final String salt) {
        try {
            final File dir = new File(getHashPath(parentPath, dirName, salt));

            if (!dir.exists()) {
                dir.mkdirs();
                if (!dir.exists())
                    throw new TechnicalException(parentPath + " directory could not be created");
                else
                    LOG.debug("created directory : " + dir.getAbsolutePath());
            }

            return dir.getAbsolutePath();
        }
        catch (TechnicalException e) {
            e.log();
        }

        return null;
    }

    @Override
    public List<String> createDirectories(final String parentPath, final String salt, final String... directories) {
        final String hashSalt = HashProvider.seedMd5(salt.getBytes());
        final List<String> list = new ArrayList<>();

        Arrays.stream(directories).forEach(dir -> list.add(createDirectory(parentPath, dir, hashSalt)));

        return list;
    }

    @Override
    public String getPath(String parentPath, String hashFileName) {
        return parentPath + File.separator + hashFileName;
    }

    @Override
    public String getFileHash(final String parentPath, final String fileName, final String salt) {
        return HashProvider.signMd5(parentPath, fileName, salt);
    }

    @Override
    public String getHashPath(String parentPath, String dirName, String salt) {
        return getPath(parentPath, getFileHash(parentPath, HashProvider.seedMd5(dirName.getBytes()), salt));
    }

    @Override
    public Collection<String> list(final String dir) {
        final List<String> l = new ArrayList<>();
        final File d = new File(dir);

        if (d.exists())
            Arrays.stream(d.listFiles()).forEach(f -> l.add(f.getName()));

        return l;
    }

    @Override
    public HashFileService setRoot(final String path) { this.root = path; return this; }

    @Override
    public HashFileService setSalt(final String salt) { this.salt = salt; return this; }

    @Override
    public boolean exists(final String parentPath, final String fileName, final String salt) {
        return new File(getHashPath(parentPath, fileName, salt)).exists();
    }
}
