package com.tanpuh.wallet.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

import java.math.BigDecimal;

public record CreateWalletRequest (
    @NotBlank(message = "WALlET_NAME_BLANK")
    String name,

    @NotNull(message = "WALLET_BALANCE_NULL")
    @PositiveOrZero(message = "WALLET_BALANCE_NEGATIVE")
    BigDecimal balance,

    @NotBlank(message = "WALLET_CURRENCY_BLANK")
    String currency
) {}