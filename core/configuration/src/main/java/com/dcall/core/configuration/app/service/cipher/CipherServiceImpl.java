package com.dcall.core.configuration.app.service.cipher;

import com.dcall.core.configuration.app.security.hash.HashProvider;
import com.dcall.core.configuration.app.entity.cipher.CipherAESBean;

public class CipherServiceImpl implements CipherService {
    @Override
    public CipherAESBean createCipherAES(final String salt, final String key, final String resource, final Integer encryptMode) {
        if (encryptMode == null)
            return new CipherAESBean(HashProvider.signSha512(salt, key), resource);
        else
            return new CipherAESBean(HashProvider.signSha512(salt, key), resource, encryptMode);
    }
}
