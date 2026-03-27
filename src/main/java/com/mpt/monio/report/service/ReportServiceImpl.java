package com.mpt.monio.report.service;

import com.mpt.monio.report.dto.DateRange;
import com.mpt.monio.report.dto.ReportItemResponse;
import com.mpt.monio.report.dto.ReportSummaryResponse;
import com.mpt.monio.transaction.entity.TransactionType;
import com.mpt.monio.transaction.repo.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReportServiceImpl implements ReportService{
    private final TransactionRepository transactionRepository;
    private static final BigDecimal PERCENTAGE_MULTIPLIER = new BigDecimal("100");

    public ReportSummaryResponse getSummary(LocalDate startDate, LocalDate endDate) {
        Long userId = Long.valueOf(SecurityContextHolder.getContext().getAuthentication().getName());

        DateRange dateRange = DateRange.ofCurrentMonthIfNull(startDate, endDate);

        return transactionRepository.getReportSummary(userId, dateRange.start(), dateRange.end());
    }

    @Override
    public List<ReportItemResponse> getStatisticsByCategory(TransactionType type, LocalDate startDate, LocalDate endDate) {
        Long userId = Long.valueOf(SecurityContextHolder.getContext().getAuthentication().getName());

        DateRange dateRange = DateRange.ofCurrentMonthIfNull(startDate, endDate);

        List<ReportItemResponse> items = transactionRepository
                .getStatisticsByCategory(userId, type.name(), dateRange.start(), dateRange.end());

        BigDecimal grandTotal = calculateGrandTotal(items);

        if (grandTotal.compareTo(BigDecimal.ZERO) > 0) // tránh case chia cho 0
            calculatePercentage(items, grandTotal);

        return items;
    }

    @Override
    public List<ReportItemResponse> getStatisticsByWallet(TransactionType type, LocalDate startDate, LocalDate endDate) {
        Long userId = Long.valueOf(SecurityContextHolder.getContext().getAuthentication().getName());

        DateRange dateRange = DateRange.ofCurrentMonthIfNull(startDate, endDate);

        List<ReportItemResponse> items = transactionRepository
                .getStatisticsByWallet(userId, type.name(), dateRange.start(), dateRange.end());

        BigDecimal grandTotal = calculateGrandTotal(items);

        if (grandTotal.compareTo(BigDecimal.ZERO) > 0) // tránh case chia cho 0
            calculatePercentage(items, grandTotal);

        return items;
    }

    private BigDecimal calculateGrandTotal(List<ReportItemResponse> items) {
        return items
                .stream()
                .map(ReportItemResponse::getTotalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private void calculatePercentage(List<ReportItemResponse> items, BigDecimal grandTotal) {
        items.forEach(item -> {
            item.setPercentage(item.getTotalAmount().multiply(PERCENTAGE_MULTIPLIER)
                    .divide(grandTotal, 2, RoundingMode.HALF_UP));
        });
    }
}
