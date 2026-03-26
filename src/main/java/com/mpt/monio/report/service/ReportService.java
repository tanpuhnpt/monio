package com.mpt.monio.report.service;

import com.mpt.monio.report.dto.ReportSummaryResponse;

import java.time.LocalDate;

public interface ReportService {
    ReportSummaryResponse getSummary(LocalDate startDate, LocalDate endDate);
}
