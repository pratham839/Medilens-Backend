package com.Medilens.controller;

import com.Medilens.dto.ReportDetailsResponse;
import com.Medilens.entity.Report;
import com.Medilens.medicaltest.MedicalTest;
import com.Medilens.service.ReportProcessingService;
import com.Medilens.service.ReportService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/reports")
public class ReportController {

    private final ReportService reportService;
    private final ReportProcessingService reportProcessingService;

    public ReportController(
            ReportService reportService,
            ReportProcessingService reportProcessingService) {

        this.reportService = reportService;
        this.reportProcessingService = reportProcessingService;
    }

    @PostMapping("/upload")
    public ResponseEntity<?> uploadReport(
            @RequestParam("file") MultipartFile file) {

        try {

            if (file.isEmpty()) {
                return ResponseEntity.badRequest()
                        .body("Please select a file.");
            }

            // Step 1: Save report
            Report report = reportService.saveReport(file);

            // Step 2: OCR → Parse → Save medical tests
            List<MedicalTest> tests =
                    reportProcessingService.processReport(
                            report.getId(),
                            report.getFilePath()
                    );

            return ResponseEntity.ok(tests);

        } catch (Exception e) {

            return ResponseEntity.internalServerError()
                    .body("Report processing failed: " + e.getMessage());
        }
    }

    // Get report by ID
    @GetMapping("/{reportId}")
    public ResponseEntity<?> getReportById(
            @PathVariable Long reportId) {

        Report report =
                reportService.getReportById(reportId);

        if (report == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(report);
    }

    // Get complete report with medical tests
    @GetMapping("/{reportId}/details")
    public ResponseEntity<?> getReportDetails(
            @PathVariable Long reportId) {

        ReportDetailsResponse response =
                reportService.getReportDetails(reportId);

        if (response == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(response);
    }
}