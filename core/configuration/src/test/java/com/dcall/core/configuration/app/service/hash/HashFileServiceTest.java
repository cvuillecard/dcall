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

public class HashFileServiceTest extends Platform {
    private final static Logger LOG = LoggerFactory.getLogger(HashFileServiceTest.class);
    private final static int MD5_LENGTH = 32;
    private final static String workspace = String.join(File.separator, Arrays.asList("src", "test", "resources", "workspace"));
    private final static String pwd =  FileUtils.getInstance().pwd() + File.separator + workspace;
    private final static String salt = HashProvider.seedMd5(HashProvider.random());
    private final static HashFileService service = new HashFileServiceImpl();

    @BeforeClass
    public static void init() {
        FileUtils.getInstance().remove(pwd);
        new File(pwd).mkdirs();
    }

    @Test public void should_create_root_directory_createRootDirectory_createDirectory_exists() {
        final String hashSalt = HashProvider.seedMd5(salt.getBytes());
        final String root = service.createRootDirectory(pwd, salt);
        final String hash = service.getFileHash(pwd, "root", hashSalt);

        Assert.assertEquals(MD5_LENGTH, hash.length());

        Assert.assertTrue(new File(root).exists());

        Assert.assertTrue(service.exists(pwd, hashSalt, "root"));
        Assert.assertTrue(service.exists(pwd, hashSalt, "root" + File.separator)); // root/
        Assert.assertTrue(service.exists(pwd, hashSalt, "root" + File.separator + File.separator + File.separator)); // root///

        Assert.assertFalse(service.exists(pwd, hashSalt, "rot"));
    }

    @Test
    public void should_create_recursively_directories_with_or_without_relative_paths_createDirectories() {
        final String hashSalt = HashProvider.seedMd5(salt.getBytes());
        final String root = service.createRootDirectory(pwd, salt);

        final String pathToto = "toto" + File.separator + "cousin_de_toto";
        final String pathTiti = "titi" + File.separator + "cert" + File.separator + "public";
        final String pathTata = "tata";

        final String hashPathToto = service.getHashPath(root, pathToto, hashSalt);
        final String hashPathTiti = service.getHashPath(root, pathTiti, hashSalt);
        final String hashPathTata = service.getHashPath(root, pathTata, hashSalt);

        final List<String> paths = service.createDirectories(root, salt, pathToto, pathTiti, pathTata);

        Assert.assertTrue(paths.contains(hashPathToto));
        Assert.assertTrue(paths.contains(hashPathTiti));
        Assert.assertTrue(paths.contains(hashPathTata));

        final String rootCert = service.getHashPath(root, "titi" + File.separator + "cert", hashSalt);
        Assert.assertTrue(service.exists(root, hashSalt, "toto", pathToto, "titi", pathTiti, "tata"));
        Assert.assertTrue(service.exists(rootCert, hashSalt, "public"));

        Assert.assertFalse(service.exists(root, hashSalt, "toto" + File.separator + "other"));
        Assert.assertFalse(service.exists(root, hashSalt, "toto", pathToto + File.separator + "other", "titi"));
        Assert.assertFalse(service.exists(rootCert, hashSalt, "private"));

        paths.stream().forEach(p -> Assert.assertTrue(new File(p).exists()));
    }
}