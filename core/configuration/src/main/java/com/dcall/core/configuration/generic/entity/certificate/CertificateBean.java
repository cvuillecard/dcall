package com.dcall.core.configuration.generic.entity.certificate;

import com.dcall.core.configuration.generic.entity.cipher.AbstractCipherResource;
import com.dcall.core.configuration.generic.entity.cipher.CipherAES;

import java.security.KeyPair;

public final class CertificateBean extends AbstractCipherResource<String> implements Certificate {
    private KeyPair keyPair;

    public CertificateBean() { super(); }

    public CertificateBean(final String path, final CipherAES<String> cipherAES) { super(path, cipherAES); }

    public CertificateBean(final String path, final CipherAES<String> cipherAES, final KeyPair keyPair) {
        super(path, cipherAES);
        this.keyPair = keyPair;
    }

    public CertificateBean(final String id, final String path, final CipherAES<String> cipherAES, final KeyPair keyPair) {
        super(id, path, cipherAES);
        this.keyPair = keyPair;
    }

    @Override public KeyPair getKeyPair() { return this.keyPair; }
    @Override public Certificate setKeyPair(KeyPair keyPair) { this.keyPair = keyPair; return this; }
}
