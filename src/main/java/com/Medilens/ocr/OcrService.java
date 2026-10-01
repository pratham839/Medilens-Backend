package com.Medilens.ocr;

import net.sourceforge.tess4j.Tesseract;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.stereotype.Service;

import java.io.File;

@Service
public class OcrService {

    private final Tesseract tesseract;

    public OcrService() {

        tesseract = new Tesseract();

        tesseract.setDatapath(
                "C:\\Program Files\\Tesseract-OCR\\tessdata"
        );

        tesseract.setLanguage("eng");
    }

    public String extractText(String filePath) throws Exception {

        File file = new File(filePath);

        /*
         * PDF processing
         */
        if (filePath.toLowerCase().endsWith(".pdf")) {

            /*
             * First try to extract selectable text
             * from the PDF using PDFBox.
             */
            try (PDDocument document = Loader.loadPDF(file)) {

                PDFTextStripper stripper = new PDFTextStripper();

                String text = stripper.getText(document);

                if (text != null && !text.trim().isEmpty()) {

                    return text;
                }
            }

            /*
             * If PDF has no selectable text,
             * use Tesseract OCR.
             */
            return tesseract.doOCR(file);
        }

        /*
         * Image processing
         *
         * Supports:
         * JPG
         * JPEG
         * PNG
         * WEBP
         */
        String lowerFilePath =
                filePath.toLowerCase();

        if (lowerFilePath.endsWith(".jpg")
                || lowerFilePath.endsWith(".jpeg")
                || lowerFilePath.endsWith(".png")
                || lowerFilePath.endsWith(".webp")) {

            return tesseract.doOCR(file);
        }

        /*
         * Unsupported file type
         */
        throw new IllegalArgumentException(
                "Unsupported report file type. " +
                        "Please upload a PDF, JPG, JPEG, PNG or WEBP file."
        );
    }
}