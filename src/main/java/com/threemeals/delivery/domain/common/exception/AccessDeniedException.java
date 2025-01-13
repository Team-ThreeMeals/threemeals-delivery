package com.threemeals.delivery.domain.common.exception;

import com.threemeals.delivery.config.error.ErrorCode;

public class AccessDeniedException extends BaseException {

    public AccessDeniedException(ErrorCode errorCode) {
        super(errorCode);
    }

    public AccessDeniedException() {
        super(ErrorCode.ACCESS_DENIED);
    }

    public AccessDeniedException(String message, ErrorCode errorCode) {
        super(message, errorCode);
    }

    public AccessDeniedException(String message) {
        super(message, ErrorCode.ACCESS_DENIED);
    }
}
