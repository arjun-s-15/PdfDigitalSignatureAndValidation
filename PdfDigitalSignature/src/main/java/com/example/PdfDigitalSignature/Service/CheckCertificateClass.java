package com.example.PdfDigitalSignature.Service;

import java.io.FileInputStream;
import java.security.KeyStore;
import java.security.cert.X509Certificate;
import java.util.Collection;
import java.util.List;

public class CheckCertificateClass {
    public static void main(String[] args) throws Exception {
        String pfxPath = "C:\\Users\\iamar\\Desktop\\Arjun+Singh.pfx";
        String password = "ArjunSingh1204";

        KeyStore keystore = KeyStore.getInstance("PKCS12");
        keystore.load(new FileInputStream(pfxPath), password.toCharArray());

        String alias = keystore.aliases().nextElement();
        X509Certificate cert = (X509Certificate) keystore.getCertificate(alias);

        // ✅ Method 1: Check Subject or Issuer Name
        String issuerDN = cert.getIssuerX500Principal().toString();
        String subjectDN = cert.getSubjectX500Principal().toString();
        System.out.println("Issuer: " + issuerDN);
        System.out.println("Subject: " + subjectDN);

        if (issuerDN.contains("Class 3") || subjectDN.contains("Class 3")) {
            System.out.println("This is a Class 3 Certificate");
        } else if (issuerDN.contains("Class 2") || subjectDN.contains("Class 2")) {
            System.out.println("This is a Class 2 Certificate");
        } else if (issuerDN.contains("Class 1") || subjectDN.contains("Class 1")) {
            System.out.println("This is a Class 1 Certificate");
        }

        // ✅ Method 2: Check Certificate Policies (OID)
        byte[] extValue = cert.getExtensionValue("2.5.29.32"); // Certificate Policies OID
        if (extValue != null) {
            // Decode ASN.1 sequence (use BouncyCastle for easier parsing)
            System.out.println("Certificate Policies Present. (Use OID mapping for Class)");
        }
    }
}
