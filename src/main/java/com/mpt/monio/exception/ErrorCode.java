package com.mpt.monio.exception;

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

    // AUTH ERROR
    EMAIL_BLANK(2001, "Email is required", HttpStatus.BAD_REQUEST),
    EMAIL_INVALID(2002, "Email is not well-formed", HttpStatus.BAD_REQUEST),
    PASSWORD_BLANK(2003, "Password is required", HttpStatus.BAD_REQUEST),
    PASSWORD_PATTERN_INVALID(2004,
            "Password must be 8-20 characters long and contain at least one uppercase, one lowercase, one digit, and one special character",
            HttpStatus.BAD_REQUEST),
    FULL_NAME_BLANK(2005, "Full name is required", HttpStatus.BAD_REQUEST),

    // USER ERROR,
    USER_EXISTED(3001, "User already existed", HttpStatus.BAD_REQUEST),
    USER_NOT_FOUND(3002, "User not existed", HttpStatus.NOT_FOUND),
    ;

    private final int code;
    private final String message;
    private final HttpStatusCode statusCode;
}
