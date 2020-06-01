package com.dcall.core.configuration.generic.entity.certificate;

import java.security.KeyPair;

public interface Certificate {
    KeyPair getKeyPair();
    Certificate setKeyPair(final KeyPair keyPair);
}
