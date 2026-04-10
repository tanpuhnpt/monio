package com.tanpuh.transaction.dto;

import java.math.BigDecimal;

public record TransferRequest (
    Long sourceWalletId,
    Long destinationWalletId,
    BigDecimal amount
) {}
