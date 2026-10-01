package com.Medilens.controller;

import com.Medilens.medicaltest.MedicalTest;
import com.Medilens.service.MedicalTestService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tests")
public class MedicalTestController {

    private final MedicalTestService medicalTestService;

    public MedicalTestController(
            MedicalTestService medicalTestService) {

        this.medicalTestService = medicalTestService;
    }

    @GetMapping("/report/{reportId}")
    public List<MedicalTest> getTestsByReportId(
            @PathVariable Long reportId) {

        return medicalTestService.getTestsByReportId(reportId);
    }
}