package com.tanpuh.common.enums;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum TransactionType {
    EXPENSE,
    INCOME,
    TRANSFER;

    @JsonCreator
    public static TransactionType fromValue(String value) {
        return TransactionType.valueOf(value.toUpperCase());
    }
}
