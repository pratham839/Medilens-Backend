package com.Medilens.controller;

import com.Medilens.entity.Report;
import com.Medilens.ocr.OcrService;
import com.Medilens.service.ReportService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/ocr-debug")
public class OcrDebugController {

    private final OcrService ocrService;
    private final ReportService reportService;

    public OcrDebugController(
            OcrService ocrService,
            ReportService reportService) {
        this.ocrService = ocrService;
        this.reportService = reportService;
    }

    @GetMapping("/{reportId}")
    public String getOcrText(
            @PathVariable Long reportId) {

        try {
            Report report =
                    reportService.getReportById(reportId);

            if (report == null) {
                return "Report not found.";
            }

            return ocrService.extractText(
                    report.getFilePath()
            );

        } catch (Exception e) {
            return "OCR failed: " + e.getMessage();
        }
    }
}