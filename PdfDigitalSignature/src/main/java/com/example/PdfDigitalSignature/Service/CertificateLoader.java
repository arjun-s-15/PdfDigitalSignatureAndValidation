package com.example.PdfDigitalSignature.Service;

import java.io.FileInputStream;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.cert.Certificate;
import java.util.Enumeration;

public class CertificateLoader {

    public static PrivateKey privateKey;
    public static Certificate[] certificateChain;

    public static void loadCert() throws Exception{
        String keystorePath = "C:\\Users\\iamar\\Desktop\\PdfDigitalSignature\\Arjun+Singh.pfx";
        String password = "ArjunSingh1204";

        KeyStore keyStore =KeyStore.getInstance("PKCS12");

        try (FileInputStream fis = new FileInputStream(keystorePath)) {
            keyStore.load(fis,password.toCharArray());
        }

//        testing for private key alias

        String alias = null;

        Enumeration<String> aliases = keyStore.aliases();

        while (aliases.hasMoreElements()){
            String currentAlias = aliases.nextElement();
            if (keyStore.isKeyEntry(currentAlias)){
                alias = currentAlias;
                break;
            }
        }
        if (alias==null){
            throw new Exception("No private key entry found  in keystore");
        }
        System.out.println("Alias: "+alias);

        privateKey = (PrivateKey) keyStore.getKey(alias,password.toCharArray());
        certificateChain = keyStore.getCertificateChain(alias);

    }

    public static void main(String[] args) throws Exception {
        loadCert();
        System.out.println("Private Key:" + privateKey);

        if (certificateChain != null) {
            for (Certificate cert : certificateChain) {
                System.out.println(cert);
            }
        } else {
           throw new Exception("No certificate chain found.");
        }

    }


}
