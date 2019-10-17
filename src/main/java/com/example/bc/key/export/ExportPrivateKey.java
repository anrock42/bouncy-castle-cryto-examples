package com.example.bc.key.export;

import org.bouncycastle.asn1.DERNull;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.openssl.PKCS8Generator;
import org.bouncycastle.openssl.jcajce.JcaPEMWriter;
import org.bouncycastle.openssl.jcajce.JceOpenSSLPKCS8EncryptorBuilder;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.operator.OutputEncryptor;
import org.bouncycastle.util.io.pem.PemObject;
import org.bouncycastle.util.io.pem.PemObjectGenerator;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.security.SecureRandom;
import java.security.Security;

public class ExportPrivateKey {

    public static void main(String[] args) throws Exception {
        if (args.length != 2) {
            System.out.println("Missing argument: <OUTPUT_FILE> <PASSWORD>");
            return;
        }
        Security.addProvider(new BouncyCastleProvider());

        PrivateKeyInfo privateKeyInfo = RSAKeyFactory.generatePrivateKeyPKCS8();

        PemObjectGenerator encrypt = encrypt(privateKeyInfo, args[1]);
        String pem = pemToString(encrypt);

        System.out.println(pem);
        toFile(args[0], pem);
    }

    public static PemObjectGenerator encrypt(PrivateKeyInfo privateKeyInfo, String password) throws OperatorCreationException {
        //encrypted form of PKCS#8 file
        OutputEncryptor encryptor =
                new JceOpenSSLPKCS8EncryptorBuilder(PKCS8Generator.AES_256_CBC)
                        .setIterationCount(100_000)
                        .setRandom(new SecureRandom())
                        .setPasssword(password.toCharArray())
                        .setPRF(hmacWithSHA512())
                        .build();

        return new PKCS8Generator(privateKeyInfo, encryptor);
    }

    private static String pemToString(PemObjectGenerator pemObjectGenerator) throws IOException {
        PemObject pemObject = pemObjectGenerator.generate();
        StringWriter stringWriter = new StringWriter();
        try (JcaPEMWriter pw = new JcaPEMWriter(stringWriter)) {
            pw.writeObject(pemObject);
        }
        return stringWriter.toString();
    }

    private static AlgorithmIdentifier hmacWithSHA512() {
        return new AlgorithmIdentifier(PKCSObjectIdentifiers.id_hmacWithSHA256, DERNull.INSTANCE);
    }

    private static void toFile(String filename, String content) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename))) {
            writer.write(content);
        }
    }
}
