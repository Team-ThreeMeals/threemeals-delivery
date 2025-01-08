package com.threemeals.delivery.domain.auth.exception;


import com.threemeals.delivery.config.error.ErrorCode;
import com.threemeals.delivery.domain.common.exception.BaseException;

public class AuthenticationException extends BaseException {

    public AuthenticationException(ErrorCode errorCode) {
        super(errorCode);
    }

    public AuthenticationException(String message, ErrorCode errorCode) {
        super(message, errorCode);
    }

    public AuthenticationException() {
        super(ErrorCode.AUTHENTICATION_FAILURE);
    }
}
