package com.threemeals.delivery.domain.common.exception;


import com.threemeals.delivery.config.error.ErrorCode;

public class EntityAlreadyExistsException extends BaseException {

    public EntityAlreadyExistsException(ErrorCode errorCode) {
        super(errorCode);
    }

    public EntityAlreadyExistsException() {
        super(ErrorCode.ENTITY_ALREADY_EXISTS);
    }

}
