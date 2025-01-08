package com.threemeals.delivery.domain.review.dto.request;

import com.threemeals.delivery.domain.order.entity.Order;
import com.threemeals.delivery.domain.review.entity.Review;

public record ReviewRequestDto (
	Long orderId,
	int rating,
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
			.isDeleted(false)
			.build();
	}
}
