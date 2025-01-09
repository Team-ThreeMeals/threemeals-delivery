package com.threemeals.delivery.domain.review.dto.request;

import org.hibernate.validator.constraints.Range;

import com.threemeals.delivery.domain.order.entity.Order;
import com.threemeals.delivery.domain.review.entity.Review;

import jakarta.validation.constraints.NotNull;

public record ReviewRequestDto(
	@NotNull(message = "주문번호는 필수값입니다.")
	Long orderId,

	@Range(min = 1, max = 5, message = "별점은 1점에서 5점 내에서만 가능합니다.")
	@NotNull(message = "평점을 남겨주세요.")
	Integer rating,

	String content,
	String reviewImageUrl
) {

	public Review toReviewEntity(Order order) {
		return Review.builder()
			.order(order)
			.user(order.getUser())
			.store(order.getStore())
			.rating(this.rating)
			.content(this.content)
			.reviewImageUrl(this.reviewImageUrl)
			.build();
	}
}
