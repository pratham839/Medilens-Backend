package com.Medilens.service;

import com.Medilens.dto.ReportDetailsResponse;
import com.Medilens.entity.Report;
import com.Medilens.medicaltest.MedicalTest;
import com.Medilens.repository.ReportRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.util.List;

@Service
public class ReportService {

    private final ReportRepository reportRepository;
    private final MedicalTestService medicalTestService;

    private final Path uploadDirectory =
            Paths.get("uploads");

    public ReportService(
            ReportRepository reportRepository,
            MedicalTestService medicalTestService) {

        this.reportRepository = reportRepository;
        this.medicalTestService = medicalTestService;
    }

    public Report saveReport(MultipartFile file) throws IOException {

        // Calculate SHA-256 hash of uploaded file
        String fileHash = calculateFileHash(file);

        // Check whether this exact file already exists
        if (reportRepository.findByFileHash(fileHash).isPresent()) {
            throw new IOException(
                    "This report file has already been uploaded."
            );
        }

        // Create uploads folder if it does not exist
        Files.createDirectories(uploadDirectory);

        // File path
        Path filePath = uploadDirectory.resolve(
                file.getOriginalFilename()
        );

        // Save file to uploads folder
        Files.copy(
                file.getInputStream(),
                filePath,
                java.nio.file.StandardCopyOption.REPLACE_EXISTING
        );

        // Create Report object
        Report report = new Report();

        report.setFileName(file.getOriginalFilename());
        report.setFileType(file.getContentType());
        report.setFilePath(filePath.toString());
        report.setFileHash(fileHash);

        // Save report in PostgreSQL
        return reportRepository.save(report);
    }

    // Calculate SHA-256 hash
    private String calculateFileHash(MultipartFile file)
            throws IOException {

        try {

            MessageDigest digest =
                    MessageDigest.getInstance("SHA-256");

            byte[] fileBytes = file.getBytes();

            byte[] hashBytes =
                    digest.digest(fileBytes);

            StringBuilder hash = new StringBuilder();

            for (byte b : hashBytes) {

                hash.append(
                        String.format("%02x", b)
                );
            }

            return hash.toString();

        } catch (Exception e) {

            throw new IOException(
                    "Could not calculate file hash.",
                    e
            );
        }
    }

    // Get report by ID
    public Report getReportById(Long reportId) {

        return reportRepository.findById(reportId)
                .orElse(null);
    }

    // Get complete report with medical tests
    public ReportDetailsResponse getReportDetails(
            Long reportId) {

        Report report =
                reportRepository.findById(reportId)
                        .orElse(null);

        if (report == null) {
            return null;
        }

        List<MedicalTest> tests =
                medicalTestService.getTestsByReportId(reportId);

        return new ReportDetailsResponse(
                report.getId(),
                report.getFileName(),
                report.getFileType(),
                report.getFilePath(),
                tests
        );
    }
}