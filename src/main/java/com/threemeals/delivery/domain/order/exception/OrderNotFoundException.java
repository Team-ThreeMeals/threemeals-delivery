package com.threemeals.delivery.domain.order.exception;

import com.threemeals.delivery.config.error.ErrorCode;
import com.threemeals.delivery.domain.common.exception.AccessDeniedException;

public class OrderNotFoundException extends AccessDeniedException {
	public OrderNotFoundException() {
		super(ErrorCode.ACCESS_DENIED);
	}
}
