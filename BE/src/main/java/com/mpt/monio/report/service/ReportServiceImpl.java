package com.mpt.monio.report.service;

import com.mpt.monio.report.dto.ReportSummaryResponse;
import com.mpt.monio.transaction.repo.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Service
@RequiredArgsConstructor
public class ReportServiceImpl implements ReportService{
    private final TransactionRepository transactionRepository;

    public ReportSummaryResponse getSummary(LocalDate startDate, LocalDate endDate) {
        Long userId = Long.valueOf(SecurityContextHolder.getContext().getAuthentication().getName());

        // mặc định nếu không truyền thì lấy từ đầu tháng đến hiện tại
        LocalDateTime startDateTime = (startDate != null)
                ? startDate.atStartOfDay()
                : LocalDate.now().withDayOfMonth(1).atStartOfDay();

        LocalDateTime endDateTime = (endDate != null)
                ? endDate.atTime(LocalTime.MAX)
                : LocalDateTime.now();

        return transactionRepository.getReportSummary(userId, startDateTime, endDateTime);
    }
}
