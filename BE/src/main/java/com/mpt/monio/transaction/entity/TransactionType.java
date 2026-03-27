package com.mpt.monio.transaction.entity;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum TransactionType {
    EXPENSE,
    INCOME;

    @JsonCreator
    public static TransactionType fromValue(String value) {
        return TransactionType.valueOf(value.toUpperCase());
    }
}
