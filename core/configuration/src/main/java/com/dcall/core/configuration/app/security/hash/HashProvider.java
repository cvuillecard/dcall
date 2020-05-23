package com.dcall.core.configuration.app.security.hash;

import org.apache.commons.codec.digest.DigestUtils;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.security.SecureRandom;
import java.util.Arrays;

public final class HashProvider {
    private static int _KEY_LENGTH = 512;
    private static String _HMAC_SHA_512 = "HmacSHA512";

    public static byte[] random() {
        final byte[] key = new byte[_KEY_LENGTH];
        new SecureRandom().nextBytes(key);

        return key;
    }

    public static byte[] hmac(final byte[] data, final byte[] key) throws Exception {
        final Mac mac = Mac.getInstance(_HMAC_SHA_512);
        mac.init(new SecretKeySpec(key, _HMAC_SHA_512));

        return mac.doFinal(data);
    }

    public static String seed(final byte[] bytes) {
        return DigestUtils.sha512Hex(bytes);
    }

    public static String sign(final String seed, String... keys) {
        return DigestUtils.sha512Hex(seed + String.join("", Arrays.asList(keys)));
    }
}
