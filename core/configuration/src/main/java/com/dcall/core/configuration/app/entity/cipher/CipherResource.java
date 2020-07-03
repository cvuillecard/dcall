package com.dcall.core.configuration.app.entity.cipher;

import com.dcall.core.configuration.app.entity.Entity;

public interface CipherResource<ID> extends Entity<ID> {
    String getPath();
    CipherAES<ID> getCipher();

    CipherResource<ID> setPath(final String path);
    CipherResource<ID> setCipher(final CipherAES<ID> cipher);
}
