package com.dcall.core.configuration.app.service.environ;

import com.dcall.core.configuration.app.constant.*;
import com.dcall.core.configuration.app.context.RuntimeContext;
import com.dcall.core.configuration.app.context.user.UserContext;
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
import com.dcall.core.configuration.generic.cluster.hazelcast.HazelcastCluster;
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
                final FileWriter fileWriter = new FileWriter(resource.getPath());
                environ.getProperties().store(fileWriter, environ.getUser().getEmail() + " - environ ");
                fileWriter.close();
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
    public Environ configureUserEnviron(final RuntimeContext runtimeContext, final boolean create) {
        final UserContext userContext = runtimeContext.userContext();

        if (create) {
            FileUtils.getInstance().createDirectory(getConfigDirectory());
            initHash(userContext);
        }
        final UserHash<String> userHash = userContext.getUserHash();
        final HashFileService hashService = hashServiceProvider.hashFileService();
        final String userPwd = create ?
                hashService.createDirectories(userHash.getPwd(), userHash.getSalt(), userHash.saltResource(EnvironConstant.USER_CONF)).get(0) :
                hashService.getHashPath(userHash.getPwd(), userHash.getMd5Salt(), userHash.saltResource(EnvironConstant.USER_CONF));
        final Environ environ = userContext.setEnviron(createEnviron(userContext, userPwd)).getEnviron();
        final AbstractCipherResource cipherEnv = (AbstractCipherResource) environ;

        final String userHome = hashService.getHashPath(userContext.getUser().getWorkspace(), userHash.getMd5Salt(), userHash.saltResource(EnvironConstant.USER_HOME));
        final String userCert = hashService.getHashPath(userHome, userHash.getMd5Salt(), userHash.saltResource(EnvironConstant.USER_CERT));

        try {
            if (create && !(new File(cipherEnv.getPath())).exists()) {
                FileUtils.getInstance().createDirectory(userContext.getUser().getWorkspace());
                hashService.createDirectories(userContext.getUser().getWorkspace(), userHash.getSalt(), userHash.saltResource(EnvironConstant.USER_HOME));
                hashService.createDirectories(userHome, userHash.getSalt(), userHash.saltResource(EnvironConstant.USER_CERT));
                final Identity identity = hashServiceProvider.identityService().createUserIdentity(userContext, userHome);
                final Certificate certificate = hashServiceProvider.certificateService().createUserCertificate(userContext, userCert);
                final FileWriter fileWriter = new FileWriter(cipherEnv.getPath());

                userContext.setIdentity(hashServiceProvider.identityService().getUserIdentity(userContext, identity))
                        .setCertificate(hashServiceProvider.certificateService().getUserCertificate(userContext, certificate));

                environ.getProperties().setProperty(EnvironConstant.USER_HOME, userHome);
                environ.getProperties().setProperty(EnvironConstant.USER_WORKSPACE, userContext.getUser().getWorkspace());
                environ.getProperties().setProperty(EnvironConstant.USER_CONF, userPwd);
                environ.getProperties().setProperty(EnvironConstant.USER_IDENTITY_PROP, ((AbstractCipherResource) identity).getPath());
                environ.getProperties().setProperty(EnvironConstant.USER_CERT, ((AbstractCipherResource) certificate).getPath());
                environ.getProperties().setProperty(EnvironConstant.COMMIT_MODE, GitCommitMode.MANUAL.toString());
                environ.getProperties().setProperty(EnvironConstant.INTERPRET_MODE, InterpretMode.LOCAL.toString());
                environ.getProperties().setProperty(EnvironConstant.PUBLIC_ID, createPublicId(userContext));
                environ.getProperties().setProperty(EnvironConstant.ALLOW_HOST_FILES, AllowHostFilesMode.ON.toString());
                environ.getProperties().setProperty(EnvironConstant.HOST_FILES_DIR, createHostFilesDirectory(userContext));


                environ.getProperties().store(fileWriter, userContext.getUser().getEmail() + " - env properties");
                fileWriter.close();

                AESProvider.encryptFile(cipherEnv.getPath(), cipherEnv.getPath(), cipherEnv.getCipher().getCipherIn());
                // verification
                // hashServiceProvider.hashFileService().exists(user.getWorkspace(), userHash.getMd5Salt(), userHash.saltResource(EnvironConstant.USER_HOME))
                // hashService.exists(userHome, userHash.getMd5Salt(), userHash.saltResource(EnvironConstant.USER_CERT))
            } else {
                environ.setProperties(loadEnvironProperties(userContext));
                userContext.setIdentity(hashServiceProvider.identityService().createUserIdentity(userContext, userHome))
                        .setCertificate(hashServiceProvider.certificateService().createUserCertificate(userContext, userCert));
            }

            runtimeContext.clusterContext().clusterListenerContext().setMemberClusterListener(HazelcastCluster.getMemberShipListener().setRuntimeContext(runtimeContext));

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
    public String createHostFilesDirectory(final UserContext context) {
        final String hostDir = hashServiceProvider.hashFileService().getHashPath(context.getUser().getWorkspace(), context.getUserHash().getMd5Salt(), EnvironConstant.HOST_FILES_DIR_NAME);
        return FileUtils.getInstance().createDirectory(hostDir);
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

    private UserHash<String> initHash(final UserContext context) {
        return context.setUserHash(hashServiceProvider.hashService().createUserHash(context.getUser(), getConfigDirectory(), SaltDef.SALT_USER)).getUserHash();
    }

    // getter
    @Override
    public String getConfigDirName() { return ResourceUtils.localProperties().getProperty(EnvironConstant.RUNTIME_CONF_NAME); }

    @Override
    public String getConfigDirectory() { return ResourceUtils.localProperties().getProperty(EnvironConstant.RUNTIME_CONF_PATH); }

    @Override
    public String getPublicId(final RuntimeContext runtimeContext) {
        return runtimeContext.userContext().getEnviron().getProperties().getProperty(EnvironConstant.PUBLIC_ID);
    }

    @Override
    public String getHostFilesDirectory(final RuntimeContext runtimeContext) {
        return runtimeContext.userContext().getEnviron().getProperties().getProperty(EnvironConstant.HOST_FILES_DIR);
    }

    @Override
    public boolean getInterpretMode(final RuntimeContext runtimeContext) {
        return Boolean.valueOf(runtimeContext.userContext().getEnviron().getProperties().getProperty(EnvironConstant.INTERPRET_MODE));
    }

    @Override
    public boolean getAutoCommitMode(final RuntimeContext runtimeContext) {
        return Boolean.valueOf(runtimeContext.userContext().getEnviron().getProperties().getProperty(EnvironConstant.COMMIT_MODE));
    }

    @Override
    public boolean getHostFilesMode(final RuntimeContext runtimeContext) {
        return Boolean.valueOf(runtimeContext.userContext().getEnviron().getProperties().getProperty(EnvironConstant.ALLOW_HOST_FILES));
    }

    @Override public String getEnvProperty(final Environ environ, final String key) {
        return (key != null && !key.isEmpty() && environ.getProperties().get(key) != null) ? environ.getProperties().get(key).toString() : null;
    }

    @Override public HashServiceProvider getHashServiceProvider() { return hashServiceProvider; }

    @Override
    public String getHostedUserPath(final RuntimeContext runtimeContext, final String publicId) {
        return hashServiceProvider.hashFileService().getHashPath(getHostFilesDirectory(runtimeContext), runtimeContext.userContext().getUserHash().getMd5Salt(), publicId);
    }

    @Override
    public String getHostedFilePath(final RuntimeContext runtimeContext, final String publicId, final String fileName) {
        return hashServiceProvider.hashFileService().getHashPath(getHostedUserPath(runtimeContext, publicId), runtimeContext.userContext().getUserHash().getMd5Salt(), fileName);
    }

    // setter
    @Override
    public Environ setEnvProperty(final Environ environ, final String key, final String value) {
        environ.getProperties().put(key, value);
        return environ;
    }
}
