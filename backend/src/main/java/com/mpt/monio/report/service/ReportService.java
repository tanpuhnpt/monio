package com.mpt.monio.report.service;

import com.mpt.monio.report.dto.ReportItemResponse;
import com.mpt.monio.report.dto.ReportSummaryResponse;
import com.mpt.monio.transaction.entity.TransactionType;

import java.time.LocalDate;
import java.util.List;

public interface ReportService {
    ReportSummaryResponse getSummary(LocalDate startDate, LocalDate endDate);

    List<ReportItemResponse> getStatisticsByCategory(TransactionType type, LocalDate startDate, LocalDate endDate);

    List<ReportItemResponse> getStatisticsByWallet(TransactionType type, LocalDate startDate, LocalDate endDate);
}
