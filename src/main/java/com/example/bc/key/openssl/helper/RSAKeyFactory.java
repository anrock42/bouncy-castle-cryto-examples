package com.example.bc.key.openssl.helper;

import org.bouncycastle.asn1.ASN1InputStream;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;

import java.io.IOException;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;

public class RSAKeyFactory {

    public static PrivateKeyInfo generatePrivateKeyPKCS8() throws NoSuchProviderException, NoSuchAlgorithmException, InvalidKeySpecException, IOException {
        KeyPair pair = generateRSAKeyPair();

        KeyFactory factory = KeyFactory.getInstance("RSA", "BC");
        PrivateKey privateKey = factory.generatePrivate(new PKCS8EncodedKeySpec(pair.getPrivate().getEncoded()));

        ASN1InputStream asn1input = new ASN1InputStream(privateKey.getEncoded());
        return PrivateKeyInfo.getInstance(asn1input.readObject());
    }

    private static KeyPair generateRSAKeyPair() throws NoSuchAlgorithmException, NoSuchProviderException {
        KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA", "BC");
        generator.initialize(4096);

        KeyPair keyPair = generator.generateKeyPair();
        return keyPair;
    }
}
