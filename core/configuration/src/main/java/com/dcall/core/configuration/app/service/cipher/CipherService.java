package com.dcall.core.configuration.app.service.cipher;

import com.dcall.core.configuration.generic.entity.cipher.CipherAESBean;

public interface CipherService {
    CipherAESBean createCipherResource(final String salt, final String key, final String targetPath, final Integer encryptMode);
}
