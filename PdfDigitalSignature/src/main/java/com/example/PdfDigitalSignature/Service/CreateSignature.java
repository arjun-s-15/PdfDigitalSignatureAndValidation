package com.example.PdfDigitalSignature.Service;

import org.apache.pdfbox.pdmodel.interactive.digitalsignature.SignatureInterface;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.Signature;
import java.security.cert.Certificate;
import java.util.Arrays;

public class CreateSignature implements SignatureInterface {

    private final PrivateKey privateKey;
    private final Certificate[] certificateChain;

    public CreateSignature(PrivateKey privateKey, Certificate[] certificateChain) {
        this.privateKey = privateKey;
        this.certificateChain = certificateChain;
    }


    public byte[] sign(InputStream content) throws java.io.IOException {
        try {
            Signature signature = Signature.getInstance("SHA256withRSA", BouncyCastleProvider.PROVIDER_NAME);
            signature.initSign(privateKey);

            byte[] buffer = new byte[16384];
            int c;

            while((c= content.read(buffer))>-1){
                signature.update(buffer,0,c);
            }
            return signature.sign();

        } catch (Exception e) {
            throw new IOException(e);
        }
    }
    public Certificate[] getCertificateChain() {
        return Arrays.copyOf(certificateChain, certificateChain.length);
    }
}
