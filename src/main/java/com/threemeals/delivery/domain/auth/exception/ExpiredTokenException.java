package com.threemeals.delivery.domain.auth.exception;


import com.threemeals.delivery.config.error.ErrorCode;
import com.threemeals.delivery.domain.common.exception.BaseException;

public class ExpiredTokenException extends BaseException {

    public ExpiredTokenException(String message, ErrorCode errorCode) {
        super(message, errorCode);
    }

    public ExpiredTokenException() {
        super(ErrorCode.TOKEN_EXPIRED);
    }
}
