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

    private String seed(String salt) {
        return HashProvider.seedMd5(salt.getBytes());
    }

    private String sign(String parentPath, String fileName, String salt) {
        return HashProvider.signMd5(parentPath, fileName, salt);
    }

    @Override
    public String createRootDirectory(final String parentPath, final String salt) {
        setRoot(createDirectories(parentPath, salt, "root").get(0));
        return this.root;
    }

    @Override
    public List<String> createDirectories(final String parentPath, final String salt, final String... directories) {
        try {
            final String hashSalt = seed(salt);
            final List<String> list = new ArrayList<>();

            if (directories.length > 0) {
                for (final String p : directories) {
                        final String path = getHashPath(parentPath, p, hashSalt);
                        final File dir = new File(path);
                        dir.mkdirs();
                        if (!dir.exists())
                            throw new TechnicalException(parentPath + File.separator + p + " > " + path + " : could not be created");
                        list.add(path);
                }
            }
            return list;
        }
        catch (TechnicalException e) {
            e.log();
        }
        return null;
    }

    @Override
    public String getPath(String parentPath, String hashFileName) {
        return parentPath + File.separator + hashFileName;
    }

    @Override
    public String getFileHash(String parentPath, final String fileName, final String salt) {
        String hash = null;

        if (fileName != null && !fileName.isEmpty()) {
            for (final String member : fileName.split(File.separator)) {
                hash = sign(parentPath, seed(member), salt);
                parentPath = getPath(parentPath, hash);
            }
        }

        return hash;
    }

    @Override
    public String getHashPath(String parentPath, String dirName, String salt) {
        String path = null;

        if (dirName != null && !dirName.isEmpty()) {
            for (final String member : dirName.split(File.separator)) {
                path = getPath(parentPath, getFileHash(parentPath, member, salt));
                parentPath = path;
            }
        }
        return path;
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
    public boolean exists(String parentPath, final String salt, final String... relativePaths) {
        if (relativePaths.length > 0) {
            for (final String p : relativePaths) {
                final String path = getHashPath(parentPath, p, salt);
                if (!new File(path).exists()) {
                    LOG.debug(parentPath + File.separator + p + " > " + path + " : doesn't exists");
                    return false;
                }
            }
            return true;
        }
        return false;
    }
}
