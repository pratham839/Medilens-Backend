package com.Medilens.service;

import com.Medilens.medicaltest.MedicalTest;
import com.Medilens.ocr.OcrService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ReportProcessingService {

    private final OcrService ocrService;
    private final MedicalTestParserService parserService;
    private final MedicalTestService medicalTestService;
    private final MedicalTestStatusService statusService;

    public ReportProcessingService(
            OcrService ocrService,
            MedicalTestParserService parserService,
            MedicalTestService medicalTestService,
            MedicalTestStatusService statusService) {

        this.ocrService = ocrService;
        this.parserService = parserService;
        this.medicalTestService = medicalTestService;
        this.statusService = statusService;
    }

    public List<MedicalTest> processReport(
            Long reportId,
            String filePath) throws Exception {

        // Check if this report has already been processed
        if (medicalTestService.alreadyProcessed(reportId)) {
            return medicalTestService.getTestsByReportId(reportId);
        }

        // Step 1: OCR
        String extractedText =
                ocrService.extractText(filePath);

        // Step 2: Parse medical tests
        List<MedicalTest> tests =
                parserService.parseText(
                        extractedText,
                        reportId
                );

        // Step 3: Determine Normal/Abnormal status
        for (MedicalTest test : tests) {

            String status =
                    statusService.determineStatus(test);

            test.setStatus(status);
        }

        // Step 4: Save tests in PostgreSQL
        return medicalTestService.saveAll(tests);
    }
}