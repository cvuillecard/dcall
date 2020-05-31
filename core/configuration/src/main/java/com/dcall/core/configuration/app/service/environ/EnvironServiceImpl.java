package com.dcall.core.configuration.app.service.environ;

import com.dcall.core.configuration.app.constant.EnvironConstant;
import com.dcall.core.configuration.app.constant.SaltDef;
import com.dcall.core.configuration.app.constant.UserConstant;
import com.dcall.core.configuration.app.context.user.UserContext;
import com.dcall.core.configuration.app.provider.hash.HashServiceProvider;
import com.dcall.core.configuration.app.security.aes.AESProvider;
import com.dcall.core.configuration.app.security.hash.HashProvider;
import com.dcall.core.configuration.app.service.hash.HashFileService;
import com.dcall.core.configuration.generic.entity.crypto.CryptoAES;
import com.dcall.core.configuration.generic.entity.crypto.CryptoAESBean;
import com.dcall.core.configuration.generic.entity.environ.Environ;
import com.dcall.core.configuration.generic.entity.environ.EnvironBean;
import com.dcall.core.configuration.generic.entity.hash.UserHash;
import com.dcall.core.configuration.generic.entity.hash.UserHashBean;
import com.dcall.core.configuration.generic.entity.user.User;
import com.dcall.core.configuration.utils.FileUtils;
import com.dcall.core.configuration.utils.ResourceUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.Cipher;
import java.io.*;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

public class EnvironServiceImpl implements EnvironService {
    private static final Logger LOG = LoggerFactory.getLogger(EnvironServiceImpl.class);
    private final HashServiceProvider hashServiceProvider;

    public EnvironServiceImpl() { this.hashServiceProvider = new HashServiceProvider(); }
    public EnvironServiceImpl(final HashServiceProvider hashServiceProvider) { this.hashServiceProvider = hashServiceProvider; }

    @Override
    public Environ configureEnviron(final UserContext context, final boolean create) {
        final Environ<String> env = new EnvironBean();
        final Properties userProps = createUserProps(context, create);

        final Iterator it = userProps.keySet().iterator();

        while (it.hasNext()) {
            final String k = (String) it.next();
            env.getEnv().put(k, userProps.getProperty(k));
        }

        return context.setEnviron(env).getEnviron();
    }

    @Override
    public boolean hasConfiguration(final UserContext context) {
        return hashServiceProvider.hashFileService().exists(
                initHash(context).getPwd(),
                context.getUserHash().getMd5Salt(),
                context.getUserHash().saltResource(EnvironConstant.USER_CONF)
        );
    }

    private Properties createUserProps(final UserContext context, final boolean create) {
        if (create) {
            FileUtils.getInstance().createDirectory(getConfigDirectory());
            initHash(context);
        }

        final UserHash<String> userHash = context.getUserHash();
        final HashFileService hashService = hashServiceProvider.hashFileService();
        final String userPwd = create ?
                hashService.createDirectories(userHash.getPwd(), userHash.getSalt(), userHash.saltResource(EnvironConstant.USER_CONF)).get(0) :
                hashService.getHashPath(userHash.getPwd(), userHash.getMd5Salt(), userHash.saltResource(EnvironConstant.USER_CONF));

        final String userPropsPath = hashService.getHashPath(userPwd, userHash.getMd5Salt(), EnvironConstant.USER_PROP_FILENAME);
        final File userPropFile = new File(userPropsPath);
        final Properties userProps = new Properties();

        try {
            final CryptoAES<String> cipher = getEnvironFileCipher(userHash.getSalt(), EnvironConstant.USER_PROP_FILENAME, userPropsPath, null);

            if (userPropFile.exists())
                userProps.load(new ByteArrayInputStream(AESProvider.decryptFileBytes(Paths.get(userPropsPath), cipher.getCipherOut())));
            else {
                FileUtils.getInstance().createDirectory(context.getUser().getPath());
                final List<String> userHome = hashService.createDirectories(context.getUser().getPath(), userHash.getSalt(), userHash.saltResource(EnvironConstant.USER_HOME));
                final String identityPath = storeUserIdentity(context.getUser(), userHome.get(0), userHash.getSalt());

                userProps.setProperty(EnvironConstant.USER_HOME, userHome.get(0));
                userProps.setProperty(EnvironConstant.USER_CONF, userPwd);
                userProps.setProperty(EnvironConstant.USER_IDENTITY_PROP, identityPath);

                userProps.store(new FileWriter(userPropsPath), context.getUser().getEmail() + " - env properties");
                AESProvider.encryptFile(userPropsPath, userPropsPath, cipher.getCipherIn());
                // verification
                // hashServiceProvider.hashFileService().exists(user.getPath(), md5Salt, userHash.saltResource(EnvironConstant.USER_HOME))
            }

            return userProps;
        }
        catch (Exception e) {
            LOG.error(e.getMessage());
        }

        return null;
    }

    private String storeUserIdentity(final User user, final String homeDir, final String salt) {
        final Properties props = new Properties();
        final String identityPath = hashServiceProvider.hashFileService().getHashPath(homeDir, hashServiceProvider.hashFileService().seed(salt), EnvironConstant.USER_IDENTITY_FILENAME);
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

    private CryptoAESBean getEnvironFileCipher(final String salt, final String key, final String targetPath, final Integer encryptMode) {
        if (encryptMode == null)
            return new CryptoAESBean(HashProvider.signSha512(salt, key), targetPath);
        else
            return new CryptoAESBean(HashProvider.signSha512(salt, key), targetPath, encryptMode);
    }

    private String getConfigDirectory() {
        return ResourceUtils.localProperties().getProperty(EnvironConstant.RUNTIME_CONF);
    }

    private UserHash<String> initHash(final UserContext context) {
        return context.setUserHash(hashServiceProvider.hashService().createUserHash(context.getUser(), getConfigDirectory(), SaltDef.SALT_USER)).getUserHash();
    }

    @Override public HashServiceProvider getHashServiceProvider() { return hashServiceProvider; }
}
