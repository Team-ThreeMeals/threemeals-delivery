package com.threemeals.delivery.domain.auth.exception;


import com.threemeals.delivery.config.error.ErrorCode;
import com.threemeals.delivery.domain.common.exception.InvalidRequestException;

public class InvalidTokenException extends InvalidRequestException {

    public InvalidTokenException() {
        super(ErrorCode.INVALID_TOKEN);
    }
}
