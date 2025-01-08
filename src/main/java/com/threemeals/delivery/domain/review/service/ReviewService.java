package com.threemeals.delivery.domain.review.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.threemeals.delivery.domain.menu.entity.OrderStatus;
import com.threemeals.delivery.domain.order.entity.Order;
import com.threemeals.delivery.domain.order.repository.OrderRepository;
import com.threemeals.delivery.domain.review.dto.request.ReviewCommentRequestDto;
import com.threemeals.delivery.domain.review.dto.request.ReviewRequestDto;
import com.threemeals.delivery.domain.review.dto.response.ReviewCommentResponseDto;
import com.threemeals.delivery.domain.review.dto.response.ReviewResponseDto;
import com.threemeals.delivery.domain.review.entity.Review;
import com.threemeals.delivery.domain.review.entity.ReviewComment;
import com.threemeals.delivery.domain.review.exception.ReviewNotAllowedException;
import com.threemeals.delivery.domain.review.exception.StoreAccessException;
import com.threemeals.delivery.domain.review.repository.ReviewCommentRepository;
import com.threemeals.delivery.domain.review.repository.ReviewRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ReviewService {
	private final ReviewRepository reviewRepository;
	private final ReviewCommentRepository reviewCommentRepository;
	private final OrderRepository orderRepository;

	@Transactional
	public ReviewResponseDto saveReview(ReviewRequestDto requestDto, Long userId) {

		Order order = orderRepository.findOrderByOrderIdAndUserId(requestDto.orderId(), userId);

		if (order.getOrderStatus() != OrderStatus.DELIVERED) {
			throw new ReviewNotAllowedException();
		}

		Review review = requestDto.toReviewEntity(order);

		return ReviewResponseDto.fromReviewEntity(reviewRepository.save(review));
	}

	@Transactional
	public ReviewCommentResponseDto saveReviewComment(ReviewCommentRequestDto requestDto, Long ownerId) {

		Review review = reviewRepository.findReviewById(requestDto.reviewId());

		if (review.getStore().getOwner().getId() != ownerId) {
			throw new StoreAccessException();
		}

		ReviewComment reviewComment = requestDto.toReviewCommentEntity(review);

		return ReviewCommentResponseDto.fromReviewCommentEntity(reviewCommentRepository.save(reviewComment));
	}

}
