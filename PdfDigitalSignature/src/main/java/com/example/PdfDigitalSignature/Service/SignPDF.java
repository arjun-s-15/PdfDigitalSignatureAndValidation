package com.example.PdfDigitalSignature.Service;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
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

public class SignPDF {

    // Create the visual signature template
    private static InputStream createVisualTemplate(PDRectangle rect) throws IOException {
        PDDocument template = new PDDocument();
        PDPage page = new PDPage(new PDRectangle(rect.getWidth(), rect.getHeight()));
        template.addPage(page);

        try (PDPageContentStream cs = new PDPageContentStream(template, page)) {
            cs.beginText();
            cs.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD), 12);
            cs.newLineAtOffset(10, rect.getHeight() / 2);
            cs.showText("Digitally signed by Arjun Singh");
            cs.endText();
        }

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        template.save(out);
        template.close();

        return new ByteArrayInputStream(out.toByteArray());
    }

    public static void signWithVisibleSignature(String inputFilePath, String outputFilePath) throws Exception {
        CertificateLoader.loadCert();
        PrivateKey privateKey = CertificateLoader.privateKey;
        Certificate[] certificateChain = CertificateLoader.certificateChain;

        try (PDDocument document = Loader.loadPDF(new File(inputFilePath))) {

            PDSignature signature = new PDSignature();
            signature.setFilter(PDSignature.FILTER_ADOBE_PPKLITE);
            signature.setSubFilter(PDSignature.SUBFILTER_ADBE_PKCS7_DETACHED);
            signature.setName("Arjun Singh");
            signature.setLocation("Noida");
            signature.setSignDate(Calendar.getInstance());

            // Ensure AcroForm is present
            PDAcroForm acroForm = document.getDocumentCatalog().getAcroForm();
            if (acroForm == null) {
                acroForm = new PDAcroForm(document);
                document.getDocumentCatalog().setAcroForm(acroForm);
            }
            acroForm.setNeedAppearances(true);

            PDSignatureField signatureField = new PDSignatureField(acroForm);
            signatureField.setPartialName("SignatureField");

            PDPage page = document.getPage(0);
            PDAnnotationWidget widget = new PDAnnotationWidget();
            PDRectangle rect = new PDRectangle(50, 50, 200, 100);
            widget.setRectangle(rect);
            widget.setPage(page);
            page.getAnnotations().add(widget);

            signatureField.setWidgets(java.util.Collections.singletonList(widget));
            acroForm.getFields().add(signatureField);

            try (SignatureOptions options = new SignatureOptions()) {
                options.setPage(0);
                options.setVisualSignature(createVisualTemplate(rect));
                options.setPreferredSignatureSize(SignatureOptions.DEFAULT_SIGNATURE_SIZE * 2);

                document.addSignature(signature, new SignatureServiceImpl(privateKey, certificateChain), options);

                try (FileOutputStream fos = new FileOutputStream(outputFilePath)) {
                    document.saveIncremental(fos);
                }
            }
        }
    }

    public static void main(String[] args) {
        try {
            String inputFile = "C:\\Users\\iamar\\Desktop\\ID card 28 Mar 2025.pdf";
            String outputFile = "signed-visible-output.pdf";

            signWithVisibleSignature(inputFile, outputFile);
            System.out.println("PDF signed successfully with visible digital signature!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
