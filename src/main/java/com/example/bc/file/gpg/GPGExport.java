package com.example.bc.file.gpg;

import com.example.bc.file.gpg.digest.SHA256PGPDigestCalculator;
import org.bouncycastle.bcpg.ArmoredOutputStream;
import org.bouncycastle.bcpg.CompressionAlgorithmTags;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.openpgp.*;
import org.bouncycastle.openpgp.operator.jcajce.JcePBEKeyEncryptionMethodGenerator;
import org.bouncycastle.openpgp.operator.jcajce.JcePGPDataEncryptorBuilder;

import java.io.*;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;
import java.security.Security;

/**
 * Stolen from: https://github.com/bcgit/bc-java/blob/master/pg/src/main/java/org/bouncycastle/openpgp/examples/PBEFileProcessor.java
 */
public class GPGExport {

    public static void main(String[] args) throws IOException, NoSuchProviderException {
        if (args.length != 2) {
            System.out.println("Missing argument: <INPUT_FILE> <PASSWORD>");
            return;
        }

        Security.addProvider(new BouncyCastleProvider());

        OutputStream out = new BufferedOutputStream(new FileOutputStream(args[0] + ".gpg"));
        encryptFile(out, args[0], args[1].toCharArray(), true);
        out.close();
    }

    private static void encryptFile(
            OutputStream out,
            String          fileName,
            char[]          passPhrase,
            boolean         withIntegrityCheck)
            throws IOException {

        try {
            byte[] compressedData = compressFile(fileName, CompressionAlgorithmTags.ZIP);

            PGPEncryptedDataGenerator encGen = new PGPEncryptedDataGenerator(new JcePGPDataEncryptorBuilder(PGPEncryptedData.AES_256)
                    .setWithIntegrityPacket(withIntegrityCheck)
                    .setSecureRandom(new SecureRandom())
                    .setProvider("BC"));

            encGen.addMethod(new JcePBEKeyEncryptionMethodGenerator(passPhrase, new SHA256PGPDigestCalculator(), 0xFF)
                    .setProvider("BC"));

            OutputStream encOut = encGen.open(out, compressedData.length);

            encOut.write(compressedData);
            encOut.close();
        } catch (PGPException e) {
            throw new RuntimeException(e);
        }
    }

    static byte[] compressFile(String fileName, int algorithm) throws IOException {
        ByteArrayOutputStream bOut = new ByteArrayOutputStream();
        PGPCompressedDataGenerator comData = new PGPCompressedDataGenerator(algorithm);
        PGPUtil.writeFileToLiteralData(comData.open(bOut), PGPLiteralData.BINARY, new File(fileName));
        comData.close();
        return bOut.toByteArray();
    }
}
