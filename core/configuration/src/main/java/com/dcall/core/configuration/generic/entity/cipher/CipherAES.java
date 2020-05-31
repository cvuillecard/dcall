package com.dcall.core.configuration.generic.entity.cipher;

import com.dcall.core.configuration.generic.entity.Entity;

import javax.crypto.Cipher;

public interface CipherAES<ID> extends Entity<ID> {
    Cipher getCipherIn();
    Cipher getCipherOut();
}
