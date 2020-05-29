package com.dcall.core.configuration.app.context.crypto;

import com.dcall.core.configuration.generic.entity.crypto.CryptoAES;
import com.dcall.core.configuration.generic.entity.crypto.CryptoAESBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class CryptoContext {
    private static final Logger LOG = LoggerFactory.getLogger(CryptoContext.class);

    private CryptoAES local = new CryptoAESBean();

    // getters
    public CryptoAES getLocal() { return local; }

    // setters
    public CryptoAES setLocal(final String password, final String salt) {
        this.local = new CryptoAESBean(password, salt);

        return getLocal();
    }
}
