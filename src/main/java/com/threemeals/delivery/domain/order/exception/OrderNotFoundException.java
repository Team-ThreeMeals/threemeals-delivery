package com.threemeals.delivery.domain.order.exception;

import com.threemeals.delivery.config.error.ErrorCode;
import com.threemeals.delivery.domain.common.exception.NotFoundException;

public class OrderNotFoundException extends NotFoundException {
	public OrderNotFoundException() {
		super(ErrorCode.ORDER_NOT_FOUND);
	}
}
