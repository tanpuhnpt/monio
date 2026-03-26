package com.mpt.monio.transaction.dto;

import com.mpt.monio.category.dto.CategoryResponse;
import com.mpt.monio.wallet.dto.WalletResponse;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TransactionResponse {
    Long id;
    BigDecimal amount;
    String note;
    LocalDateTime createdAt;
    String type;
    CategoryResponse category;
    WalletResponse wallet;
}
