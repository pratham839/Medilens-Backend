package com.Medilens.service;

import com.Medilens.medicaltest.MedicalTest;
import com.Medilens.repository.MedicalTestRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MedicalTestService {

    private final MedicalTestRepository medicalTestRepository;

    public MedicalTestService(MedicalTestRepository medicalTestRepository) {
        this.medicalTestRepository = medicalTestRepository;
    }

    // Check whether this report has already been processed
    public boolean alreadyProcessed(Long reportId) {

        return medicalTestRepository.existsByReportId(reportId);
    }

    public MedicalTest saveMedicalTest(
            Long reportId,
            String testName,
            Double value,
            String unit,
            String referenceRange) {

        MedicalTest medicalTest = new MedicalTest();

        medicalTest.setReportId(reportId);
        medicalTest.setTestName(testName);
        medicalTest.setValue(value);
        medicalTest.setUnit(unit);
        medicalTest.setReferenceRange(referenceRange);

        return medicalTestRepository.save(medicalTest);
    }

    public List<MedicalTest> saveAll(
            List<MedicalTest> tests) {

        return medicalTestRepository.saveAll(tests);
    }

    // Get all medical tests belonging to a specific report
    public List<MedicalTest> getTestsByReportId(Long reportId) {

        return medicalTestRepository.findByReportId(reportId);
    }
}