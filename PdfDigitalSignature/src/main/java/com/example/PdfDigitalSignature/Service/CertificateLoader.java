package com.example.PdfDigitalSignature.Service;

import java.io.FileInputStream;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.cert.Certificate;
import java.util.Enumeration;

public class CertificateLoader {

    public static PrivateKey privateKey;
    public static Certificate[] certificateChain;

    public static void loadCert() throws Exception {
        String keystorePath = "C:\\Users\\iamar\\Desktop\\Arjun+Singh.pfx";
        String password = "ArjunSingh1204";

        KeyStore keyStore = KeyStore.getInstance("PKCS12");
        keyStore.load(new FileInputStream(keystorePath), password.toCharArray());

        String alias = keyStore.aliases().nextElement();
        System.out.println("Default Alias: " + alias);
        privateKey = (PrivateKey) keyStore.getKey(alias, password.toCharArray());
        certificateChain = keyStore.getCertificateChain(alias);

//        if (keyStore.isKeyEntry(alias)) {
//            privateKey = (PrivateKey) keyStore.getKey(alias, password.toCharArray());
//            certificateChain = keyStore.getCertificateChain(alias);
//        }

//        Enumeration<String> aliases = keyStore.aliases();
//        while (aliases.hasMoreElements()) {
//            String a = aliases.nextElement();
//            System.out.println("Alias: " + a);
//            System.out.println("Is key entry: " + keyStore.isKeyEntry(a));
//            System.out.println("Is certificate entry: " + keyStore.isCertificateEntry(a));
//        }
    }

//    public static void main(String[] args) throws Exception {
//        loadCert();
//        System.out.println("Private Key: " + privateKey);
//
//        if (certificateChain != null) {
//            for (Certificate cert : certificateChain) {
//                System.out.println(cert);
//            }
//        } else {
//            System.out.println("No certificate ");
//        }
//    }
}
