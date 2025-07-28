package com.example.PdfDigitalSignature.Service;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.interactive.digitalsignature.PDSignature;
import org.apache.pdfbox.pdmodel.interactive.digitalsignature.SignatureOptions;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import java.io.File;
import java.io.FileOutputStream;
import java.security.Security;
import java.util.Calendar;

public class SignPDF {

    static {
        if (Security.getProvider(BouncyCastleProvider.PROVIDER_NAME) == null) {
            Security.addProvider(new BouncyCastleProvider());
        }
    }

    public static void main(String[] args) throws Exception {
        CertificateLoader.loadCert();

        File inputFile = new File("C:\\Users\\iamar\\Desktop\\Internship Letter-Arjun Singh.pdf");
        File outputFile = new File("C:\\Users\\iamar\\Desktop\\test_signed_file.pdf");

        try (PDDocument document = Loader.loadPDF(inputFile)) {
            PDSignature signature = new PDSignature();
            signature.setFilter(PDSignature.FILTER_ADOBE_PPKLITE);
            signature.setSubFilter(PDSignature.SUBFILTER_ADBE_PKCS7_DETACHED);
            signature.setName("Arjun Singh");
            signature.setLocation("Noida");
            signature.setReason("Agree to the terms stated");
            signature.setSignDate(Calendar.getInstance());

            // Visible signature options
//            SignatureOptions signatureOptions = new SignatureOptions();
//            signatureOptions.setPage(0);
//
//            PDRectangle rect = new PDRectangle(336, 488, 200, 50);
//
//            signatureOptions.setVisualSignature(
//                    VisibleSignatureHelper.createVisualSignatureTemplate(
//                            document,
//                            rect,
//                            "C:\\Users\\iamar\\Desktop\\signature.jpg",
//                            "Arjun Singh",
//                            "Noida",
//                            "Agree to the terms stated"
//                    )
//            );

            document.addSignature(
                    signature,
                    new CreateSignature(CertificateLoader.privateKey, CertificateLoader.certificateChain)
            );

            try (FileOutputStream fos = new FileOutputStream(outputFile)) {
                document.saveIncremental(fos);
            }
        }

        System.out.println("✅ PDF signed successfully: " + outputFile.getAbsolutePath());
    }
}
