package com.dcall.core.configuration.app.service.certificate;

import com.dcall.core.configuration.app.constant.CertificateConstant;
import com.dcall.core.configuration.app.constant.EnvironConstant;
import com.dcall.core.configuration.app.context.user.UserContext;
import com.dcall.core.configuration.app.provider.hash.HashServiceProvider;
import com.dcall.core.configuration.app.security.aes.AESProvider;
import com.dcall.core.configuration.app.security.rsa.RSAProvider;
import com.dcall.core.configuration.generic.entity.certificate.Certificate;
import com.dcall.core.configuration.generic.entity.certificate.CertificateBean;
import com.dcall.core.configuration.generic.entity.cipher.AbstractCipherResource;
import com.dcall.core.configuration.generic.entity.cipher.CipherAES;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.Cipher;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.security.KeyPair;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableEntryException;
import java.security.cert.CertificateException;

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
    public Certificate createUserCertificate(final UserContext context, final String path, final String salt) {
        try {
            final String certPath = hashServiceProvider.hashFileService().getHashPath(path, context.getUserHash().getMd5Salt(), EnvironConstant.USER_KEYSTORE_FILENAME);

            if (!new File(certPath).exists()) {
                final CipherAES<String> cipher = hashServiceProvider.cipherService().createCipherAES(salt, EnvironConstant.USER_KEYSTORE_FILENAME, certPath, null);

                RSAProvider.createKeyStore(
                        RSAProvider.KeyStoreType.PKCS12,
                        certPath,
                        context.getUser().getEmail(),
                        RSAProvider.generateKeyPair(),
                        CertificateConstant.ONE_MILLENIUM,
                        context.getUserHash().saltResource(CertificateConstant.CERT_PUB_DOMAIN),
                        context.getUserHash().saltResource(CertificateConstant.DEFAULT_STORE_PASS),
                        context.getUserHash().saltResource(CertificateConstant.DEFAULT_KEY_PASS));

                AESProvider.encryptFile(certPath, certPath, cipher.getCipherIn());

                return new CertificateBean(certPath, cipher);
            }
            else {
                final CipherAES<String> cipher = hashServiceProvider.cipherService().createCipherAES(salt, EnvironConstant.USER_KEYSTORE_FILENAME, certPath, Cipher.DECRYPT_MODE);
                return getUserCertificate(context, new CertificateBean(certPath, cipher));
            }
        } catch (Exception e) {
            LOG.error(e.getMessage());
        }

        return null;
    }

    @Override
    public Certificate getUserCertificate(final UserContext context, final String path, final String salt) {
        try {
            if (new File(path).exists()) {
                final CipherAES<String> cipher = hashServiceProvider.cipherService().createCipherAES(salt, EnvironConstant.USER_KEYSTORE_FILENAME, path, Cipher.DECRYPT_MODE);

                return new CertificateBean(path, cipher, loadUserKeyPair(context, path, cipher));
            }
        }
        catch (Exception e) {
            LOG.error(e.getMessage());
        }

        return null;
    }

    @Override
    public Certificate getUserCertificate(final UserContext context, final Certificate certificate) {
        final AbstractCipherResource<String> cert = (AbstractCipherResource<String>) certificate;
        try {
            if (new File(cert.getPath()).exists())
                return certificate.setKeyPair(loadUserKeyPair(context, cert.getPath(), cert.getCipher()));
        }
        catch (Exception e) {
            LOG.error(e.getMessage());
        }

        return null;
    }

    private KeyPair loadUserKeyPair(final UserContext context, final String certPath, final CipherAES<String> cipher) throws KeyStoreException, IOException, NoSuchAlgorithmException, CertificateException, UnrecoverableEntryException {
        return RSAProvider.getKeyPairFromInputStreamKeyStore(
                new ByteArrayInputStream(AESProvider.decryptFileBytes(Paths.get(certPath), cipher.getCipherOut())),
                RSAProvider.KeyStoreType.PKCS12,
                context.getUserHash().saltResource(CertificateConstant.CERT_PUB_DOMAIN),
                context.getUserHash().saltResource(CertificateConstant.DEFAULT_STORE_PASS),
                context.getUserHash().saltResource(CertificateConstant.DEFAULT_KEY_PASS)
        );
    }
}
