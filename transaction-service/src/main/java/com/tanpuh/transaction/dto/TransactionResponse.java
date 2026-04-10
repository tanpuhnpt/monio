package com.tanpuh.transaction.dto;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record TransactionResponse (
    Long id,
    BigDecimal amount,
    String note,
    String type,

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    LocalDateTime createdAt,

    CategoryResponse category,
    WalletResponse wallet,
    WalletResponse destinationWallet
) {}
