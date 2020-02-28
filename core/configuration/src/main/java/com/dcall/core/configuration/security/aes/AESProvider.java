package com.dcall.core.configuration.security.aes;

import com.dcall.core.configuration.security.rsa.RSAProvider;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import java.io.*;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

public final class AESProvider {
    private static final String DEFAULT_CHARSET = "UTF-8";
    private static final int _KEY_LENGTH = 128; // 256 needs JCE
    private static final int _BUFFER_SIZE = 8192;

    public static SecretKey generateSecretKey() throws Exception {
        KeyGenerator gen = KeyGenerator.getInstance("AES");
        gen.init(_KEY_LENGTH);

        return gen.generateKey();
    }

    public static IvParameterSpec generateIV() {
        byte[] iv = new byte[16];
        new SecureRandom().nextBytes(iv);

        return new IvParameterSpec(iv);
    }

    public static Cipher initCipher(final int cipherEncryptMode, final SecretKey secretKey, final IvParameterSpec iv) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidAlgorithmParameterException, InvalidKeyException {

        final Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(cipherEncryptMode, secretKey, iv);

        return cipher;
    }

    public static byte[] encryptString(final String message, final Cipher cipher) throws UnsupportedEncodingException, BadPaddingException, IllegalBlockSizeException {
        return cipher.doFinal(message.getBytes(DEFAULT_CHARSET));
    }

    public static byte[] decryptString(final byte[] bytes, final Cipher cipher) throws BadPaddingException, IllegalBlockSizeException {
        return cipher.doFinal(bytes);
    }

    public static String encode(final byte[] bytes) {
        return Base64.getEncoder().encodeToString(bytes);
    }

    public static void encryptFile(final String inputFilePath, final String outputFilePath, final Cipher cipher) throws Exception {
        final File input = new File(inputFilePath);
        final File output = new File(outputFilePath);
        int nread;
        final byte[] buffer = new byte[_BUFFER_SIZE];

        if (!input.exists())
            throw new FileNotFoundException(RSAProvider.class.getName() + " encryptFile() : Failed to find path " + inputFilePath + " or bad rights.");

        if (output.exists())
            output.delete();

        final InputStream cin = new DataInputStream(new FileInputStream(input));
        final CipherOutputStream cout = new CipherOutputStream(new FileOutputStream(outputFilePath), cipher);

        while ((nread = cin.read(buffer)) > 0)
            cout.write(buffer, 0, nread);

        cin.close();
        cout.close();
    }

    /**
     * Average : ~650 mb (647mb) - test exectued in ~13 sec, 6.5s encryption / 6.5 decryption
     *  -> Average : 100mb / 1sec with (13 iterations) | 1000 mb (1GB) -> ~10sec with (130 iterations)
     *  | 1000000mb (1000GB) 1TB -> 10000 sec (130000 iterations) : 166 minutes ! (2h46 min WTF !)
     *  \ 4TB -> 166min * 4 = 11h04min LOL...cipher buffer size length seems to be fixed to 512
     * @param inputFilePath
     * @param outputFilePath
     * @param cipher
     * @throws Exception
     */
    public static void decryptFile(final String inputFilePath, final String outputFilePath, final Cipher cipher) throws Exception {
        final File input = new File(inputFilePath);
        final File output = new File(outputFilePath);
        int nread;
        final byte[] buffer = new byte[_BUFFER_SIZE];

        if (!input.exists())
            throw new FileNotFoundException(RSAProvider.class.getName() + " encryptFile() : Failed to find path " + inputFilePath + " or bad rights.");

        if (output.exists())
            output.delete();

        final CipherInputStream cin = new CipherInputStream(new DataInputStream(new FileInputStream(input)), cipher);
        final OutputStream cout = new FileOutputStream(outputFilePath);

        while ((nread = cin.read(buffer)) > 0)
            cout.write(buffer, 0, nread);

        cin.close();
        cout.close();
    }
}
