package com.mpt.monio.exception;

import lombok.Getter;

@Getter
public class AppException extends RuntimeException {
    private final ErrorCode errorCode;

    public AppException(ErrorCode baseErrorCode) {
        super(baseErrorCode.getMessage());
        this.errorCode = baseErrorCode;
    }
}
