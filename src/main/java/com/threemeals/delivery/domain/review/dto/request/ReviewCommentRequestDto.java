package com.threemeals.delivery.domain.review.dto.request;

import com.threemeals.delivery.domain.review.entity.Review;
import com.threemeals.delivery.domain.review.entity.ReviewComment;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ReviewCommentRequestDto(
	@NotNull(message = "리뷰 선택은 필수입니다.")
	Long reviewId,

	@NotBlank(message = "내용을 입력해주세요.")
	String content
) {
	public ReviewComment toReviewCommentEntity(Review review) {
		return ReviewComment.builder()
			.review(review)
			.owner(review.getStore().getOwner())
			.content(this.content)
			.isDeleted(false)
			.build();
	}
}
