package com.dcall.core.configuration.app.security.hash;

import org.apache.commons.codec.binary.Base64;
import org.junit.Assert;
import org.junit.Test;

public class HashProviderTest {
    final int HMAC_BYTE_ARRAY_SIZE = 64;
    final int HMAC_ENCODED_LENGTH = 88;
    final int BYTE_ARRAY_SIZE = 512;
    final int RANDOM_LENGTH = 684;
    final int SHA_STRING_LENGTH = 128;

    @Test
    public void should_generate_random_512_byte_array_each_call_random() {
        final byte[] rand1 = HashProvider.random();
        final byte[] rand2 = HashProvider.random();
        final String encodedRand1 = Base64.encodeBase64String(rand1);
        final String encodedRand2 = Base64.encodeBase64String(rand2);

        Assert.assertNotEquals(encodedRand1, encodedRand2);
        Assert.assertEquals(BYTE_ARRAY_SIZE, rand1.length);
        Assert.assertEquals(BYTE_ARRAY_SIZE, rand2.length);
        Assert.assertEquals(RANDOM_LENGTH, encodedRand1.length());
        Assert.assertEquals(RANDOM_LENGTH, encodedRand2.length());
    }

    @Test
    public void should_generate_hmac_with_data_hmac() throws Exception {
        final String data = "This is a string";
        final byte[] key = HashProvider.random();

        final byte[] hmac = HashProvider.hmac(data.getBytes(), key);
        final String encodedHMAC = Base64.encodeBase64String(hmac);

        Assert.assertEquals(HMAC_BYTE_ARRAY_SIZE, hmac.length);
        Assert.assertEquals(HMAC_ENCODED_LENGTH, encodedHMAC.length());
        Assert.assertNotEquals(encodedHMAC, HashProvider.hmac(data.getBytes(), HashProvider.random()));
        Assert.assertEquals(encodedHMAC, Base64.encodeBase64String(HashProvider.hmac(data.getBytes(), key)));
    }

    @Test
    public void should_generate_128_hash_from_secret_sha512_seed() {
        final String secret = Base64.encodeBase64String(HashProvider.random());
        final String key = HashProvider.seedSha512(secret.getBytes());
        final String key2 = HashProvider.seedSha512(secret.getBytes());

        Assert.assertNotEquals(secret, key);
        Assert.assertEquals(key, key2);
        Assert.assertEquals(SHA_STRING_LENGTH, key.length());
    }

    @Test
    public void should_sign_sha_512_with_128_hash_sign() {
        final String secret = Base64.encodeBase64String(HashProvider.random());
        final String seed = HashProvider.seedSha512(secret.getBytes());
        final String signature = HashProvider.signSha512(seed, "ADN1", "ADN2", "ADN3");
        final String differentSignature = HashProvider.signSha512(seed, "ADN3", "ADN2", "ADN1");

        Assert.assertEquals(signature, HashProvider.signSha512(seed, "ADN1", "ADN2", "ADN3"));
        Assert.assertNotEquals(signature, differentSignature);
        Assert.assertEquals(SHA_STRING_LENGTH, signature.length());
        Assert.assertEquals(SHA_STRING_LENGTH, differentSignature.length());
    }
}
