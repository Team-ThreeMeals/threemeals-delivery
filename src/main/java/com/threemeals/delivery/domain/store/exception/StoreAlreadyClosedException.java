package com.threemeals.delivery.domain.store.exception;

import com.threemeals.delivery.config.error.ErrorCode;
import com.threemeals.delivery.domain.common.exception.BaseException;

public class StoreAlreadyClosedException extends BaseException {
	public StoreAlreadyClosedException() {super(ErrorCode.STORE_ALREADY_CLOSED);}
}
