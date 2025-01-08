package com.threemeals.delivery.domain.review.dto.response;

import java.time.LocalDate;

import com.threemeals.delivery.domain.review.entity.Review;

public record ReviewResponseDto(
	String username,
	int rating,
	String content,
	String reviewImageUrl,
	LocalDate createdAt
) {
	public static ReviewResponseDto fromReviewEntity(Review review) {
		return new ReviewResponseDto(
			review.getUsername(),
			review.getRating(),
			review.getContent(),
			review.getReviewImageUrl(),
			review.getCreatedAt().toLocalDate()
		);
	}
}
