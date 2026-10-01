package com.Medilens.dto;

import com.Medilens.medicaltest.MedicalTest;

import java.util.List;

public class ReportDetailsResponse {

    private Long id;
    private String fileName;
    private String fileType;
    private String filePath;
    private List<MedicalTest> tests;

    public ReportDetailsResponse() {
    }

    public ReportDetailsResponse(
            Long id,
            String fileName,
            String fileType,
            String filePath,
            List<MedicalTest> tests) {

        this.id = id;
        this.fileName = fileName;
        this.fileType = fileType;
        this.filePath = filePath;
        this.tests = tests;
    }

    public Long getId() {
        return id;
    }

    public String getFileName() {
        return fileName;
    }

    public String getFileType() {
        return fileType;
    }

    public String getFilePath() {
        return filePath;
    }

    public List<MedicalTest> getTests() {
        return tests;
    }
}