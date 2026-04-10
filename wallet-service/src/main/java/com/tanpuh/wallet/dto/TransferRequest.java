package com.tanpuh.wallet.dto;

import java.math.BigDecimal;

public record TransferRequest (
    Long sourceWalletId,
    Long destinationWalletId,
    BigDecimal amount
) {}
