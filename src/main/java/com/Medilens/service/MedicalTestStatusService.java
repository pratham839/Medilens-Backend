
        package com.Medilens.service;

import com.Medilens.medicaltest.MedicalTest;
import org.springframework.stereotype.Service;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class MedicalTestStatusService {

    public String determineStatus(MedicalTest test) {

        Double value = test.getValue();
        String referenceRange = test.getReferenceRange();

        if (value == null || referenceRange == null || referenceRange.isBlank()) {
            return "UNKNOWN";
        }

        referenceRange = referenceRange
                .replace("—", "-")
                .replace("–", "-")
                .replace("−", "-")
                .replace("--", "-")
                .trim();

        // Range example: 4,000-11,000
        Pattern rangePattern = Pattern.compile(
                "([0-9,]+(?:\\.[0-9]+)?)\\s*-\\s*([0-9,]+(?:\\.[0-9]+)?)"
        );

        Matcher rangeMatcher = rangePattern.matcher(referenceRange);

        if (rangeMatcher.find()) {

            double minimum = Double.parseDouble(
                    rangeMatcher.group(1).replace(",", "")
            );

            double maximum = Double.parseDouble(
                    rangeMatcher.group(2).replace(",", "")
            );

            if (value < minimum) {
                return "LOW";
            }

            if (value > maximum) {
                return "HIGH";
            }

            return "NORMAL";
        }

        // Less-than example: <200
        Pattern lessThanPattern = Pattern.compile(
                "<\\s*([0-9,]+(?:\\.[0-9]+)?)"
        );

        Matcher lessThanMatcher = lessThanPattern.matcher(referenceRange);

        if (lessThanMatcher.find()) {

            double maximum = Double.parseDouble(
                    lessThanMatcher.group(1).replace(",", "")
            );

            if (value < maximum) {
                return "NORMAL";
            }

            return "HIGH";
        }

        // Greater-than example: >10
        Pattern greaterThanPattern = Pattern.compile(
                ">\\s*([0-9,]+(?:\\.[0-9]+)?)"
        );

        Matcher greaterThanMatcher =
                greaterThanPattern.matcher(referenceRange);

        if (greaterThanMatcher.find()) {

            double minimum = Double.parseDouble(
                    greaterThanMatcher.group(1).replace(",", "")
            );

            if (value > minimum) {
                return "NORMAL";
            }

            return "LOW";
        }

        return "UNKNOWN";
    }
}

