package com.dcall.core.configuration.app.service.environ;

import com.dcall.core.configuration.app.constant.*;
import com.dcall.core.configuration.app.context.RuntimeContext;
import com.dcall.core.configuration.app.context.user.UserContext;
import com.dcall.core.configuration.app.exception.TechnicalException;
import com.dcall.core.configuration.app.provider.hash.HashServiceProvider;
import com.dcall.core.configuration.app.security.aes.AESProvider;
import com.dcall.core.configuration.app.security.hash.HashProvider;
import com.dcall.core.configuration.app.service.hash.HashFileService;
import com.dcall.core.configuration.app.entity.certificate.Certificate;
import com.dcall.core.configuration.app.entity.cipher.AbstractCipherResource;
import com.dcall.core.configuration.app.entity.cipher.CipherAES;
import com.dcall.core.configuration.app.entity.environ.Environ;
import com.dcall.core.configuration.app.entity.environ.EnvironBean;
import com.dcall.core.configuration.app.entity.hash.UserHash;
import com.dcall.core.configuration.app.entity.identity.Identity;
import com.dcall.core.configuration.utils.FileUtils;
import com.dcall.core.configuration.utils.ResourceUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.file.Paths;
import java.util.Properties;

public class EnvironServiceImpl implements EnvironService {
    private static final Logger LOG = LoggerFactory.getLogger(EnvironServiceImpl.class);
    private final HashServiceProvider hashServiceProvider;

    public EnvironServiceImpl() { this.hashServiceProvider = new HashServiceProvider(); }
    public EnvironServiceImpl(final HashServiceProvider hashServiceProvider) { this.hashServiceProvider = hashServiceProvider; }

    @Override
    public Environ createEnviron(final UserContext context, final String path) {
        final String environPath = hashServiceProvider.hashFileService().getHashPath(path, context.getUserHash().getMd5Salt(), EnvironConstant.USER_PROP_FILENAME);
        final CipherAES<String> cipher = hashServiceProvider.cipherService().createCipherAES(context.getUserHash().getSalt(), EnvironConstant.USER_PROP_FILENAME, environPath, null);

        return new EnvironBean(environPath, cipher, context.getUser());
    }

    @Override //todo create PropertyService -> same code in IdentityServiceImpl.updateUserIdentity()
    public Environ updateEnviron(final Environ environ) {
        try {
            if (environ != null && environ instanceof AbstractCipherResource) {
                final AbstractCipherResource resource = (AbstractCipherResource) environ;
                FileUtils.getInstance().remove(resource.getPath());
                environ.getProperties().store(new FileWriter(resource.getPath()), environ.getUser().getEmail() + " - identity ");
                AESProvider.encryptFile(resource.getPath(), resource.getPath(), resource.getCipher().getCipherIn());
            }
        }
        catch (Exception e) {
            LOG.error(e.getMessage());
        }

        return environ;
    }

    @Override
    public boolean hasConfiguration(final UserContext context) {
        return hashServiceProvider.hashFileService().exists(
                initHash(context).getPwd(),
                context.getUserHash().getMd5Salt(),
                context.getUserHash().saltResource(EnvironConstant.USER_CONF)
        );
    }

    @Override
    public Environ configureUserEnviron(final UserContext context, final boolean create) {
        if (create) {
            FileUtils.getInstance().createDirectory(getConfigDirectory());
            initHash(context);
        }
        final UserHash<String> userHash = context.getUserHash();
        final HashFileService hashService = hashServiceProvider.hashFileService();
        final String userPwd = create ?
                hashService.createDirectories(userHash.getPwd(), userHash.getSalt(), userHash.saltResource(EnvironConstant.USER_CONF)).get(0) :
                hashService.getHashPath(userHash.getPwd(), userHash.getMd5Salt(), userHash.saltResource(EnvironConstant.USER_CONF));
        final Environ environ = context.setEnviron(createEnviron(context, userPwd)).getEnviron();
        final AbstractCipherResource cipherEnv = (AbstractCipherResource) environ;

        if (!create)
            environ.setProperties(loadEnvironProperties(context));

        final String userHome = hashService.getHashPath(context.getUser().getWorkspace(), userHash.getMd5Salt(), userHash.saltResource(EnvironConstant.USER_HOME));
        final String userCert = hashService.getHashPath(userHome, userHash.getMd5Salt(), userHash.saltResource(EnvironConstant.USER_CERT));

        try {
            if (create && !(new File(cipherEnv.getPath())).exists()) {
                FileUtils.getInstance().createDirectory(context.getUser().getWorkspace());
               hashService.createDirectories(context.getUser().getWorkspace(), userHash.getSalt(), userHash.saltResource(EnvironConstant.USER_HOME));
               hashService.createDirectories(userHome, userHash.getSalt(), userHash.saltResource(EnvironConstant.USER_CERT));
                final Identity identity = hashServiceProvider.identityService().createUserIdentity(context, userHome);
                final Certificate certificate = hashServiceProvider.certificateService().createUserCertificate(context, userCert);

                context.setIdentity(hashServiceProvider.identityService().getUserIdentity(context, identity))
                        .setCertificate(hashServiceProvider.certificateService().getUserCertificate(context, certificate));

                environ.getProperties().setProperty(EnvironConstant.USER_HOME, userHome);
                environ.getProperties().setProperty(EnvironConstant.USER_WORKSPACE, context.getUser().getWorkspace());
                environ.getProperties().setProperty(EnvironConstant.USER_CONF, userPwd);
                environ.getProperties().setProperty(EnvironConstant.USER_IDENTITY_PROP, ((AbstractCipherResource) identity).getPath());
                environ.getProperties().setProperty(EnvironConstant.USER_CERT, ((AbstractCipherResource) certificate).getPath());
                environ.getProperties().setProperty(EnvironConstant.COMMIT_MODE, String.valueOf(GitCommitMode.MANUAL.mode()));
                environ.getProperties().setProperty(EnvironConstant.INTERPRET_MODE, String.valueOf(InterpretMode.LOCAL.mode()));
                environ.getProperties().setProperty(EnvironConstant.PUBLIC_ID, createPublicId(context));

                environ.getProperties().store(new FileWriter(cipherEnv.getPath()), context.getUser().getEmail() + " - env properties");
                AESProvider.encryptFile(cipherEnv.getPath(), cipherEnv.getPath(), cipherEnv.getCipher().getCipherIn());
                // verification
                // hashServiceProvider.hashFileService().exists(user.getWorkspace(), userHash.getMd5Salt(), userHash.saltResource(EnvironConstant.USER_HOME))
                // hashService.exists(userHome, userHash.getMd5Salt(), userHash.saltResource(EnvironConstant.USER_CERT))
            }
            else
                context.setIdentity(hashServiceProvider.identityService().createUserIdentity(context, userHome))
                    .setCertificate(hashServiceProvider.certificateService().createUserCertificate(context, userCert));


            return environ;
        }
        catch (Exception e) {
            LOG.error(e.getMessage());
        }

        return null;
    }

    @Override
    public String createPublicId(final UserContext context) {
        return HashProvider.signSha512(context.getUserHash().getMd5Salt(), EnvironConstant.PUBLIC_ID);
    }

    @Override
    public Properties loadEnvironProperties(final UserContext context) {
        try {
            final AbstractCipherResource cipherEnv = (AbstractCipherResource) context.getEnviron();
            final Properties props = new Properties();

            props.load(new ByteArrayInputStream(AESProvider.decryptFileBytes(Paths.get(cipherEnv.getPath()), cipherEnv.getCipher().getCipherOut())));
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

    @Override public String getEnvProperty(final Environ environ, final String key) {
        return (key != null && !key.isEmpty() && environ.getProperties().get(key) != null) ? environ.getProperties().get(key).toString() : null;
    }

    @Override
    public Environ setEnvProperty(final Environ environ, final String key, final String value) {
        environ.getProperties().put(key, value);
        return environ;
    }
}
