package com.example.PdfDigitalSignature.Service;

import org.apache.pdfbox.pdmodel.interactive.digitalsignature.SignatureInterface;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import java.io.IOException;
import java.io.InputStream;
import java.security.*;
import java.security.cert.Certificate;

public class SignatureServiceImpl implements SignatureInterface {
    private final PrivateKey privatekey;
    private final Certificate[] certificateChain;

    public SignatureServiceImpl(PrivateKey privatekey, Certificate[] certificateChain) {
        this.privatekey = privatekey;
        this.certificateChain = certificateChain;
    }


    @Override
    public byte[] sign(InputStream content) throws IOException {
        try {
            Signature signature = Signature.getInstance("SHA256withRSA");
            signature.initSign(privatekey);

            byte[] data = content.readAllBytes();
            signature.update(data);

            return signature.sign();

        } catch (NoSuchAlgorithmException | InvalidKeyException | SignatureException e) {
            throw new RuntimeException(e);
        }


    }

}

