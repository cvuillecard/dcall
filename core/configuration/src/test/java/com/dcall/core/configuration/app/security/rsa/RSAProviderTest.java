package com.dcall.core.configuration.app.security.rsa;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.File;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Base64;

public class RSAProviderTest {
    private static KeyPair _keyPair;
    private final String _UTF_8 = "UTF-8";
    private final String _MESSAGE = "Mon message secret à moi..que je peux pas le partager parcque je veux pas.....J'ai bobo dans la tête : je suis victime du système informatique inter-galactique ...??";

    @BeforeClass
    public static void init() throws Exception {
        _keyPair = RSAProvider.generateKeyPair();
    }

    @Test public void should_generate_key_pair_generateKeyPair() {
        Assert.assertNotNull(_keyPair);
        Assert.assertNotNull(_keyPair.getPrivate());
        Assert.assertNotNull(_keyPair.getPublic());
    }

    @Test public void should_encode_public_key_to_string_base64_encodeKey() {
        final String encodedPublicKey = RSAProvider.encodeKey(_keyPair, RSAProvider.KeyType.PUBLIC);

        Assert.assertEquals(Base64.getEncoder().encodeToString(_keyPair.getPublic().getEncoded()), encodedPublicKey);
    }

    @Test public void should_encode_private_key_to_string_base64_encodeKey() {
        final String encodedPrivateKey = RSAProvider.encodeKey(_keyPair, RSAProvider.KeyType.PRIVATE);

        Assert.assertEquals(Base64.getEncoder().encodeToString(_keyPair.getPrivate().getEncoded()), encodedPrivateKey);
    }

    @Test public void shoud_encrypt_message_with_public_key_encrypt() throws Exception {
        final String base64UncryptedDatas = Base64.getEncoder().encodeToString(_MESSAGE.getBytes(_UTF_8));
        final String base64CryptedDatas = RSAProvider.encrypt(_MESSAGE, _keyPair.getPublic());

        Assert.assertNotNull(base64CryptedDatas);
        Assert.assertTrue(base64CryptedDatas.length() > 0);
        Assert.assertNotEquals(base64CryptedDatas.length(), base64UncryptedDatas.length());
        Assert.assertNotEquals(base64CryptedDatas, base64UncryptedDatas);
    }

    @Test public void should_decrypt_message_with_private_key_decrypt() throws Exception {
        final String base64EncodedCryptedDatas = RSAProvider.encrypt(_MESSAGE, _keyPair.getPublic());
        final String decryptedDatas = RSAProvider.decrypt(base64EncodedCryptedDatas, _keyPair.getPrivate());

        Assert.assertEquals(_MESSAGE, decryptedDatas);
    }

    @Test public void should_decrypt_message_encrypted_with_string_public_key_decrypt() throws Exception {
        final String publicKeyAsString = RSAProvider.encodeKey(_keyPair, RSAProvider.KeyType.PUBLIC);
        final PublicKey publicKeyFromString = RSAProvider.getPublicKey(publicKeyAsString);

        final String base64EncodedCryptedDatas = RSAProvider.encrypt(_MESSAGE, publicKeyFromString);

        Assert.assertNotEquals(_MESSAGE, base64EncodedCryptedDatas);

        final String dataDecrypted = RSAProvider.decrypt(base64EncodedCryptedDatas, _keyPair.getPrivate());

        Assert.assertEquals(_MESSAGE, dataDecrypted);
    }

    @Test public void should_decrypt_message_encrypted_with_string_private_key_decrypt() throws Exception {
        final String privateKeyAsString = RSAProvider.encodeKey(_keyPair, RSAProvider.KeyType.PRIVATE);
        final PrivateKey privateKeyFromString = RSAProvider.getPrivateKey(privateKeyAsString);

        final String base64EncodedCryptedDatas = RSAProvider.encrypt(_MESSAGE, _keyPair.getPublic());

        Assert.assertNotEquals(_MESSAGE, base64EncodedCryptedDatas);

        final String dataDecrypted = RSAProvider.decrypt(base64EncodedCryptedDatas, privateKeyFromString);

        Assert.assertEquals(_MESSAGE, dataDecrypted);
    }

    @Test public void should_sign_message_with_unique_hash_using_private_key_sign() throws Exception {
        final String hashSignature = RSAProvider.sign(_MESSAGE, _keyPair.getPrivate());
        final String hashSignatureFromPrivateKeyAsString = RSAProvider.sign(_MESSAGE, RSAProvider.getPrivateKey(RSAProvider.encodeKey(_keyPair, RSAProvider.KeyType.PRIVATE)));

        Assert.assertNotNull(hashSignature);
        Assert.assertTrue(hashSignature.length() > 0);

        Assert.assertEquals(hashSignature, hashSignatureFromPrivateKeyAsString);

        final String otherHashSignature = RSAProvider.sign("other secret message in the galaxy", _keyPair.getPrivate());

        Assert.assertNotEquals(hashSignature, otherHashSignature);
        Assert.assertEquals(hashSignature, RSAProvider.sign(_MESSAGE, _keyPair.getPrivate()));
    }

    @Test public void should_verify_a_message_signed_with_public_key() throws  Exception {
        final String hashSignature = RSAProvider.sign(_MESSAGE, _keyPair.getPrivate());
        final String wrongSignature = RSAProvider.sign("Another message", _keyPair.getPrivate());
        final String hashSignatureFromPrivateKeyAsString = RSAProvider.sign(_MESSAGE, RSAProvider.getPrivateKey(RSAProvider.encodeKey(_keyPair, RSAProvider.KeyType.PRIVATE)));
        final PublicKey publicKeyFromString = RSAProvider.getPublicKey(RSAProvider.encodeKey(_keyPair, RSAProvider.KeyType.PUBLIC));

        Assert.assertFalse(RSAProvider.verify(_MESSAGE, wrongSignature, _keyPair.getPublic()));

        Assert.assertTrue(RSAProvider.verify(_MESSAGE, hashSignature, _keyPair.getPublic()));
        Assert.assertTrue(RSAProvider.verify(_MESSAGE, hashSignature, publicKeyFromString));

        Assert.assertTrue(RSAProvider.verify(_MESSAGE, hashSignatureFromPrivateKeyAsString, _keyPair.getPublic()));
        Assert.assertTrue(RSAProvider.verify(_MESSAGE, hashSignatureFromPrivateKeyAsString, publicKeyFromString));
    }

    @Test public void should_create_keystore_createKeyStore() throws Exception {
        final RSAProvider.KeyStoreType storeType = RSAProvider.KeyStoreType.PKCS12;
        final String keyStoreFullPath = "./src/test/resources/keystore.p12";
        final String domainName = "Crea Technologie";
        final Long validity = 365L;
        final String aliasKey = "certificate_key";
        final String storePass = "123456";
        final String keyPass = "654321";

        RSAProvider.createKeyStore(storeType, keyStoreFullPath, domainName, _keyPair, validity, aliasKey, storePass, keyPass);

        final File keyStore = new File(keyStoreFullPath);

        Assert.assertTrue(keyStore.exists());
        Assert.assertTrue(keyStore.length() > 0);
    }

    //  keytool -genkeypair -storetype PKCS12 -alias certificate_key -storepass 123456 -keypass 654321 -keyalg RSA -sigalg SHA512withRSA -validity 365 -keysize 4096 -keystore keystore.p12
    @Test public void should_get_keypair_from_keystore_getKeyPairFromKeyStore() throws Exception {
        final RSAProvider.KeyStoreType storeType = RSAProvider.KeyStoreType.PKCS12;
        final String keyStoreFullPath = "./src/test/resources/keystore.p12";
        final String aliasKey = "certificate_key";
        final String storePass = "123456";
        final String keyPass = "654321";

        should_create_keystore_createKeyStore(); // create a keyStore with RSA certificate keyPair

        final KeyPair keyPairLoaded = RSAProvider.getKeyPairFromFileKeyStore(keyStoreFullPath, storeType, aliasKey, storePass, keyPass);

        Assert.assertEquals(RSAProvider.encodeKey(_keyPair, RSAProvider.KeyType.PUBLIC), RSAProvider.encodeKey(keyPairLoaded, RSAProvider.KeyType.PUBLIC));
        Assert.assertEquals(RSAProvider.encodeKey(_keyPair, RSAProvider.KeyType.PRIVATE), RSAProvider.encodeKey(keyPairLoaded, RSAProvider.KeyType.PRIVATE));
        // Check certificate via CLI : keytool -list -v -alias certificate_key -storepass 123456 -keystore keystore.p12 -storetype PKCS12
        // Check certificate base64 encoded via CLI : keytool -list -rfc -alias certificate_key -storepass 123456 -keystore keystore.p12 -storetype PKCS12
        // export key from certificate : keytool -export -keystore keystore.p12 -storetype PKCS12 -alias certificate_key -rfc -file cert.pem
        // openssl pkcs12 -info -in keystore.p12 must fail
        // keytool -v -importkeystore -srckeystore keystore.p12 -srcstoretype PKCS12 -srcalias certificate_key -destkeystore imported.p12 -deststoretype PKCS12
    }
}
