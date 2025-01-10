package com.threemeals.delivery.domain.review.exception;

import com.threemeals.delivery.config.error.ErrorCode;
import com.threemeals.delivery.domain.common.exception.InvalidRequestException;

public class RatingRangeException extends InvalidRequestException {
	public RatingRangeException() {
		super(ErrorCode.REVIEW_RATING_RANGE_BAD);
	}
}
