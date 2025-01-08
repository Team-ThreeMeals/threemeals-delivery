package com.threemeals.delivery.domain.menu.exception;


import static com.threemeals.delivery.config.error.ErrorCode.*;

import com.threemeals.delivery.config.error.ErrorCode;
import com.threemeals.delivery.domain.common.exception.AccessDeniedException;

public class DeletedMenuOptionException extends AccessDeniedException {

    public DeletedMenuOptionException() {
        super(MENU_OPTION_DELETED);
    }
}
