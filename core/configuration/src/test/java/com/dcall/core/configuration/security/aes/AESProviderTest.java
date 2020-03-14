package com.dcall.core.configuration.security.aes;

import org.apache.commons.codec.digest.DigestUtils;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.FileInputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Base64;

public class AESProviderTest {
    private static SecretKey _secret;
    private static IvParameterSpec _iv;
    private static final String password = "q7HyVPwdjVp0ifD9++mbug==";
    private static final byte[] salt = "supersalt%#!@$#|-*+".getBytes();

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
    public void should_generate_secret_key_with_password_and_salt_getSecretKey() throws Exception {
        final String secret = Base64.getEncoder().encodeToString(AESProvider.getSecretKey(password, salt).getEncoded());
        final String secret2 = Base64.getEncoder().encodeToString(AESProvider.getSecretKey(password, salt).getEncoded());

        Assert.assertEquals(secret, secret2);
    }

    @Test
    public void should_encrypt_string_with_IV_encryptString() throws Exception {
        final String msg = "message test";
        final Cipher enc = AESProvider.initCipherWithIV(Cipher.ENCRYPT_MODE, _secret, _iv);
        final Cipher dec = AESProvider.initCipherWithIV(Cipher.DECRYPT_MODE, _secret, _iv);

        final byte[] cryptedMsg = AESProvider.encryptString(msg, enc);
        final byte[] decryptedMsg = AESProvider.decryptBytes(cryptedMsg, dec);

        Assert.assertNotEquals(msg, new String(cryptedMsg));
        Assert.assertEquals(msg, new String(decryptedMsg));
    }

    @Test
    public void should_encrypt_string_without_IV_encryptString() throws Exception {
        String msg = "message test";
        String secret = Base64.getEncoder().encodeToString(_secret.getEncoded());

        // using SecretKey
        Cipher enc = AESProvider.initCipher(Cipher.ENCRYPT_MODE, _secret);
        Cipher dec = AESProvider.initCipher(Cipher.DECRYPT_MODE, _secret);

        byte[] cryptedMsg = AESProvider.encryptString(msg, enc);
        byte[] decryptedMsg = AESProvider.decryptBytes(cryptedMsg, dec);

        Assert.assertNotEquals(msg, new String(cryptedMsg));
        Assert.assertEquals(msg, new String(decryptedMsg));

        // using SecretKeySpec
        enc = AESProvider.initCipher(Cipher.ENCRYPT_MODE, new SecretKeySpec(_secret.getEncoded(), "AES"));
        dec = AESProvider.initCipher(Cipher.DECRYPT_MODE, new SecretKeySpec(_secret.getEncoded(), "AES"));

        cryptedMsg = AESProvider.encryptString(msg, enc);
        decryptedMsg = AESProvider.decryptBytes(cryptedMsg, dec);

        Assert.assertNotEquals(msg, new String(cryptedMsg));
        Assert.assertEquals(msg, new String(decryptedMsg));

        // using SecretKeySpec from password and salt
        enc = AESProvider.initCipher(Cipher.ENCRYPT_MODE, AESProvider.getSecretKey(password, salt));
        dec = AESProvider.initCipher(Cipher.DECRYPT_MODE, AESProvider.getSecretKey(password, salt));

        cryptedMsg = AESProvider.encryptString(msg, enc);
        decryptedMsg = AESProvider.decryptBytes(cryptedMsg, dec);

        Assert.assertNotEquals(msg, new String(cryptedMsg));
        Assert.assertEquals(msg, new String(decryptedMsg));
    }

    @Test
    public void should_encrypt_file_text_encryptFile() throws Exception {
        final String inputFile = "./src/test/resources/cert.pem";
        final String encryptedFile = "./src/test/resources/cert-encrypted.pem";
        final String decryptedFile = "./src/test/resources/cert-decrypted.pem";
        final Cipher enc = AESProvider.initCipherWithIV(Cipher.ENCRYPT_MODE, _secret, _iv);
        final Cipher dec = AESProvider.initCipherWithIV(Cipher.DECRYPT_MODE, _secret, _iv);

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

        final Cipher enc = AESProvider.initCipherWithIV(Cipher.ENCRYPT_MODE, _secret, _iv);
        final Cipher dec = AESProvider.initCipherWithIV(Cipher.DECRYPT_MODE, _secret, _iv);

        AESProvider.encryptFile(inputFile, encryptedFile, enc);
        AESProvider.decryptFile(encryptedFile, decryptedFile, dec);

        final String md5sumInputFile = new String(DigestUtils.md5(new FileInputStream(inputFile)));
        final String md5sumEncFile = new String(DigestUtils.md5(new FileInputStream(encryptedFile)));
        final String md5sumDecFile = new String(DigestUtils.md5(new FileInputStream(decryptedFile)));

        Assert.assertNotEquals(md5sumInputFile, md5sumEncFile);
        Assert.assertEquals(md5sumInputFile, md5sumDecFile);
    }

    @Test
    public void sould_decrypt_all_bytes_of_encrypted_file() throws Exception {
        final String inputFile = "./src/test/resources/cert.pem";
        final String encryptedFile = "./src/test/resources/cert-encrypted.pem";
        final String decryptedFile = "./src/test/resources/cert-decrypted.pem";
        final Cipher inCipher = AESProvider.initCipher(Cipher.ENCRYPT_MODE, AESProvider.getSecretKey(password, salt));
        final Cipher outCipher = AESProvider.initCipher(Cipher.DECRYPT_MODE, AESProvider.getSecretKey(password, salt));

        AESProvider.encryptFile(inputFile, encryptedFile, inCipher);
        AESProvider.decryptFile(encryptedFile, decryptedFile, outCipher);

        // when : we read all bytes of the decrypted file and the same file encrypted with our method
        final byte[] allBytes = Files.readAllBytes(Paths.get(decryptedFile));
        final byte[] decryptedAllBytes = AESProvider.decryptFileBytes(encryptedFile, outCipher);

        // then : the buffers must be equals, means we rode the file decrypting all encrypted bytes
        Assert.assertEquals(new String(allBytes), new String(decryptedAllBytes));
        Assert.assertEquals(Base64.getEncoder().encodeToString(allBytes), Base64.getEncoder().encodeToString(decryptedAllBytes));
    }

}
