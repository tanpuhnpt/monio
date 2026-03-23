package com.mpt.monio.wallet.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateWalletRequest {
    @NotBlank(message = "WALlET_NAME_BLANK")
    private String name;

    @PositiveOrZero(message = "WALLET_BALANCE_NEGATIVE")
    private BigDecimal balance;

    @NotBlank(message = "WALLET_CURRENCY_BLANK")
    private String currency;
}