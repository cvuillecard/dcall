package com.dcall.core.configuration.app.service.environ;

import com.dcall.core.configuration.app.constant.EnvironConstant;
import com.dcall.core.configuration.app.constant.SaltDef;
import com.dcall.core.configuration.app.constant.UserConstant;
import com.dcall.core.configuration.app.context.user.UserContext;
import com.dcall.core.configuration.app.provider.hash.HashServiceProvider;
import com.dcall.core.configuration.app.security.aes.AESProvider;
import com.dcall.core.configuration.app.service.hash.HashFileService;
import com.dcall.core.configuration.generic.entity.cipher.CipherAES;
import com.dcall.core.configuration.generic.entity.environ.Environ;
import com.dcall.core.configuration.generic.entity.environ.EnvironBean;
import com.dcall.core.configuration.generic.entity.hash.UserHash;
import com.dcall.core.configuration.generic.entity.user.User;
import com.dcall.core.configuration.utils.FileUtils;
import com.dcall.core.configuration.utils.ResourceUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.Cipher;
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
            final CipherAES<String> cipher = hashServiceProvider.cipherService().createCipherResource(userHash.getSalt(), EnvironConstant.USER_PROP_FILENAME, userPropsPath, null);

            if (userPropFile.exists())
                userProps.load(new ByteArrayInputStream(AESProvider.decryptFileBytes(Paths.get(userPropsPath), cipher.getCipherOut())));
            else {
                FileUtils.getInstance().createDirectory(context.getUser().getPath());
                final String userHome = hashService.createDirectories(context.getUser().getPath(), userHash.getSalt(), userHash.saltResource(EnvironConstant.USER_HOME)).get(0);
                final String userCert = hashService.createDirectories(userHome, userHash.getSalt(), userHash.saltResource(EnvironConstant.USER_CERT)).get(0);
                final String identityPath = hashServiceProvider.identityService().createUserIdentity(context.getUser(), userHome, userHash.getSalt());
                final String certPath = hashServiceProvider.certificateService().createUserCertificate(context, userCert, userHash.getSalt());

                userProps.setProperty(EnvironConstant.USER_HOME, userHome);
                userProps.setProperty(EnvironConstant.USER_CONF, userPwd);
                userProps.setProperty(EnvironConstant.USER_IDENTITY_PROP, identityPath);
                userProps.setProperty(EnvironConstant.USER_CERT, certPath);

                userProps.store(new FileWriter(userPropsPath), context.getUser().getEmail() + " - env properties");
                AESProvider.encryptFile(userPropsPath, userPropsPath, cipher.getCipherIn());
                // verification
                // hashServiceProvider.hashFileService().exists(user.getPath(), userHash.getMd5Salt(), userHash.saltResource(EnvironConstant.USER_HOME))
                // hashService.exists(userHome, userHash.getMd5Salt(), userHash.saltResource(EnvironConstant.USER_CERT))
            }

            return userProps;
        }
        catch (Exception e) {
            LOG.error(e.getMessage());
        }

        return null;
    }

    private String getConfigDirectory() {
        return ResourceUtils.localProperties().getProperty(EnvironConstant.RUNTIME_CONF);
    }

    private UserHash<String> initHash(final UserContext context) {
        return context.setUserHash(hashServiceProvider.hashService().createUserHash(context.getUser(), getConfigDirectory(), SaltDef.SALT_USER)).getUserHash();
    }

    @Override public HashServiceProvider getHashServiceProvider() { return hashServiceProvider; }
}
