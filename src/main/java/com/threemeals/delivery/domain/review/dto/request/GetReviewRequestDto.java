package com.threemeals.delivery.domain.review.dto.request;

import org.hibernate.validator.constraints.Range;

import jakarta.validation.constraints.NotNull;

public record GetReviewRequestDto(
	@NotNull (message = "가게ID는 필수 입력값입니다.")
	Long storeId,

	@Range(min = 1, max = 5, message = "1~5점 사이로 선택해주세요.")
	Integer minRating,

	@Range(min = 1, max = 5, message = "1~5점 사이로 선택해주세요.")
	Integer maxRating
) {
}
