package com.dcall.core.configuration.security.aes;

import com.dcall.core.configuration.utils.FileUtils;
import org.apache.commons.compress.utils.IOUtils;
import org.apache.commons.compress.utils.SeekableInMemoryByteChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.nio.channels.Channels;
import java.nio.channels.SeekableByteChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Base64;
import java.util.function.Predicate;

public final class AESProvider {
    private static final Logger LOG = LoggerFactory.getLogger(AESProvider.class);

    private static final int MAX_BUFFER_SIZE = Integer.MAX_VALUE - 8;
    private static final String DEFAULT_CHARSET = "UTF-8";
    private static final int _KEY_LENGTH = 128; // 256 needs JCE
    private static final int _BUFFER_SIZE = 8192;
    private static final int _ITERATE_SIZE = 10000;

    public static SecretKey generateSecretKey() throws Exception {
        final KeyGenerator gen = KeyGenerator.getInstance("AES");
        gen.init(_KEY_LENGTH);

        return gen.generateKey();
    }

    public static SecretKey getSecretKey(final String password, final byte[] salt) throws Exception {
        final SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
        final SecretKey secretKey = factory.generateSecret(new PBEKeySpec(password.toCharArray(), salt, _ITERATE_SIZE, _KEY_LENGTH));

        return new SecretKeySpec(secretKey.getEncoded(), "AES");
    }

    public static IvParameterSpec generateIV() {
        byte[] iv = new byte[16];
        new SecureRandom().nextBytes(iv);

        return new IvParameterSpec(iv);
    }

    public static Cipher initCipherWithIV(final int cipherEncryptMode, final SecretKey secretKey, final IvParameterSpec iv) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidAlgorithmParameterException, InvalidKeyException {

        final Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(cipherEncryptMode, secretKey, iv);

        return cipher;
    }

    public static Cipher initCipher(final int cipherEncryptMode, final SecretKey secretKey) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException {

        final Cipher cipher = Cipher.getInstance("AES");
        cipher.init(cipherEncryptMode, secretKey);

        return cipher;
    }

    public static byte[] encryptString(final String message, final Cipher cipher) throws UnsupportedEncodingException, BadPaddingException, IllegalBlockSizeException {
        return cipher.doFinal(message.getBytes(DEFAULT_CHARSET));
    }

    public static byte[] decryptBytes(final byte[] bytes, final Cipher cipher) throws BadPaddingException, IllegalBlockSizeException {
        return cipher.doFinal(bytes);
    }

    public static String encode(final byte[] bytes) {
        return Base64.getEncoder().encodeToString(bytes);
    }

    /**
     * Encrypt a file using cipher AES.
     *
     * Important - When destination full path is the same than the source full path only :
     *
     * File().renameTo() doesn't produce what we expect (the behaviour of cmd 'mv' on linux).
     * If we just use output.renameTo(input) instead of using a copy of the source, the new file replaced is corrupted.
     * I didn't search the explicit reason..(deleting the old file after encryption or other classic ways don't work)
     * So, to keep the integrity of the new file when the file destination's full path is the same than the source, we copy the source
     * file with a different file name and use it directly as the new source to encrypt for the destination file.
     * At the end we delete the copy and rename the new file with the source file not copied to have the good filename.
     * I agree, it's strange...
     *
     * @param inputFilePath
     * @param outputFilePath
     * @param cipher
     * @throws Exception
     */
    public static void encryptFile(final String inputFilePath, final String outputFilePath, final Cipher cipher) throws Exception {
        final boolean isReplaceSource = inputFilePath.equals(outputFilePath);
        final String default_suffix = "_encrypted";
        final File input = new File(inputFilePath);
        final File copyInput = new File(inputFilePath + "_copy");
        final File output = new File(isReplaceSource ? outputFilePath + default_suffix : outputFilePath);
        int nread;
        final byte[] buffer = new byte[_BUFFER_SIZE];

        if (!input.exists())
            throw new FileNotFoundException(AESProvider.class.getName() + " encryptFile() : Failed to find path " + inputFilePath + " or bad rights.");

        if (output.exists() && !isReplaceSource)
            output.delete();
        if (isReplaceSource)
            input.renameTo(copyInput);

        final InputStream cin = new DataInputStream(new FileInputStream(isReplaceSource ? copyInput : input));
        final CipherOutputStream cout = new CipherOutputStream(new FileOutputStream(outputFilePath), cipher);

        while ((nread = cin.read(buffer)) > 0)
            cout.write(buffer, 0, nread);

        cin.close();
        cout.close();

        if (isReplaceSource) {
            output.renameTo(new File(inputFilePath));
            copyInput.delete();
        }
    }

    public static void encryptDirectory(final String filePath, final Cipher enc, String prefix, final Predicate<String> fileNameCond) {

            try {
                final File target = new File(filePath);
                final String[] pathArray = filePath.split(File.separator);
                final String[] pwdArray = Arrays.copyOfRange(pathArray, 0, pathArray.length - 1);
                final String pwd = String.join(File.separator, pwdArray);

                prefix = prefix != null && !prefix.isEmpty() ? prefix : "";

                if (!target.exists())
                    throw new FileNotFoundException(filePath);

                if (fileNameCond == null || fileNameCond.test(pathArray[pathArray.length - 1])) {
                    if (target.isDirectory()) {
                        for (final String subFile : target.list())
                            encryptDirectory(filePath + File.separator + subFile, enc, prefix, fileNameCond);
                    } else {
                        encryptFile(filePath, pwd + File.separator + prefix + pathArray[pathArray.length - 1], enc);
                    }
                }

            } catch (Exception e) {
                LOG.error(e.getMessage());
            }
    }

    public static void decryptDirectory(final String filePath, final Cipher dec, String prefix, final Predicate<String> fileNameCond) {

        try {
            final File target = new File(filePath);
            final String[] pathArray = filePath.split(File.separator);
            final String[] pwdArray = Arrays.copyOfRange(pathArray, 0, pathArray.length - 1);
            final String pwd = String.join(File.separator, pwdArray);

            prefix = prefix != null && !prefix.isEmpty() ? prefix : "";

            if (!target.exists())
                throw new FileNotFoundException(filePath);

            if (fileNameCond == null || fileNameCond.test(pathArray[pathArray.length - 1])) {
                if (target.isDirectory()) {
                    for (final String subFile : target.list())
                        decryptDirectory(filePath + File.separator + subFile, dec, prefix, fileNameCond);
                } else {
                    decryptFile(filePath, pwd + File.separator + prefix + pathArray[pathArray.length - 1], dec);
                }
            }

        } catch (Exception e) {
            LOG.error(e.getMessage());
        }
    }

    /**
     * Average : ~650 mb (647mb) - test exectued in ~13 sec, 6.5s encryption / 6.5 decryption
     *  -> Average : 100mb / 1sec with (13 iterations) | 1000 mb (1GB) -> ~10sec with (130 iterations)
     *  | 1000000mb (1000GB) 1TB -> 10000 sec (130000 iterations) : 166 minutes ! (2h46 min WTF !)
     *  \ 4TB -> 166min * 4 = 11h04min LOL...cipher buffer size length seems to be fixed to 512
     *
     * Important - When destination full path is the same than the source full path only :
     *
     * File().renameTo() doesn't produce what we expect (the behaviour of cmd 'mv' on linux).
     * If we just use output.renameTo(input) instead of using a copy of the source, the new file replaced is corrupted.
     * I didn't search the explicit reason..(deleting the old file after encryption or other classic ways don't work)
     * So, to keep the integrity of the new file when the file destination's full path is the same than the source, we copy the source
     * file with a different file name and use it directly as the new source to decrypt for the destination file.
     * At the end we delete the copy and rename the new file with the source file not copied to have the good filename.
     * I agree, it's strange...
     *
     * @param inputFilePath
     * @param outputFilePath
     * @param cipher
     * @throws Exception
     */
    public static void decryptFile(final String inputFilePath, final String outputFilePath, final Cipher cipher) throws Exception {
        final boolean isReplaceSource = inputFilePath.equals(outputFilePath);
        final String default_suffix = "_decrypted";
        final File input = new File(inputFilePath);
        final File copyInput = new File(inputFilePath + "_copy");
        final File output = new File(isReplaceSource ? outputFilePath + default_suffix : outputFilePath);
        int nread;
        final byte[] buffer = new byte[_BUFFER_SIZE];

        if (!input.exists())
            throw new FileNotFoundException(AESProvider.class.getName() + " encryptFile() : Failed to find path " + inputFilePath + " or bad rights.");

        if (output.exists() && !isReplaceSource)
            output.delete();
        if (isReplaceSource)
            input.renameTo(copyInput);

        final CipherInputStream cin = new CipherInputStream(new DataInputStream(new FileInputStream(isReplaceSource ? copyInput : input)), cipher);
        final OutputStream cout = new FileOutputStream(outputFilePath);

        while ((nread = cin.read(buffer)) > 0)
            cout.write(buffer, 0, nread);

        cin.close();
        cout.close();

        if (isReplaceSource) {
            output.renameTo(new File(inputFilePath));
            copyInput.delete();
        }
    }

    /**
     * Decrypt all bytes of a file using cipher AES
     *
     * @param filePath
     * @param cipher
     * @return
     * @throws IOException
     */
    public static byte[] decryptFileBytes(final Path filePath, final Cipher cipher) throws IOException {
        try (SeekableByteChannel sbc = Files.newByteChannel(filePath);
             final InputStream in = Channels.newInputStream(sbc)) {
            final long size = sbc.size();
            if (size > (long)MAX_BUFFER_SIZE)
                throw new OutOfMemoryError("Required array size too large");

            final CipherInputStream cin = new CipherInputStream(in, cipher);
            return FileUtils.getInstance().read(cin, (int)size);
        }
    }

    /**
     * Decrypt all bytes of an inputStream using cipher AES
     *
     * @param inputStream
     * @param cipher
     * @return
     * @throws IOException
     */
    public static byte[] decryptInputStreamBytes(final InputStream inputStream, final Cipher cipher) throws IOException {
        try (SeekableByteChannel sbc = new SeekableInMemoryByteChannel(IOUtils.toByteArray(inputStream));
             final InputStream in = Channels.newInputStream(sbc)) {
            final long size = sbc.size();
            if (size > (long)MAX_BUFFER_SIZE)
                throw new OutOfMemoryError("Required array size too large");

            final CipherInputStream cin = new CipherInputStream(in, cipher);
            return FileUtils.getInstance().read(cin, (int)size);
        }
    }
}
