package com.hambug.Hambug.domain.report.repository;

import com.hambug.Hambug.domain.report.entity.Report;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReportRepository extends JpaRepository<Report, Long>, CustomReportRepository {
}
