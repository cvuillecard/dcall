package com.dcall.core.configuration.generic.entity.crypto;

import com.dcall.core.configuration.generic.entity.Entity;

import javax.crypto.Cipher;

public interface CryptoAES<ID> extends Entity<ID> {
    Cipher getCipherIn();
    Cipher getCipherOut();
}
