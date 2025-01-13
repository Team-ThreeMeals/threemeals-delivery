package com.threemeals.delivery.domain.review.dto.response;

import java.time.LocalDate;

import com.threemeals.delivery.domain.review.entity.ReviewComment;

public record ReviewCommentResponseDto(
	String owner,
	String content,
	LocalDate createdAt
) {
	public static ReviewCommentResponseDto fromReviewCommentEntity(ReviewComment reviewComment) {
		return new ReviewCommentResponseDto(
			"사장님",
			reviewComment.getContent(),
			reviewComment.getCreatedAt().toLocalDate()
		);
	}
}
