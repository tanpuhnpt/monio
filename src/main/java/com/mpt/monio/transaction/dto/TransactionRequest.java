package com.mpt.monio.transaction.dto;

import com.mpt.monio.transaction.entity.TransactionType;
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
public class TransactionRequest {
    BigDecimal amount;
    String note;
    TransactionType type;
    LocalDateTime createdAt;
    Long categoryId;
    Long walletId;
}
