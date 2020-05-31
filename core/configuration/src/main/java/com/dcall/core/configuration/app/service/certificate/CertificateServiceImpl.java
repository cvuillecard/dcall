package com.dcall.core.configuration.app.service.certificate;

import com.dcall.core.configuration.app.constant.CertificateConstant;
import com.dcall.core.configuration.app.constant.EnvironConstant;
import com.dcall.core.configuration.app.context.user.UserContext;
import com.dcall.core.configuration.app.provider.hash.HashServiceProvider;
import com.dcall.core.configuration.app.security.aes.AESProvider;
import com.dcall.core.configuration.app.security.rsa.RSAProvider;
import com.dcall.core.configuration.generic.entity.cipher.CipherAES;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.Cipher;
import java.io.File;

public class CertificateServiceImpl implements CertificateService {
    private static final Logger LOG = LoggerFactory.getLogger(CertificateServiceImpl.class);
    private final HashServiceProvider hashServiceProvider;

    public CertificateServiceImpl() {
        this.hashServiceProvider = new HashServiceProvider();
    }

    public CertificateServiceImpl(final HashServiceProvider hashServiceProvider) {
        this.hashServiceProvider = hashServiceProvider;
    }

    @Override
    public String createUserCertificate(final UserContext context, final String path, final String salt) {
        try {
            final String certPath = hashServiceProvider.hashFileService().getHashPath(path, hashServiceProvider.hashFileService().seed(salt), EnvironConstant.USER_KEYSTORE_FILENAME);
            final CipherAES<String> cipher = hashServiceProvider.cipherService().createCipherResource(salt, EnvironConstant.USER_KEYSTORE_FILENAME, certPath, Cipher.ENCRYPT_MODE);

            if (!new File(certPath).exists()) {
                RSAProvider.createKeyStore(
                        RSAProvider.KeyStoreType.PKCS12,
                        certPath,
                        context.getUser().getEmail(),
                        RSAProvider.generateKeyPair(),
                        CertificateConstant.INFINITE_VALIDITY,
                        context.getUserHash().saltResource(CertificateConstant.CERT_PUB_DOMAIN),
                        context.getUserHash().saltResource(CertificateConstant.DEFAULT_STORE_PWD),
                        context.getUserHash().saltResource(CertificateConstant.DEFAULT_KEY_PWD));

                AESProvider.encryptFile(certPath, certPath, cipher.getCipherIn());
            }

            return certPath;

        } catch (Exception e) {
            LOG.error(e.getMessage());
        }

        return null;
    }
}
