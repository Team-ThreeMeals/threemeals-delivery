package com.threemeals.delivery.domain.review.dto.request;

import jakarta.validation.constraints.NotNull;

public record GetReviewRequestDto(
	@NotNull (message = "가게ID는 필수 입력값입니다.")
	Long storeId
) {
}
