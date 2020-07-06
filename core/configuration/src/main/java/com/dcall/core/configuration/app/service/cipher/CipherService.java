package com.dcall.core.configuration.app.service.cipher;

import com.dcall.core.configuration.app.entity.cipher.CipherAES;

public interface CipherService {
    CipherAES createCipherAES(final String salt, final String key, final String targetPath, final Integer encryptMode);
}
