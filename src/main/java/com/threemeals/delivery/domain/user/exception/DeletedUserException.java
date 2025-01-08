package com.threemeals.delivery.domain.user.exception;


import com.threemeals.delivery.config.error.ErrorCode;
import com.threemeals.delivery.domain.common.exception.AccessDeniedException;

public class DeletedUserException extends AccessDeniedException {

    public DeletedUserException() {
        super(ErrorCode.USER_DELETED);
    }
}
