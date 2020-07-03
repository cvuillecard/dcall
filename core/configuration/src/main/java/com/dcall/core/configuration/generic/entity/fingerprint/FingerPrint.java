package com.dcall.core.configuration.generic.entity.fingerprint;

import com.dcall.core.configuration.generic.entity.Entity;
import com.dcall.core.configuration.generic.entity.cipher.CipherAES;

import java.security.PrivateKey;
import java.security.PublicKey;

public interface FingerPrint<ID> extends Entity<ID> {
    PublicKey getPublicKey();
    PrivateKey getPrivateKey();
    CipherAES<ID> getCipherAES();

    FingerPrint<ID> setPublicKey(final PublicKey publicKey);
    FingerPrint<ID> setPrivateKey(final PrivateKey privateKey);
    FingerPrint<ID> setCipherAES(final CipherAES<ID> cipherAES);
}
