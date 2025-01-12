package com.threemeals.delivery.domain.store.exception;

import com.threemeals.delivery.config.error.ErrorCode;
import com.threemeals.delivery.domain.common.exception.AccessDeniedException;

public class StoreAccessException extends AccessDeniedException {
	public StoreAccessException() {
		super(ErrorCode.ACCESS_DENIED);
	}
}
