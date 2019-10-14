package com.example.bc.key.export;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.openssl.PEMEncryptor;
import org.bouncycastle.openssl.jcajce.JcaMiscPEMGenerator;
import org.bouncycastle.openssl.jcajce.JcaPEMWriter;
import org.bouncycastle.openssl.jcajce.JcePEMEncryptorBuilder;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.Security;

public class ExportPrivateKey {

    public static void main(String[] args) throws IOException {
        if (args.length != 2) {
            System.out.println("Missing argument: <OUTPUT_FILE> <PASSWORD>");
            return;
        }
        Security.addProvider(new BouncyCastleProvider());

        KeyPair keyPair = RSAKeyFactory.generate();

        String privateKeyString = toPem(keyPair.getPrivate(), args[1]);
        System.out.println(privateKeyString);
        toFile(args[0], privateKeyString);
    }

    public static String toPem(PrivateKey privateKey, String password) throws IOException {
        StringWriter sw = new StringWriter();
        try (JcaPEMWriter pemWriter = new JcaPEMWriter(sw)) {
            PEMEncryptor encryptor = new JcePEMEncryptorBuilder("AES-256-CBC").build(password.toCharArray());
            // privateKey is a java.security.PrivateKey
            JcaMiscPEMGenerator gen = new JcaMiscPEMGenerator(privateKey, encryptor);
            pemWriter.writeObject(gen);
        }

        return sw.toString();
    }

    private static void toFile(String filename, String content) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename))) {
            writer.write(content);
        }
    }
}
