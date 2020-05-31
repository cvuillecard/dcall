package com.dcall.core.configuration.app.service.identity;

import com.dcall.core.configuration.app.constant.EnvironConstant;
import com.dcall.core.configuration.app.constant.UserConstant;
import com.dcall.core.configuration.app.provider.hash.HashServiceProvider;
import com.dcall.core.configuration.app.security.aes.AESProvider;
import com.dcall.core.configuration.generic.entity.cipher.CipherAES;
import com.dcall.core.configuration.generic.entity.user.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.Cipher;
import java.io.File;
import java.io.FileWriter;
import java.util.Properties;

public class IdentityServiceImpl implements IdentityService {
    private static final Logger LOG = LoggerFactory.getLogger(IdentityServiceImpl.class);
    private final HashServiceProvider hashServiceProvider;

    public IdentityServiceImpl() { this.hashServiceProvider = new HashServiceProvider(); }
    public IdentityServiceImpl(final HashServiceProvider hashServiceProvider) { this.hashServiceProvider = hashServiceProvider; }

    @Override
    public String createUserIdentity(final User user, final String path, final String salt) { //todo create identity Bean ?
        final Properties props = new Properties();
        final String identityPath = hashServiceProvider.hashFileService().getHashPath(path, hashServiceProvider.hashFileService().seed(salt), EnvironConstant.USER_IDENTITY_FILENAME);
        final File f = new File(identityPath);

        try {
            if (!f.exists()) {
                final CipherAES<String> cipher = hashServiceProvider.cipherService().createCipherResource(salt, EnvironConstant.USER_IDENTITY_FILENAME, identityPath, Cipher.ENCRYPT_MODE);
                props.setProperty(UserConstant.NAME, user.getName());
                props.setProperty(UserConstant.SURNAME, user.getSurname());
                props.setProperty(UserConstant.EMAIL, user.getEmail());
                props.setProperty(UserConstant.LOGIN, user.getLogin());
                props.setProperty(EnvironConstant.USER_HOME, user.getPath());

                props.store(new FileWriter(identityPath), user.getEmail() + " - identity ");
                AESProvider.encryptFile(identityPath, identityPath, cipher.getCipherIn());
            }
        } catch (Exception e) {
            LOG.error(e.getMessage());
        }
        return identityPath;
    }
}
