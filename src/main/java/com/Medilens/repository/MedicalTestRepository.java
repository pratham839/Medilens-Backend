package com.Medilens.repository;

import com.Medilens.medicaltest.MedicalTest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MedicalTestRepository extends JpaRepository<MedicalTest, Long> {

    List<MedicalTest> findByReportId(Long reportId);

    boolean existsByReportId(Long reportId);
}