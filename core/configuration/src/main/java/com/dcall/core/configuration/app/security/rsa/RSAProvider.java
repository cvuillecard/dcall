package com.dcall.core.configuration.app.security.rsa;

import sun.security.x509.*;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import java.io.*;
import java.math.BigInteger;
import java.nio.file.NoSuchFileException;
import java.security.*;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.Date;

public final class RSAProvider {
    private static final String _ALGORITHM = "RSA";
    private static final String _UTF_8 = "UTF-8";
    private static final String _SIGALG = "SHA512withRSA";
    private static final int _KEYSIZE = 4096;
    /** KEY SIZE | SECURITY STENGTH
     * 1024 bit  | <= 80
     * 2048 bit  | 112 [ considered quite strong by NIST preco before 2030 ... who knows my friend ? ]
     * 3072 bit  | 128
     * 7680 bit  | 192 [ for 2080 new year ? depending of performances impacted by RSA algo implementation ]
     */

    enum KeyType { PUBLIC, PRIVATE }

    /**
     * if -storetype not specified using keytool, by default type used is JKS
     *
     * Preconization : PKCS12 to be compliant JAVA9, PKCS12 is going to be the default type
     * default keystore type available at $JRE/lib/security/java.security
     *
     * See also https://www.pixelstech.net/article/1408345768-Different-types-of-keystore-in-Java----Overview
     */
    public enum KeyStoreType { JKS, JCEKS, PKCS12, PKCS11, DKS }

    public static KeyPair generateKeyPair() throws Exception {
        KeyPairGenerator generator = KeyPairGenerator.getInstance(_ALGORITHM);
        generator.initialize(_KEYSIZE, new SecureRandom());
        KeyPair pair = generator.generateKeyPair();

        return pair;
    }

    public static X509Certificate generateX509Certificat(final String domainName, final KeyPair keyPair, final Long validity, final String sigAlgName) throws GeneralSecurityException, IOException {
        final PrivateKey privateKey = keyPair.getPrivate();
        final X509CertInfo info = new X509CertInfo();
        final Date from = new Date();
        final Date to = new Date(from.getTime() + validity * 1000L * 24L * 60L * 60L);

        CertificateValidity interval = new CertificateValidity(from, to);
        BigInteger serialNumber = new BigInteger(64, new SecureRandom());
        X500Name owner = new X500Name(domainName);
        AlgorithmId sigAlgId = new AlgorithmId(AlgorithmId.md5WithRSAEncryption_oid);

        info.set(X509CertInfo.VALIDITY, interval);
        info.set(X509CertInfo.SERIAL_NUMBER, new CertificateSerialNumber(serialNumber));
        info.set(X509CertInfo.SUBJECT, owner);
        info.set(X509CertInfo.ISSUER, owner);
        info.set(X509CertInfo.KEY, new CertificateX509Key(keyPair.getPublic()));
        info.set(X509CertInfo.VERSION, new CertificateVersion(CertificateVersion.V3));
        info.set(X509CertInfo.ALGORITHM_ID, new CertificateAlgorithmId(sigAlgId));

        // Sign the cert to identify the algorithm that's used.
        X509CertImpl certificate = new X509CertImpl(info);
        certificate.sign(privateKey, sigAlgName);

        // Update the algorithm info, and resign.
        sigAlgId = (AlgorithmId) certificate.get(X509CertImpl.SIG_ALG);
        info.set(CertificateAlgorithmId.NAME + "." + CertificateAlgorithmId.ALGORITHM, sigAlgId);
        certificate = new X509CertImpl(info);
        certificate.sign(privateKey, sigAlgName);

        return certificate;
    }

    public static void createKeyStore(final KeyStoreType storeType, final String keyStoreFullPath, final String domainName, final KeyPair keyPair,
                                     final Long validity, final String alias, final String storePass, final String keyPass) throws Exception {
        final File keystore = new File(keyStoreFullPath);
        final int MIN_PKCS12_PWD_LENGTH = 6;

        if (keystore.exists())
            keystore.delete();

        if (storePass.length() < MIN_PKCS12_PWD_LENGTH)
            throw new BadPaddingException(RSAProvider.class.getName() + " : Failed to create KeyStore -storepass <pwd> requires a padding of 6 characters at least.");
        if (keyPass.length() < MIN_PKCS12_PWD_LENGTH)
            throw new BadPaddingException(RSAProvider.class.getName() + " : Failed to create KeyStore -keypass <pwd> requires a padding of 6 characters at least.");

        final FileOutputStream fos = new FileOutputStream(keyStoreFullPath);
        final java.security.cert.Certificate[] chain = {generateX509Certificat("cn=" + domainName, keyPair, validity, _SIGALG)};

        KeyStore keyStore = KeyStore.getInstance(storeType == null ? KeyStoreType.PKCS12.name() : storeType.name());
        keyStore.load(null, null);
        keyStore.setKeyEntry(alias, keyPair.getPrivate(), keyPass.toCharArray(), chain);

        keyStore.store(fos, storePass.toCharArray());

        fos.close();
    }

    public static String encodeKey(final Key key) {
            return Base64.getEncoder().encodeToString(key.getEncoded());
    }

    public static String encodeKey(final KeyPair keyPair, final RSAProvider.KeyType keyType) {
        switch (keyType) {
            case PUBLIC: return encodeKey(keyPair.getPublic());
            case PRIVATE: return encodeKey(keyPair.getPrivate());
            default: break;
        }

        return null;
    }

    public static KeyPair getKeyPairFromFileKeyStore(final String keyStoreFullPath, final RSAProvider.KeyStoreType storeType, final String alias, final String storePass, final String keyPass) throws Exception {
        if (!new File(keyStoreFullPath).exists())
            throw new NoSuchFileException(RSAProvider.class.getName() + " : getKeyPairFromFileKeyStore() -> keystore with path " + keyStoreFullPath + "doesn't fileExists. Please check the path or rights.");

        return getKeyPairFromInputStreamKeyStore(new FileInputStream(keyStoreFullPath), storeType, alias, storePass, keyPass);
    }

    public static KeyPair getKeyPairFromInputStreamKeyStore(final InputStream inputStream, final KeyStoreType storeType, final String alias, final String storePass, final String keyPass) throws KeyStoreException, IOException, NoSuchAlgorithmException, CertificateException, UnrecoverableEntryException {
        if (inputStream == null)
            throw new NullPointerException("cannot create KeyPair : inputStream is null");

        KeyStore keyStore = KeyStore.getInstance(storeType == null ? KeyStoreType.PKCS12.name() : storeType.name());
        keyStore.load(inputStream, storePass.toCharArray());   //Keystore password
        KeyStore.PasswordProtection keyPassword = new KeyStore.PasswordProtection(keyPass.toCharArray());

        final KeyStore.PrivateKeyEntry privateKeyEntry = (KeyStore.PrivateKeyEntry) keyStore.getEntry(alias, keyPassword);

        final java.security.cert.Certificate cert = keyStore.getCertificate(alias);
        PublicKey publicKey = cert.getPublicKey();
        PrivateKey privateKey = privateKeyEntry.getPrivateKey();

        return new KeyPair(publicKey, privateKey);
    }

    /** X.509 format spec by default **/
    public static PublicKey getPublicKey(final String publicKey) throws Exception {
        return KeyFactory.getInstance(_ALGORITHM).generatePublic(new X509EncodedKeySpec(Base64.getDecoder().decode(publicKey.getBytes())));
    }

    /** PKCS#8 format spec by default **/
    public static PrivateKey getPrivateKey(final String privateKey) throws Exception {
        return KeyFactory.getInstance(_ALGORITHM).generatePrivate(new PKCS8EncodedKeySpec(Base64.getDecoder().decode(privateKey)));
    }

    public static byte[] encrypt(final byte[] bytes, final PublicKey publicKey) throws Exception {
        Cipher encryptCipher = Cipher.getInstance(_ALGORITHM);
        encryptCipher.init(Cipher.ENCRYPT_MODE, publicKey);

        return encryptCipher.doFinal(bytes);
    }

    public static byte[] decrypt(final byte[] bytes, final PrivateKey privateKey) throws Exception {
        Cipher decriptCipher = Cipher.getInstance(_ALGORITHM);
        decriptCipher.init(Cipher.DECRYPT_MODE, privateKey);

        return decriptCipher.doFinal(bytes);
    }

    public static String encryptString(final String plainText, final PublicKey publicKey) throws Exception {
        return Base64.getEncoder().encodeToString(encrypt(plainText.getBytes(_UTF_8), publicKey));
    }

    public static String decryptString(final String cipherText, final PrivateKey privateKey) throws Exception {
        byte[] bytes = Base64.getDecoder().decode(cipherText);
        return new String(decrypt(bytes, privateKey));
    }

    public static String sign(final String plainText, final PrivateKey privateKey) throws Exception {
        final Signature privateSign = Signature.getInstance(_SIGALG);

        privateSign.initSign(privateKey);
        privateSign.update(plainText.getBytes(_UTF_8));

        return Base64.getEncoder().encodeToString(privateSign.sign());
    }

    public static boolean verify(final String plainText, final String signature, final PublicKey publicKey) throws Exception {
        final Signature publicSignature = Signature.getInstance(_SIGALG);

        publicSignature.initVerify(publicKey);
        publicSignature.update(plainText.getBytes(_UTF_8));

        byte[] signatureBytes = Base64.getDecoder().decode(signature);

        return publicSignature.verify(signatureBytes);
    }

}
