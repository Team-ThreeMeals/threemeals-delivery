package com.threemeals.delivery.domain.review.dto.response;

import java.util.List;

public record GetReviewResponseDto(
	ReviewResponseDto review,
	List<ReviewCommentResponseDto> comments
) {
}
