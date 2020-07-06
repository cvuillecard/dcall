package com.dcall.core.configuration.app.entity.fingerprint;

import com.dcall.core.configuration.app.entity.Entity;

import javax.crypto.SecretKey;
import java.security.PrivateKey;
import java.security.PublicKey;

public interface FingerPrint<ID> extends Entity<ID> {
    PublicKey getPublicKey();
    PrivateKey getPrivateKey();
    SecretKey getSecretKey();

    FingerPrint<ID> setPublicKey(final PublicKey publicKey);
    FingerPrint<ID> setPrivateKey(final PrivateKey privateKey);
    FingerPrint<String> setSecretKey(SecretKey secretKey);
}
