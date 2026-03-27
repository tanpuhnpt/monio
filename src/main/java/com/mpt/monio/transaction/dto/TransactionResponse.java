package com.mpt.monio.transaction.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
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

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    LocalDateTime createdAt;

    String type;
    CategoryResponse category;
    WalletResponse wallet;
    WalletResponse destinationWallet;
}
