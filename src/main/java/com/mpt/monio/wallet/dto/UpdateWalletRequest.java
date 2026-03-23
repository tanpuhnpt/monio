package com.mpt.monio.wallet.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateWalletRequest {
    @NotBlank(message = "WALlET_NAME_BLANK")
    private String name;

    @NotBlank(message = "WALLET_CURRENCY_BLANK")
    private String currency;
}
