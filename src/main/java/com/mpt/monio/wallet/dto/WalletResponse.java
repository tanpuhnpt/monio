package com.mpt.monio.wallet.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class WalletResponse {
    private Long id;
    private String name;
    private BigDecimal balance;
    private String currency;
}
