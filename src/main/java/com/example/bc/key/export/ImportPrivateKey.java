package com.example.bc.key.export;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.openssl.PEMDecryptorProvider;
import org.bouncycastle.openssl.PEMEncryptedKeyPair;
import org.bouncycastle.openssl.PEMParser;
import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter;
import org.bouncycastle.openssl.jcajce.JcePEMDecryptorProviderBuilder;

import java.io.FileReader;
import java.io.IOException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.Security;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.RSAPrivateCrtKeySpec;
import java.security.spec.RSAPrivateKeySpec;

public class ImportPrivateKey {

    public static void main(String[] args) throws IOException, InvalidKeySpecException, NoSuchAlgorithmException {
        Security.addProvider(new BouncyCastleProvider());

        if (args.length != 2) {
            System.out.println("Expecting 2 arguments: <PATH_KEY> <PASSWORD>");
            return;
        }

        RSAPrivateKeySpec privateKeySpec = readPem(args[0], args[1]);
        System.out.println("private key [exponent d=" + privateKeySpec.getPrivateExponent());
    }

    private static RSAPrivateKeySpec readPem(String keyfileName, String password) throws InvalidKeySpecException, IOException, NoSuchAlgorithmException {
        // Loads a privte key from the specified key file name
        final PEMParser pemParser = new PEMParser(new FileReader(keyfileName));
        final Object object = pemParser.readObject();
        JcaPEMKeyConverter converter = new JcaPEMKeyConverter().setProvider("BC");
        // Encrypted key - we will use provided password
        PEMEncryptedKeyPair ckp = (PEMEncryptedKeyPair) object;
        PEMDecryptorProvider decProv = new JcePEMDecryptorProviderBuilder().build(password.toCharArray());
        KeyPair kp = converter.getKeyPair(ckp.decryptKeyPair(decProv));
        // RSA
        KeyFactory keyFac = KeyFactory.getInstance("RSA");
        RSAPrivateCrtKeySpec privateKey = keyFac.getKeySpec(kp.getPrivate(), RSAPrivateCrtKeySpec.class);

        return privateKey;
    }
}
