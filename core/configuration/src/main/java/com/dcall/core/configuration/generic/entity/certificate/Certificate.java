package com.dcall.core.configuration.generic.entity.certificate;

import java.io.Serializable;
import java.security.KeyPair;

public interface Certificate extends Serializable {
    KeyPair getKeyPair();
    Certificate setKeyPair(final KeyPair keyPair);
}
