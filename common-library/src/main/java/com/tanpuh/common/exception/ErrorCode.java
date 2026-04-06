package com.tanpuh.common.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

@Getter
@AllArgsConstructor
public enum ErrorCode {
    // GLOBAL ERROR
    UNCATEGORIZED(9999, "An unexpected error occurred", HttpStatus.INTERNAL_SERVER_ERROR),
    JSON_INVALID(1001, "Invalid JSON request", HttpStatus.BAD_REQUEST),
    MESSAGE_KEY_INVALID(1002, "Invalid message key", HttpStatus.BAD_REQUEST),
    BIND_INVALID(1003, "Binding error occurred", HttpStatus.BAD_REQUEST),
    UNAUTHENTICATED(1004, "Authentication is required", HttpStatus.UNAUTHORIZED),
    UNAUTHORIZED(1005, "You do not have permission to access", HttpStatus.FORBIDDEN),

    // AUTH ERROR
    EMAIL_BLANK(2001, "Email is required", HttpStatus.BAD_REQUEST),
    EMAIL_INVALID(2002, "Email is not well-formed", HttpStatus.BAD_REQUEST),
    PASSWORD_BLANK(2003, "Password is required", HttpStatus.BAD_REQUEST),
    PASSWORD_PATTERN_INVALID(2004,
            "Password must be 8-20 characters long and contain at least one uppercase, one lowercase, one digit, and one special character",
            HttpStatus.BAD_REQUEST),
    FULL_NAME_BLANK(2005, "Full name is required", HttpStatus.BAD_REQUEST),
    TOKEN_BLANK(2006, "JWT is required", HttpStatus.BAD_REQUEST),

    // USER ERROR,
    USER_EXISTED(3001, "User already existed", HttpStatus.BAD_REQUEST),
    USER_NOT_FOUND(3002, "User not existed", HttpStatus.NOT_FOUND),

    // CATEGORY ERROR
    CATEGORY_NOT_FOUND(4001, "Category not existed in this user", HttpStatus.NOT_FOUND),
    CATEGORY_ID_NULL(4002, "Category ID is required", HttpStatus.BAD_REQUEST),

    // CATEGORY ERROR
    WALLET_EXISTED(5001, "Wallet already existed in this user", HttpStatus.BAD_REQUEST),
    WALLET_NOT_FOUND(5002, "Wallet not existed in this user", HttpStatus.NOT_FOUND),
    WALLET_NAME_BLANK(5003, "Wallet name is required", HttpStatus.BAD_REQUEST),
    WALLET_BALANCE_NULL(5004, "Wallet balance is required", HttpStatus.BAD_REQUEST),
    WALLET_BALANCE_NEGATIVE(5005, "Wallet balance must be greater than or equal to 0", HttpStatus.BAD_REQUEST),
    WALLET_CURRENCY_BLANK(5006, "Wallet currency is required", HttpStatus.BAD_REQUEST),
    WALLET_ID_NULL(5007, "Wallet ID is required", HttpStatus.BAD_REQUEST),
    DESTINATION_WALLET_INVALID(5008, "Destination wallet must be different from source wallet", HttpStatus.BAD_REQUEST),

    // TRANSACTION ERROR
    TRANSACTION_NOT_FOUND(6001, "Transaction not existed in this user", HttpStatus.NOT_FOUND),
    TRANSACTION_AMOUNT_NULL(6002, "Transaction amount is required", HttpStatus.BAD_REQUEST),
    TRANSACTION_AMOUNT_NEGATIVE_ZERO(6003, "Transaction amount must be greater than 0", HttpStatus.BAD_REQUEST),
    TRANSACTION_AMOUNT_INVALID(6004, "Transaction amount format is invalid", HttpStatus.BAD_REQUEST),
    TRANSACTION_NOTE_INVALID(6005, "Transaction note is too long", HttpStatus.BAD_REQUEST),
    TRANSACTION_TYPE_NULL(6006, "Transaction type is required", HttpStatus.BAD_REQUEST),
    TRANSACTION_DATE_NULL(6007, "Transaction date is required", HttpStatus.BAD_REQUEST),
    TRANSACTION_DATE_INVALID(6008, "Transaction date cannot be in the future", HttpStatus.BAD_REQUEST),
    ;

    private final int code;
    private final String message;
    private final HttpStatusCode statusCode;
}
