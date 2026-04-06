package com.tanpuh.category.entity;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum CategoryType {
    EXPENSE,
    INCOME;

    @JsonCreator
    public static CategoryType fromValue(String value) {
        return CategoryType.valueOf(value.toUpperCase());
    }
}
