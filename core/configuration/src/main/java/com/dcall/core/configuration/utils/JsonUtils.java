package com.dcall.core.configuration.utils;

import com.dcall.core.configuration.app.exception.TechnicalException;
import com.dcall.core.configuration.app.security.aes.AESProvider;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.Cipher;

public final class JsonUtils {
    private static final Logger LOG = LoggerFactory.getLogger(JsonUtils.class);
    private static final ObjectMapper mapper = new ObjectMapper();

    final static byte[] objToByteArray(final Object o, final Cipher cipher) {
        try {
            final byte[] bytes = mapper.writeValueAsBytes(o);
            return cipher != null ? AESProvider.encryptBytes(bytes, cipher) : bytes;
        } catch (Exception e) {
            new TechnicalException(e).log();
        }

        return null;
    }

    final static <T> T byteArrayToObj(final byte[] bytes, Class<T> type, final Cipher cipher) {
        try {
            return cipher != null ? mapper.readValue(AESProvider.decryptBytes(bytes, cipher), type) : mapper.readValue(bytes, type);
        } catch (Exception e) {
            new TechnicalException(e).log();
        }

        return null;
    }
}
