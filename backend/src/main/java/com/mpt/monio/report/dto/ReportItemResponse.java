package com.mpt.monio.report.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class ReportItemResponse {
    private Long id;
    private String name;
    private BigDecimal totalAmount;
    private BigDecimal percentage;

    public ReportItemResponse(Long id, String name, Number totalAmount) {
        this.id = id;
        this.name = name;
        this.totalAmount = totalAmount != null
                ? new BigDecimal(totalAmount.toString()) : BigDecimal.ZERO;
        this.percentage = BigDecimal.ZERO;
    }
}
