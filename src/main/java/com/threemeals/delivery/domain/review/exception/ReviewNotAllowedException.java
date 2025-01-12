package com.threemeals.delivery.domain.review.exception;

import com.threemeals.delivery.config.error.ErrorCode;
import com.threemeals.delivery.domain.common.exception.InvalidRequestException;

public class ReviewNotAllowedException extends InvalidRequestException {
	public ReviewNotAllowedException() {
		super(ErrorCode.REVIEW_NOT_ALLOWED);
	}
}
