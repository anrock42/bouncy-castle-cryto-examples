package com.example.bc.key.export;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;

public class RSAKeyFactory {

    public static KeyPair generate() {
        try {
            return generateRSAKeyPair();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static KeyPair generateRSAKeyPair() throws NoSuchAlgorithmException, NoSuchProviderException {
        KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA", "BC");
        generator.initialize(4096);

        KeyPair keyPair = generator.generateKeyPair();
        return keyPair;
    }
}
