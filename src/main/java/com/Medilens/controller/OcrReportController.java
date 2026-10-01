package com.Medilens.controller;

import com.Medilens.ocr.OcrService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/ocr")
public class OcrReportController {

    private final OcrService ocrService;

    public OcrReportController(OcrService ocrService) {
        this.ocrService = ocrService;
    }

    @GetMapping("/report/{reportId}")
    public String getOcrText(@PathVariable Long reportId) {

        String filePath = "uploads/MediLens_Test_Report_Abnormal.pdf";

        try {
            return ocrService.extractText(filePath);
        } catch (Exception e) {
            return "OCR failed: " + e.getMessage();
        }
    }
}