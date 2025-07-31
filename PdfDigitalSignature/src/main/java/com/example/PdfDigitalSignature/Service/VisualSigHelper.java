package com.example.PdfDigitalSignature.Service;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDCIDFontType0;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAnnotationWidget;
import org.apache.pdfbox.pdmodel.interactive.digitalsignature.PDSignature;
import org.apache.pdfbox.pdmodel.interactive.digitalsignature.SignatureOptions;
import org.apache.pdfbox.pdmodel.interactive.form.PDAcroForm;
import org.apache.pdfbox.pdmodel.interactive.form.PDSignatureField;

import java.io.*;
import java.security.PrivateKey;
import java.security.cert.Certificate;
import java.util.Calendar;
import java.util.List;

public class VisualSigHelper {
    private InputStream createVisualTemplate(PDDocument document, PDRectangle rect) throws IOException {
        // Use PDFBox's built-in method to create a valid visual signature template
        try (PDDocument template = document.createVisualSignatureTemplateDocument(0, rect)) {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            template.save(baos);
            return new ByteArrayInputStream(baos.toByteArray());
        }
    }

    public void signPdfWithVisibleSignature(File inputFile, File outputFile,
                                            PrivateKey privateKey, Certificate[] certificateChain) throws IOException {

        try (PDDocument document = Loader.loadPDF(inputFile)) {
            PDSignature signature = new PDSignature();
            signature.setFilter(PDSignature.FILTER_ADOBE_PPKLITE);
            signature.setSubFilter(PDSignature.SUBFILTER_ADBE_PKCS7_DETACHED);
            signature.setName("Arjun Singh");
            signature.setLocation("Noida");
            signature.setSignDate(Calendar.getInstance());

            // Define signature rectangle and generate visual template
            float x = 50, y = 50, width = 150, height = 100;
            PDRectangle signatureRect = new PDRectangle(x, y, width, height);
            InputStream visualTemplate = createVisualTemplate(document, signatureRect);

            // Ensure AcroForm exists
            PDAcroForm acroForm = document.getDocumentCatalog().getAcroForm();
            if (acroForm == null) {
                acroForm = new PDAcroForm(document);
                document.getDocumentCatalog().setAcroForm(acroForm);
            }
            acroForm.setNeedAppearances(true);

            // Create and attach the signature field to the page
            PDSignatureField signatureField = new PDSignatureField(acroForm);
            signatureField.setPartialName("SignatureField");

            PDAnnotationWidget widget = new PDAnnotationWidget();
            widget.setRectangle(signatureRect);
            PDPage page = document.getPage(0);
            widget.setPage(page);
            signatureField.setWidgets(List.of(widget));
            page.getAnnotations().add(widget);
            acroForm.getFields().add(signatureField);

            // Set up signature options
            try (SignatureOptions options = new SignatureOptions()) {
                options.setPage(0); // page index
                options.setVisualSignature(visualTemplate);
                options.setPreferredSignatureSize(SignatureOptions.DEFAULT_SIGNATURE_SIZE * 2);

                // Apply signature
                document.addSignature(signature, new SignatureServiceImpl(privateKey, certificateChain), options);
            }

            // Save the document incrementally
            try (FileOutputStream fos = new FileOutputStream(outputFile)) {
                document.saveIncremental(fos);
            }
        }
    }



    public static void main(String[] args) {
        try {
            // Load your certificate (.pfx/.p12) and private key
            CertificateLoader.loadCert(); // Make sure this class exists and loads your keystore
            PrivateKey privateKey = CertificateLoader.privateKey;
            Certificate[] certificateChain = CertificateLoader.certificateChain;

            // Input and output PDF file paths
            File inputFile = new File("C:\\Users\\iamar\\Desktop\\ID card 28 Mar 2025.pdf");
            File outputFile = new File("C:\\Users\\iamar\\Desktop\\signed-output.pdf");

            // Create instance and sign
            VisualSigHelper signer = new VisualSigHelper();
            signer.signPdfWithVisibleSignature(inputFile, outputFile, privateKey, certificateChain);

            System.out.println("PDF signed successfully with visible signature!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
