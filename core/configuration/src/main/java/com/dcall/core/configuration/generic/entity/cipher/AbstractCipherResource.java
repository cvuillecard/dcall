package com.dcall.core.configuration.generic.entity.cipher;

import com.dcall.core.configuration.generic.entity.Entity;

public abstract class AbstractCipherResource<ID> implements CipherResource<ID> {
    private ID id;
    private String path;
    private CipherAES<ID> cipher;

    public AbstractCipherResource() {}

    public AbstractCipherResource(final String path, final CipherAES<ID> cipherAES) {
        this.path = path;
        this.cipher = cipherAES;
    }

    public AbstractCipherResource(final ID id, final String path, final CipherAES<ID> cipherAES) {
        this.id = id;
        this.path = path;
        this.cipher = cipherAES;
    }

    // getters
    @Override public ID getId() { return this.id; }
    @Override public String getPath() { return this.path; }
    @Override public CipherAES<ID> getCipher() { return this.cipher; }

    // setters
    @Override public Entity<ID> setId(final ID id) { this.id = id; return this; }
    @Override public CipherResource<ID> setPath(final String path) { this.path = path; return this; }
    @Override public CipherResource<ID> setCipher(final CipherAES<ID> cipher) { this.cipher = cipher; return this; }
}
