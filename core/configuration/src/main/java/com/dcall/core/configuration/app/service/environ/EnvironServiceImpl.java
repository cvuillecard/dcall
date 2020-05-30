package com.dcall.core.configuration.app.service.environ;

import com.dcall.core.configuration.app.constant.EnvironConstant;
import com.dcall.core.configuration.app.constant.UserConstant;
import com.dcall.core.configuration.app.provider.hash.HashServiceProvider;
import com.dcall.core.configuration.app.security.aes.AESProvider;
import com.dcall.core.configuration.app.security.hash.HashProvider;
import com.dcall.core.configuration.generic.entity.crypto.CryptoAES;
import com.dcall.core.configuration.generic.entity.crypto.CryptoAESBean;
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
    public Environ createEnviron(final User user) {
        final Environ<String> env = new EnvironBean();
        final Properties userProps = createUserProps(user);

        final Iterator it = userProps.keySet().iterator();

        while (it.hasNext()) {
            final String k = (String) it.next();
            env.getEnv().put(k, userProps.getProperty(k));
        }

        return env;
    }

    public boolean hasConfiguration(final User user) {
        final String pwd = getRuntimeConfig();
        final String salt = HashProvider.createSalt512(user.getEmail(), user.getPassword());
        final String md5Salt = hashServiceProvider.hashFileService().seed(salt);
        final String configUserPath = HashProvider.createSalt512(salt, EnvironConstant.USER_CONF);

        return hashServiceProvider.hashFileService().exists(pwd, md5Salt, configUserPath);
    }

    private Properties createUserProps(final User user) {
        final String salt = HashProvider.createSalt512(user.getEmail(), user.getPassword());
        final String md5Salt = hashServiceProvider.hashFileService().seed(salt);
        final String pwd = FileUtils.getInstance().createDirectory(getRuntimeConfig());
        final String configUserPath = HashProvider.createSalt512(salt, EnvironConstant.USER_CONF);

        final List<String> userPwd = hashServiceProvider.hashFileService().createDirectories(pwd, salt, configUserPath);

        final String userPropsPath = hashServiceProvider.hashFileService().getHashPath(userPwd.get(0), EnvironConstant.USER_PROP_FILENAME, md5Salt);
        final File userPropFile = new File(userPropsPath);
        final Properties userProps = new Properties();

        try {
            final CryptoAES<String> cipher = getEnvironFileCipher(salt, EnvironConstant.USER_PROP_FILENAME, userPropsPath, null);

            if (userPropFile.exists())
                userProps.load(new ByteArrayInputStream(AESProvider.decryptFileBytes(Paths.get(userPropsPath), cipher.getCipherOut())));
            else {
                FileUtils.getInstance().createDirectory(user.getPath());
                final String homeUserPath = HashProvider.createSalt512(salt, EnvironConstant.USER_HOME);
                final List<String> userHome = hashServiceProvider.hashFileService().createDirectories(user.getPath(), salt, homeUserPath);
                final String identityPath = storeUserIdentity(user, userHome.get(0), salt);

                userProps.setProperty(EnvironConstant.USER_HOME, userHome.get(0));
                userProps.setProperty(EnvironConstant.USER_CONF, userPwd.get(0));
                userProps.setProperty(EnvironConstant.USER_IDENTITY_PROP, identityPath);

                userProps.store(new FileWriter(userPropsPath), user.getEmail() + " - env properties");
                AESProvider.encryptFile(userPropsPath, userPropsPath, cipher.getCipherIn());
                // verification
                // hashServiceProvider.hashFileService().exists(user.getPath(), md5Salt, homeUserPath)

            }

            return userProps;
        }
        catch (Exception e) {
            LOG.error(e.getMessage());
        }

        return null;
    }

    private CryptoAESBean getEnvironFileCipher(final String salt, final String key, final String targetPath, final Integer encryptMode) {
        if (encryptMode == null)
            return new CryptoAESBean(HashProvider.signSha512(salt, key), targetPath);
        else
            return new CryptoAESBean(HashProvider.signSha512(salt, key), targetPath, encryptMode);
    }

    private String storeUserIdentity(final User user, final String homeDir, final String salt) {
        final Properties props = new Properties();
        final String identityPath = hashServiceProvider.hashFileService().getHashPath(homeDir, EnvironConstant.USER_IDENTITY_FILENAME, hashServiceProvider.hashFileService().seed(salt));
        final File f = new File(identityPath);

        try {
            if (!f.exists()) {
                final CryptoAES<String> cipher = getEnvironFileCipher(salt, EnvironConstant.USER_IDENTITY_FILENAME, identityPath, Cipher.ENCRYPT_MODE);
                props.setProperty(UserConstant.NAME, user.getName());
                props.setProperty(UserConstant.SURNAME, user.getSurname());
                props.setProperty(UserConstant.EMAIL, user.getEmail());
                props.setProperty(UserConstant.LOGIN, user.getLogin());
                props.setProperty(EnvironConstant.USER_HOME, user.getPath());

                props.store(new FileWriter(identityPath), user.getEmail() + " - identity ");
                AESProvider.encryptFile(identityPath, identityPath, cipher.getCipherIn());
            }
        }
        catch (Exception e) {
            LOG.error(e.getMessage());
        }

        return identityPath;
    }

    private String getRuntimeConfig() {
        return ResourceUtils.localProperties().getProperty(EnvironConstant.RUNTIME_CONF);
    }

    @Override public HashServiceProvider getHashServiceProvider() { return hashServiceProvider; }
}
