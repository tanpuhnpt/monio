package com.tanpuh.wallet.dto;

import com.tanpuh.common.enums.TransactionType;

import java.math.BigDecimal;

public record UpdateBalanceRequest (
    Long id,
    TransactionType type,
    BigDecimal amount
) {}
