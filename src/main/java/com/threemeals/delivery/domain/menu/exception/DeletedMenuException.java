package com.threemeals.delivery.domain.menu.exception;


import com.threemeals.delivery.config.error.ErrorCode;
import com.threemeals.delivery.domain.common.exception.AccessDeniedException;

public class DeletedMenuException extends AccessDeniedException {

    public DeletedMenuException() {
        super(ErrorCode.MENU_DELETED);
    }
}
