package com.threemeals.delivery.domain.store.exception;

import com.threemeals.delivery.config.error.ErrorCode;
import com.threemeals.delivery.domain.common.exception.BaseException;

public class StoreLimitExceededException extends BaseException {
	public StoreLimitExceededException() {
		super(ErrorCode.STORE_LIMIT_EXCEEDED);
	}

}
