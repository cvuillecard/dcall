package com.dcall.core.configuration.app.service.hash;

import com.dcall.core.configuration.app.security.hash.HashProvider;
import com.dcall.core.configuration.app.system.platform.Platform;
import com.dcall.core.configuration.utils.FileUtils;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.Arrays;
import java.util.List;

public class HashServiceTest extends Platform {
    private final static Logger LOG = LoggerFactory.getLogger(HashServiceTest.class);
    private final static int MD5_LENGTH = 32;
    private final static String workspace = String.join(File.separator, Arrays.asList("src", "test", "resources", "workspace"));
    private final static String pwd =  FileUtils.getInstance().pwd() + File.separator + workspace;
    private final static String salt = HashProvider.seedMd5(HashProvider.random());
    private final static HashFileService service = new HashFileServiceImpl();

    @BeforeClass
    public static void init() {
        FileUtils.getInstance().remove(pwd);
        new File(pwd).mkdir();
    }

    @Test public void should_create_root_directory_createRootDirectory_createDirectory() {
        final String root = service.createRootDirectory(pwd, salt);
        final String root2 = service.createDirectory(pwd, "root", HashProvider.seedMd5(salt.getBytes()));
        final String hash = root.replace(pwd + File.separator, "");
        final String hash2 = root2.replace(pwd + File.separator, "");

        Assert.assertEquals(MD5_LENGTH, hash.length());
        Assert.assertEquals(MD5_LENGTH, hash2.length());

        Assert.assertTrue(new File(root).exists());
        Assert.assertEquals(hash, hash2);
    }

    @Test
    public void should_create_recursively_directories_createDirectories() {
        final String hashSalt = HashProvider.seedMd5(salt.getBytes());
        final String root = service.createRootDirectory(pwd, salt);

        final String hashPathToto = service.getHashPath(root, "toto", hashSalt);
        final String hashPathTiti = service.getHashPath(root, "titi", hashSalt);
        final String hashPathTata = service.getHashPath(root, "tata", hashSalt);

        final List<String> paths = service.createDirectories(root, salt, "toto", "titi", "tata");

        Assert.assertTrue(paths.contains(hashPathToto));
        Assert.assertTrue(paths.contains(hashPathTiti));
        Assert.assertTrue(paths.contains(hashPathTata));

        Assert.assertTrue(service.exists(root, "toto", hashSalt));
        Assert.assertTrue(service.exists(root, "titi", hashSalt));
        Assert.assertTrue(service.exists(root, "tata", hashSalt));

        paths.stream().forEach(p -> Assert.assertTrue(new File(p).exists()));
    }
}
