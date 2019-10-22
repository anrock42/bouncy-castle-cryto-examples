package com.example.bc.key.openssl;

import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.crypto.params.RSAKeyParameters;
import org.bouncycastle.crypto.util.PrivateKeyFactory;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.openssl.PEMParser;
import org.bouncycastle.openssl.jcajce.JceOpenSSLPKCS8DecryptorProviderBuilder;
import org.bouncycastle.operator.InputDecryptorProvider;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.pkcs.PKCS8EncryptedPrivateKeyInfo;
import org.bouncycastle.pkcs.PKCSException;

import java.io.FileReader;
import java.io.IOException;
import java.security.Security;

public class ImportPrivateKey {

    public static void main(String[] args) throws Exception {
        Security.addProvider(new BouncyCastleProvider());

        if (args.length != 2) {
            System.out.println("Expecting 2 arguments: <PATH_KEY> <PASSWORD>");
            return;
        }

        PrivateKeyInfo privateKeyInfo = readPem(args[0], args[1]);
        RSAKeyParameters key = (RSAKeyParameters) PrivateKeyFactory.createKey(privateKeyInfo);
        System.out.println("private key [exponent d=" + key.getExponent());
    }

    private static PrivateKeyInfo readPem(String keyfileName, String password) throws IOException, OperatorCreationException, PKCSException {
        // Loads a privte key from the specified key file name
        final PEMParser pemParser = new PEMParser(new FileReader(keyfileName));
        PKCS8EncryptedPrivateKeyInfo pair = (PKCS8EncryptedPrivateKeyInfo) pemParser.readObject();
        JceOpenSSLPKCS8DecryptorProviderBuilder jce = new JceOpenSSLPKCS8DecryptorProviderBuilder();
        InputDecryptorProvider decProv = jce.build(password.toCharArray());
        return pair.decryptPrivateKeyInfo(decProv);
    }
}
