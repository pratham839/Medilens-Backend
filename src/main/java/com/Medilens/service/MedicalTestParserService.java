package com.Medilens.service;

import com.Medilens.medicaltest.MedicalTest;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class MedicalTestParserService {

    public List<MedicalTest> parseText(String text, Long reportId) {

        List<MedicalTest> tests = new ArrayList<>();

        String[] lines = text.split("\\R");

        Pattern pattern = Pattern.compile(
                "^(.+?)\\s+" +
                        "([0-9,]+(?:\\.[0-9]+)?)\\s+" +
                        "(.+?)\\s+" +
                        "([<>]?\\s*[0-9,]+(?:\\.[0-9]+)?" +
                        "(?:\\s*-\\s*[0-9,]+(?:\\.[0-9]+)?)?)$"
        );

        for (String line : lines) {

            line = line.trim();

            if (line.isEmpty()) {
                continue;
            }

            // Remove status column if OCR captured it
            line = line.replaceFirst(
                    "(?i)\\s+(normal|low|high|abnormal)\\s*$",
                    ""
            );

            String normalizedLine = line
                    .toLowerCase()
                    .replaceAll("\\s+", " ")
                    .trim();

            // Ignore metadata and non-test lines
            if (isMetadata(normalizedLine)) {
                continue;
            }

            // Ignore phone numbers
            if (containsPhoneNumber(line)) {
                continue;
            }

            // Ignore email addresses
            if (line.contains("@")) {
                continue;
            }

            Matcher matcher = pattern.matcher(line);

            if (!matcher.matches()) {
                continue;
            }

            String testName = matcher.group(1).trim();
            String valueText = matcher.group(2).trim();
            String unit = matcher.group(3).trim();
            String referenceRange = matcher.group(4).trim();

            // Extra protection against header/footer text
            if (isInvalidTestName(testName)) {
                continue;
            }

            try {

                Double value = Double.parseDouble(
                        valueText.replace(",", "")
                );

                referenceRange = referenceRange
                        .replace("—", "-")
                        .replace("–", "-")
                        .replace("−", "-")
                        .replaceAll("-+", "-")
                        .replaceAll("\\s+", " ")
                        .trim();

                MedicalTest test = new MedicalTest();

                test.setReportId(reportId);
                test.setTestName(testName);
                test.setValue(value);
                test.setUnit(unit);
                test.setReferenceRange(referenceRange);

                tests.add(test);

            } catch (NumberFormatException ignored) {
                // Ignore incorrectly formatted values
            }
        }

        return tests;
    }

    private boolean isMetadata(String text) {

        String value = text
                .toLowerCase()
                .replaceAll("\\s+", " ")
                .trim();

        return value.contains("age / sex")
                || value.contains("age/sex")
                || value.contains("collection date")
                || value.contains("patient name")
                || value.contains("date of birth")
                || value.startsWith("patient")
                || value.startsWith("name:")
                || value.startsWith("gender")
                || value.startsWith("sex")
                || value.startsWith("report date")
                || value.startsWith("sample date")
                || value.startsWith("test name")
                || value.startsWith("reference range")
                || value.startsWith("medical laboratory")
                || value.startsWith("medilens test report")
                || value.startsWith("note:")
                || value.contains("accurate results")
                || value.contains("better health")
                || value.contains("diagnostics")
                || value.contains("laboratory report")
                || value.contains("consultant pathologist")
                || value.contains("computer generated report")
                || value.startsWith("remarks")
                || value.startsWith("ref. by")
                || value.startsWith("patient id")
                || value.startsWith("lab no.")
                || value.startsWith("sample collected")
                || value.startsWith("report generated");
    }

    private boolean containsPhoneNumber(String text) {

        Pattern phonePattern = Pattern.compile(
                "\\+?\\d[\\d\\s()\\-]{7,}\\d"
        );

        return phonePattern.matcher(text).find();
    }

    private boolean isInvalidTestName(String testName) {

        String name = testName
                .toLowerCase()
                .trim();

        if (name.contains("accurate results")) {
            return true;
        }

        if (name.contains("better health")) {
            return true;
        }

        if (name.contains("diagnostics")) {
            return true;
        }

        if (name.contains("laboratory")) {
            return true;
        }

        if (name.contains("report")) {
            return true;
        }

        if (name.contains("patient")) {
            return true;
        }

        if (name.contains("sample")) {
            return true;
        }

        if (name.contains("+91")) {
            return true;
        }

        if (name.contains("@")) {
            return true;
        }

        return false;
    }
}