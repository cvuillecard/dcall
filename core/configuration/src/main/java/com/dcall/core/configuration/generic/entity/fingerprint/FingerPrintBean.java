package com.dcall.core.configuration.generic.entity.fingerprint;

import com.dcall.core.configuration.generic.entity.Entity;
import com.dcall.core.configuration.generic.entity.cipher.AbstractCipherResource;
import com.dcall.core.configuration.generic.entity.cipher.CipherAES;

import java.security.PrivateKey;
import java.security.PublicKey;

public class FingerPrintBean extends AbstractCipherResource<String> implements FingerPrint<String> {
    private String id;
    private PublicKey publicKey;
    private PrivateKey privateKey;
    private CipherAES<String> cipherAES;

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
    @Override public CipherAES<String> getCipherAES() { return this.cipherAES; }

    // setters
    @Override public Entity<String> setId(final String id) { this.id = id; return this; }
    @Override public FingerPrint<String> setPublicKey(final PublicKey publicKey) { this.publicKey = publicKey; return this; }
    @Override public FingerPrint<String> setPrivateKey(final PrivateKey privateKey) { this.privateKey = privateKey; return this; }
    @Override public FingerPrint<String> setCipherAES(final CipherAES<String> cipherAES) { this.cipherAES = cipherAES; return this; }
}
