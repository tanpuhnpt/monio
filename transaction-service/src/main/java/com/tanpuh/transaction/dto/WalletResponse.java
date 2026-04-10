package com.tanpuh.transaction.dto;

import java.math.BigDecimal;

public record WalletResponse(
    Long id,
    String name,
    BigDecimal balance,
    String currency
) {}
