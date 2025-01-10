package com.threemeals.delivery.domain.common.exception;


import com.threemeals.delivery.config.error.ErrorCode;

public class NotFoundException extends BaseException {

    public NotFoundException(ErrorCode errorCode) {
        super(errorCode);
    }

    public NotFoundException() {
        super(ErrorCode.NOT_FOUND);
    }
}
