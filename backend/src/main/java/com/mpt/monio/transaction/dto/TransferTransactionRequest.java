package com.mpt.monio.transaction.dto;

import jakarta.validation.constraints.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TransferTransactionRequest {
    @NotNull(message = "TRANSACTION_AMOUNT_NULL")
    @Positive(message = "TRANSACTION_AMOUNT_NEGATIVE_ZERO")
    @Digits(integer = 11, fraction = 2, message = "TRANSACTION_AMOUNT_INVALID")
    BigDecimal amount;

    @Size(max = 100, message = "TRANSACTION_NOTE_INVALID")
    String note;

    @NotNull(message = "TRANSACTION_DATE_NULL")
    @PastOrPresent(message = "TRANSACTION_DATE_INVALID")
    LocalDateTime createdAt;

    @NotNull(message = "WALLET_ID_NULL")
    Long sourceWalletId;

    @NotNull(message = "WALLET_ID_NULL")
    Long destinationWalletId;
}
