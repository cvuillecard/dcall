package com.dcall.core.configuration.generic.entity.crypto;

import com.dcall.core.configuration.app.security.aes.AESProvider;
import com.dcall.core.configuration.generic.entity.Entity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;

public class CryptoAESBean implements CryptoAES<String> {
    private static final Logger LOG = LoggerFactory.getLogger(CryptoAESBean.class);

    private String id;
    private Cipher cipherIn;
    private Cipher cipherOut;

    public CryptoAESBean() {}
    public CryptoAESBean(final String password, final String salt) {
        try {
            final SecretKey key = AESProvider.getSecretKey(password, salt.getBytes());
            cipherIn = AESProvider.initCipher(Cipher.ENCRYPT_MODE, key);
            cipherOut = AESProvider.initCipher(Cipher.DECRYPT_MODE, key);
        } catch (Exception e) {
            LOG.error(e.getMessage());
        }
    }

    public CryptoAESBean(final String password, final String salt, final int encryptMode) {
        try {
            final SecretKey key = AESProvider.getSecretKey(password, salt.getBytes());

            if (encryptMode == Cipher.ENCRYPT_MODE)
                cipherIn = AESProvider.initCipher(Cipher.ENCRYPT_MODE, key);
            else
                cipherOut = AESProvider.initCipher(Cipher.DECRYPT_MODE, key);
        } catch (Exception e) {
            LOG.error(e.getMessage());
        }
    }

    // getters
    @Override public String getId() { return id; }
    @Override public Cipher getCipherIn() { return cipherIn; }
    @Override public Cipher getCipherOut() { return cipherOut; }

    // setters
    @Override public Entity<String> setId(final String id) { this.id = id; return this; }
}
