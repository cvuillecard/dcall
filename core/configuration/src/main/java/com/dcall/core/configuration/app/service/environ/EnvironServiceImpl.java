package com.dcall.core.configuration.app.service.environ;

import com.dcall.core.configuration.app.constant.EnvironConstant;
import com.dcall.core.configuration.app.constant.SaltDef;
import com.dcall.core.configuration.app.context.user.UserContext;
import com.dcall.core.configuration.app.provider.hash.HashServiceProvider;
import com.dcall.core.configuration.app.security.aes.AESProvider;
import com.dcall.core.configuration.app.service.hash.HashFileService;
import com.dcall.core.configuration.generic.entity.certificate.Certificate;
import com.dcall.core.configuration.generic.entity.cipher.AbstractCipherResource;
import com.dcall.core.configuration.generic.entity.cipher.CipherAES;
import com.dcall.core.configuration.generic.entity.environ.Environ;
import com.dcall.core.configuration.generic.entity.environ.EnvironBean;
import com.dcall.core.configuration.generic.entity.hash.UserHash;
import com.dcall.core.configuration.generic.entity.identity.Identity;
import com.dcall.core.configuration.utils.FileUtils;
import com.dcall.core.configuration.utils.ResourceUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.file.Paths;
import java.util.Iterator;
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
        final CipherAES<String> cipher = hashServiceProvider.cipherService().createCipherResource(userHash.getSalt(), EnvironConstant.USER_PROP_FILENAME, userPropsPath, null);

        final File userPropFile = new File(userPropsPath);
        Properties userProps = new Properties();

        if (!create)
            userProps = loadUserProperties(context, userPropsPath, cipher);

        final String userHome = hashService.getHashPath(context.getUser().getWorkspace(), userHash.getMd5Salt(), userHash.saltResource(EnvironConstant.USER_HOME));
        final String userCert = hashService.getHashPath(userHome, userHash.getMd5Salt(), userHash.saltResource(EnvironConstant.USER_CERT));

        try {
            if (create && !userPropFile.exists()) {
                FileUtils.getInstance().createDirectory(context.getUser().getWorkspace());
               hashService.createDirectories(context.getUser().getWorkspace(), userHash.getSalt(), userHash.saltResource(EnvironConstant.USER_HOME));
               hashService.createDirectories(userHome, userHash.getSalt(), userHash.saltResource(EnvironConstant.USER_CERT));
                final Identity identity = hashServiceProvider.identityService().createUserIdentity(context, userHome, userHash.getSalt());
                final Certificate certificate = hashServiceProvider.certificateService().createUserCertificate(context, userCert, userHash.getSalt());

                context.setIdentity(hashServiceProvider.identityService().getUserIdentity(context, identity))
                        .setCertificate(hashServiceProvider.certificateService().getUserCertificate(context, certificate));

                userProps.setProperty(EnvironConstant.USER_HOME, userHome);
                userProps.setProperty(EnvironConstant.USER_WORKSPACE, context.getUser().getWorkspace());
                userProps.setProperty(EnvironConstant.USER_CONF, userPwd);
                userProps.setProperty(EnvironConstant.USER_IDENTITY_PROP, ((AbstractCipherResource<String>) identity).getPath());
                userProps.setProperty(EnvironConstant.USER_CERT, ((AbstractCipherResource<String>) certificate).getPath());

                userProps.store(new FileWriter(userPropsPath), context.getUser().getEmail() + " - env properties");
                AESProvider.encryptFile(userPropsPath, userPropsPath, cipher.getCipherIn());
                // verification
                // hashServiceProvider.hashFileService().exists(user.getWorkspace(), userHash.getMd5Salt(), userHash.saltResource(EnvironConstant.USER_HOME))
                // hashService.exists(userHome, userHash.getMd5Salt(), userHash.saltResource(EnvironConstant.USER_CERT))
            }
            else
                context.setIdentity(hashServiceProvider.identityService().createUserIdentity(context, userHome, userHash.getSalt()))
                    .setCertificate(hashServiceProvider.certificateService().createUserCertificate(context, userCert, userHash.getSalt()));


            return userProps;
        }
        catch (Exception e) {
            LOG.error(e.getMessage());
        }

        return null;
    }

    private Properties loadUserProperties(final UserContext context, final String userPropsPath, final CipherAES<String> cipher) {
        try {
            final Properties props = new Properties();

            props.load(new ByteArrayInputStream(AESProvider.decryptFileBytes(Paths.get(userPropsPath), cipher.getCipherOut())));
            context.getUser().setWorkspace(props.getProperty(EnvironConstant.USER_WORKSPACE));

            return props;
        }
        catch (Exception e) {
            LOG.error(e.getMessage());
        }

        return null;
    }

    @Override
    public String getConfigDirName() { return ResourceUtils.localProperties().getProperty(EnvironConstant.RUNTIME_CONF_NAME); }

    @Override
    public String getConfigDirectory() { return ResourceUtils.localProperties().getProperty(EnvironConstant.RUNTIME_CONF_PATH); }

    private UserHash<String> initHash(final UserContext context) {
        return context.setUserHash(hashServiceProvider.hashService().createUserHash(context.getUser(), getConfigDirectory(), SaltDef.SALT_USER)).getUserHash();
    }

    @Override public HashServiceProvider getHashServiceProvider() { return hashServiceProvider; }
}
