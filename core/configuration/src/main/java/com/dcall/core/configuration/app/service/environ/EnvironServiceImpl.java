package com.dcall.core.configuration.app.service.environ;

import com.dcall.core.configuration.app.constant.EnvironConstant;
import com.dcall.core.configuration.app.constant.UserConstant;
import com.dcall.core.configuration.app.provider.hash.HashServiceProvider;
import com.dcall.core.configuration.app.security.aes.AESProvider;
import com.dcall.core.configuration.app.security.hash.HashProvider;
import com.dcall.core.configuration.generic.entity.environ.Environ;
import com.dcall.core.configuration.generic.entity.environ.EnvironBean;
import com.dcall.core.configuration.generic.entity.user.User;
import com.dcall.core.configuration.utils.FileUtils;
import com.dcall.core.configuration.utils.ResourceUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import java.io.*;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

public class EnvironServiceImpl implements EnvironService {
    private static final Logger LOG = LoggerFactory.getLogger(EnvironServiceImpl.class);
    private final HashServiceProvider hashServiceProvider;

    public EnvironServiceImpl() { this.hashServiceProvider = new HashServiceProvider(); }
    public EnvironServiceImpl(final HashServiceProvider hashServiceProvider) { this.hashServiceProvider = hashServiceProvider; }

    @Override
    public Environ getOrCreateUserEnv(final User user) {
        final Environ<String> env = new EnvironBean();
        final Properties userProps = getOrCreateUserProps(user);

        final Iterator it = userProps.keySet().iterator();

        while (it.hasNext()) {
            final String k = (String) it.next();
            env.getEnv().put(k, userProps.getProperty(k));
        }

        return env;
    }

    public void checkWorkspace(final User user) {
    }

    private Properties getOrCreateUserProps(final User user) {
        final String confDir = getRuntimeConfig();
        final String confPath = getUserHash(user, EnvironConstant.USER_CONF);
        final String homePath = getUserHash(user, user.getPath());

        final String confSalt = getPathSalt(confDir, confPath);

        FileUtils.getInstance().createDirectory(confDir);
        final List<String> conf = hashServiceProvider.hashFileService().createDirectories(confDir, confSalt, confPath);

        final String userPropsPath = hashServiceProvider.hashFileService().getHashPath(conf.get(0), EnvironConstant.USER_PROP_FILENAME, confSalt);
        final File userPropFile = new File(userPropsPath);
        final Properties userProps = new Properties();

        try {
            final SecretKey key = AESProvider.getSecretKey(confPath, confSalt.getBytes());
            if (userPropFile.exists())
                userProps.load(new ByteArrayInputStream(AESProvider.decryptFileBytes(Paths.get(userPropsPath),
                        AESProvider.initCipher(Cipher.DECRYPT_MODE, key))));
            else {
                FileUtils.getInstance().createDirectory(user.getPath());
                final String homeSalt = getPathSalt(user.getPath(), homePath);
                final List<String> home = hashServiceProvider.hashFileService().createDirectories(user.getPath(), homeSalt, homePath);
                final String identityPath = storeUserIdentity(user, home.get(0), homeSalt);

                userProps.setProperty(EnvironConstant.USER_HOME, home.get(0));
                userProps.setProperty(EnvironConstant.USER_CONF, conf.get(0));
                userProps.setProperty(EnvironConstant.USER_IDENTITY_PROP, identityPath);

                userProps.store(new FileWriter(userPropsPath), user.getEmail() + " - env properties");
                AESProvider.encryptFile(userPropsPath, userPropsPath, AESProvider.initCipher(Cipher.ENCRYPT_MODE, key));
            }

            return userProps;
        }
        catch (Exception e) {
            LOG.error(e.getMessage());
        }

        return null;
    }

    private String storeUserIdentity(final User user, final String homeDir, final String homeSalt) {
        final Properties props = new Properties();
        final String identityPath = hashServiceProvider.hashFileService().getHashPath(homeDir, EnvironConstant.USER_IDENTITY_FILENAME, homeSalt);
        final File f = new File(identityPath);

        try {
            if (!f.exists()) {
                final SecretKey key = AESProvider.getSecretKey(identityPath, homeSalt.getBytes());

                props.setProperty(UserConstant.NAME, user.getName());
                props.setProperty(UserConstant.SURNAME, user.getSurname());
                props.setProperty(UserConstant.EMAIL, user.getEmail());
                props.setProperty(UserConstant.LOGIN, user.getLogin());
                props.setProperty(EnvironConstant.USER_HOME, user.getPath());

                props.store(new FileWriter(identityPath), user.getEmail() + " - identity ");
                AESProvider.encryptFile(identityPath, identityPath, AESProvider.initCipher(Cipher.ENCRYPT_MODE, key));
            }
        }
        catch (Exception e) {
            LOG.error(e.getMessage());
        }

        return identityPath;
    }

    private String getPathSalt(String dir, String hash) {
        return HashProvider.seedMd5((dir + File.separator + hash).getBytes());
    }

    private String getUserHash(User user, String path) {
        return HashProvider.signSha512(user.getEmail(), user.getPassword(), path);
    }

    private String getRuntimeConfig() {
        return ResourceUtils.localProperties().getProperty(EnvironConstant.RUNTIME_CONF);
    }
}
