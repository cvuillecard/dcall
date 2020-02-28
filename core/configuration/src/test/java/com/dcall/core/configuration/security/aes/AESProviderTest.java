package com.dcall.core.configuration.security.aes;

import org.apache.commons.codec.digest.DigestUtils;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import java.io.FileInputStream;
import java.util.Base64;

public class AESProviderTest {
    private static SecretKey _secret;
    private static IvParameterSpec _iv;

    @BeforeClass
    public static void init() throws Exception {
        _secret = AESProvider.generateSecretKey();
        _iv = AESProvider.generateIV();
    }

    @Test
    public void should_generate_secret_key_generateSecretKey() throws Exception {
        final String secretKey2 = Base64.getEncoder().encodeToString(AESProvider.generateSecretKey().getEncoded());

        Assert.assertNotNull(secretKey2);
        Assert.assertNotEquals(Base64.getEncoder().encodeToString(_secret.getEncoded()), secretKey2);
    }

    @Test
    public void should_encrypt_string_encryptString() throws Exception {
        final String msg = "message test";
        final Cipher enc = AESProvider.initCipher(Cipher.ENCRYPT_MODE, _secret, _iv);
        final Cipher dec = AESProvider.initCipher(Cipher.DECRYPT_MODE, _secret, _iv);

        final byte[] cryptedMsg = AESProvider.encryptString(msg, enc);
        final byte[] decryptedMsg = AESProvider.decryptString(cryptedMsg, dec);

        Assert.assertNotEquals(msg, new String(cryptedMsg));
        Assert.assertEquals(msg, new String(decryptedMsg));
    }

    @Test
    public void should_encrypt_file_text_encryptFile() throws Exception {
        final String inputFile = "./src/test/resources/cert.pem";
        final String encryptedFile = "./src/test/resources/cert-encrypted.pem";
        final String decryptedFile = "./src/test/resources/cert-decrypted.pem";
        final Cipher enc = AESProvider.initCipher(Cipher.ENCRYPT_MODE, _secret, _iv);
        final Cipher dec = AESProvider.initCipher(Cipher.DECRYPT_MODE, _secret, _iv);

        AESProvider.encryptFile(inputFile, encryptedFile, enc);
        AESProvider.decryptFile(encryptedFile, decryptedFile, dec);

        final String md5sumInputFile = new String(DigestUtils.md5(new FileInputStream(inputFile)));
        final String md5sumEncFile = new String(DigestUtils.md5(new FileInputStream(encryptedFile)));
        final String md5sumDecFile = new String(DigestUtils.md5(new FileInputStream(decryptedFile)));

        Assert.assertNotEquals(md5sumInputFile, md5sumEncFile);
        Assert.assertEquals(md5sumInputFile, md5sumDecFile);
    }

    @Test
    public void should_encrypt_file_image_encryptFile() throws Exception {
        final String inputFile = "./src/test/resources/image.png";
        final String encryptedFile = "./src/test/resources/image-encrypted.png";
        final String decryptedFile = "./src/test/resources/image-decrypted.png";

        final Cipher enc = AESProvider.initCipher(Cipher.ENCRYPT_MODE, _secret, _iv);
        final Cipher dec = AESProvider.initCipher(Cipher.DECRYPT_MODE, _secret, _iv);

        AESProvider.encryptFile(inputFile, encryptedFile, enc);
        AESProvider.decryptFile(encryptedFile, decryptedFile, dec);

        final String md5sumInputFile = new String(DigestUtils.md5(new FileInputStream(inputFile)));
        final String md5sumEncFile = new String(DigestUtils.md5(new FileInputStream(encryptedFile)));
        final String md5sumDecFile = new String(DigestUtils.md5(new FileInputStream(decryptedFile)));

        Assert.assertNotEquals(md5sumInputFile, md5sumEncFile);
        Assert.assertEquals(md5sumInputFile, md5sumDecFile);
    }

}
