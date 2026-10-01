package com.Medilens.controller;

import com.Medilens.medicaltest.MedicalTest;
import com.Medilens.service.MedicalTestParserService;
import com.Medilens.service.MedicalTestService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/parser")
public class MedicalTestParserController {

    private final MedicalTestParserService parserService;
    private final MedicalTestService medicalTestService;

    public MedicalTestParserController(
            MedicalTestParserService parserService,
            MedicalTestService medicalTestService) {

        this.parserService = parserService;
        this.medicalTestService = medicalTestService;
    }

    @PostMapping("/save")
    public List<MedicalTest> parseAndSave(
            @RequestBody String text) {

        List<MedicalTest> tests =
                parserService.parseText(text, 1L);

        return medicalTestService.saveAll(tests);
    }
}