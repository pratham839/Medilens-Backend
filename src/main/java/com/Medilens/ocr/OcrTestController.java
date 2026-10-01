package com.Medilens.ocr;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/ocr")
public class OcrTestController {

    private final OcrService ocrService;

    public OcrTestController(OcrService ocrService) {
        this.ocrService = ocrService;
    }

    @GetMapping("/test")
    public String testOcr() {

        String filePath = "uploads/MediLens_Dummy_Test_Report.pdf";

        try {
            return ocrService.extractText(filePath);
        } catch (Exception e) {
            return "OCR failed: " + e.getMessage();
        }
    }
}