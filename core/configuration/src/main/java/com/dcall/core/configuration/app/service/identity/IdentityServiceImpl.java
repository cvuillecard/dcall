package com.dcall.core.configuration.app.service.identity;

import com.dcall.core.configuration.app.constant.EnvironConstant;
import com.dcall.core.configuration.app.constant.UserConstant;
import com.dcall.core.configuration.app.context.user.UserContext;
import com.dcall.core.configuration.app.provider.hash.HashServiceProvider;
import com.dcall.core.configuration.app.security.aes.AESProvider;
import com.dcall.core.configuration.generic.entity.cipher.AbstractCipherResource;
import com.dcall.core.configuration.generic.entity.cipher.CipherAES;
import com.dcall.core.configuration.generic.entity.identity.Identity;
import com.dcall.core.configuration.generic.entity.identity.IdentityBean;
import com.dcall.core.configuration.generic.entity.user.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.Cipher;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileWriter;
import java.nio.file.Paths;
import java.util.Properties;

public class IdentityServiceImpl implements IdentityService {
    private static final Logger LOG = LoggerFactory.getLogger(IdentityServiceImpl.class);
    private final HashServiceProvider hashServiceProvider;

    public IdentityServiceImpl() { this.hashServiceProvider = new HashServiceProvider(); }
    public IdentityServiceImpl(final HashServiceProvider hashServiceProvider) { this.hashServiceProvider = hashServiceProvider; }

    @Override
    public Identity createUserIdentity(final UserContext context, final String path, final String salt) {
        final String identityPath = hashServiceProvider.hashFileService().getHashPath(path, context.getUserHash().getMd5Salt(), EnvironConstant.USER_IDENTITY_FILENAME);
        final CipherAES<String> cipher = hashServiceProvider.cipherService().createCipherResource(salt, EnvironConstant.USER_IDENTITY_FILENAME, identityPath, null);
        final Identity identity = new IdentityBean(identityPath, cipher, context.getUser());

        final File f = new File(identityPath);

        try {
            if (!f.exists()) {
                identity.getProperties().store(new FileWriter(identityPath), context.getUser().getEmail() + " - identity ");

                AESProvider.encryptFile(identityPath, identityPath, cipher.getCipherIn());

                return identity;
            }
            else
                return getUserIdentity(identity);

        } catch (Exception e) {
            LOG.error(e.getMessage());
        }

        return null;
    }

    @Override
    public Identity getUserIdentity(final Identity identity) {
        try {
            final Properties props = new Properties();
            final AbstractCipherResource<String> cipherResource = (AbstractCipherResource<String>) identity;
            props.load(new ByteArrayInputStream(AESProvider.decryptFileBytes(Paths.get(cipherResource.getPath()), cipherResource.getCipher().getCipherOut())));

            return identity.setProperties(props);
        }
        catch (Exception e) {
            LOG.error(e.getMessage());
        }

        return null;
    }
}
