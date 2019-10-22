package com.example.bc.file.gpg.digest;

import org.bouncycastle.bcpg.HashAlgorithmTags;
import org.bouncycastle.jcajce.io.OutputStreamFactory;
import org.bouncycastle.openpgp.operator.PGPDigestCalculator;
import org.bouncycastle.operator.DigestCalculator;

import java.io.OutputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class SHA256PGPDigestCalculator implements PGPDigestCalculator {

    private MessageDigest digest;

    public SHA256PGPDigestCalculator() {
        try
        {
            digest = MessageDigest.getInstance("SHA256");
        }
        catch (NoSuchAlgorithmException e)
        {
            throw new IllegalStateException("cannot find SHA-256: " + e.getMessage());
        }
    }

    public int getAlgorithm() {
        return HashAlgorithmTags.SHA256;
    }

    public OutputStream getOutputStream()
    {
        return OutputStreamFactory.createStream(digest);
    }

    public byte[] getDigest()
    {
        return digest.digest();
    }

    public void reset()
    {
        digest.reset();
    }
}
