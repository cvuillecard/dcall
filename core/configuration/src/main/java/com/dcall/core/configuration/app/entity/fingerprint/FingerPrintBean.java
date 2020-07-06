package com.dcall.core.configuration.app.entity.fingerprint;

import com.dcall.core.configuration.app.entity.Entity;
import com.dcall.core.configuration.app.entity.cipher.AbstractCipherResource;

import javax.crypto.SecretKey;
import java.security.PrivateKey;
import java.security.PublicKey;

public class FingerPrintBean extends AbstractCipherResource<String> implements FingerPrint<String> {
    private String id;
    private PublicKey publicKey;
    private PrivateKey privateKey;
    private SecretKey secretKey;

    public FingerPrintBean() {}

    public FingerPrintBean(final String id, final PublicKey publicKey) { this.id = id; this.publicKey = publicKey; }

    public FingerPrintBean(final String id, final PublicKey publicKey, final PrivateKey privateKey) {
        this.id = id;
        this.publicKey = publicKey;
        this.privateKey = privateKey;
    }

    // getters
    @Override public String getId() { return this.id; }
    @Override public PublicKey getPublicKey() { return this.publicKey;}
    @Override public PrivateKey getPrivateKey() { return this.privateKey;}
    @Override public SecretKey getSecretKey() { return this.secretKey; }

    // setters
    @Override public Entity<String> setId(final String id) { this.id = id; return this; }
    @Override public FingerPrint<String> setPublicKey(final PublicKey publicKey) { this.publicKey = publicKey; return this; }
    @Override public FingerPrint<String> setPrivateKey(final PrivateKey privateKey) { this.privateKey = privateKey; return this; }
    @Override public FingerPrint<String> setSecretKey(final SecretKey secretKey) { this.secretKey = secretKey; return this; }
}
