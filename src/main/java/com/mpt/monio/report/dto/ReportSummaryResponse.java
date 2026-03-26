package com.mpt.monio.report.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class ReportSummaryResponse {
    private BigDecimal totalIncome;
    private BigDecimal totalExpense;
    private BigDecimal netBalance;

    public ReportSummaryResponse(Number totalIncome, Number totalExpense) {
        this.totalIncome = totalIncome != null ?
                new BigDecimal(totalIncome.toString()) : BigDecimal.ZERO;

        this.totalExpense = totalExpense != null ?
                new BigDecimal(totalExpense.toString()) : BigDecimal.ZERO;

        this.netBalance = this.totalIncome.subtract(this.totalExpense);
    }
}
