package com.threemeals.delivery.domain.common.exception;


import com.threemeals.delivery.config.error.ErrorCode;

public class InvalidRequestException extends BaseException {

    public InvalidRequestException(ErrorCode errorCode) {
        super(errorCode);
    }

    public InvalidRequestException() {
        super(ErrorCode.INVALID_REQUEST);
    }
}
