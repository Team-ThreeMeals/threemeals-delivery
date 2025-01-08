package com.threemeals.delivery.domain.review.dto.request;

import com.threemeals.delivery.domain.order.entity.Order;
import com.threemeals.delivery.domain.review.entity.Review;

public record ReviewRequestDto (
	Long storeId,
	int rating,
	String content,
	String reviewImageUrl
) {

	public Review toReviewEntity(Order order) {
		return Review.builder()
			.order(order)
			.username(order.getUser().getUsername())
			.storeName(order.getStore().getStoreName())
			.rating(this.rating)
			.content(this.content)
			.reviewImageUrl(this.reviewImageUrl)
			.isDeleted(false)
			.build();
	}
}
