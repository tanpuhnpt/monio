package com.tanpuh.wallet.dto;

import jakarta.validation.constraints.NotBlank;

public record UpdateWalletRequest (
    @NotBlank(message = "WALlET_NAME_BLANK")
    String name,

    @NotBlank(message = "WALLET_CURRENCY_BLANK")
    String currency
) {}
