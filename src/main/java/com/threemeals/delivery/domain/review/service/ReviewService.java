package com.threemeals.delivery.domain.review.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.threemeals.delivery.config.error.ErrorCode;
import com.threemeals.delivery.domain.common.exception.AccessDeniedException;
import com.threemeals.delivery.domain.common.exception.NotFoundException;
import com.threemeals.delivery.domain.menu.entity.OrderStatus;
import com.threemeals.delivery.domain.order.entity.Order;
import com.threemeals.delivery.domain.order.repository.OrderRepository;
import com.threemeals.delivery.domain.review.dto.request.GetReviewRequestDto;
import com.threemeals.delivery.domain.review.dto.request.ReviewCommentRequestDto;
import com.threemeals.delivery.domain.review.dto.request.ReviewRequestDto;
import com.threemeals.delivery.domain.review.dto.response.GetReviewResponseDto;
import com.threemeals.delivery.domain.review.dto.response.ReviewCommentResponseDto;
import com.threemeals.delivery.domain.review.dto.response.ReviewResponseDto;
import com.threemeals.delivery.domain.review.entity.Review;
import com.threemeals.delivery.domain.review.entity.ReviewComment;
import com.threemeals.delivery.domain.review.exception.RatingRangeException;
import com.threemeals.delivery.domain.review.exception.ReviewNotAllowedException;
import com.threemeals.delivery.domain.review.exception.StoreAccessException;
import com.threemeals.delivery.domain.review.repository.ReviewCommentRepository;
import com.threemeals.delivery.domain.review.repository.ReviewRepository;
import com.threemeals.delivery.domain.store.repository.StoreRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ReviewService {
	private final ReviewRepository reviewRepository;
	private final ReviewCommentRepository reviewCommentRepository;
	private final OrderRepository orderRepository;
	private final StoreRepository storeRepository;

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

	public Page<GetReviewResponseDto> getStoreAllReviews(GetReviewRequestDto requestDto, Pageable pageable) {

		// 스토어 존재하는지 확인 용. 임시.  store에 해당 메서드가 존재하면 그걸로 변경
		if (storeRepository.existsById(requestDto.storeId()) == false) {
			throw new NotFoundException(ErrorCode.STORE_NOT_FOUND);
		}

		List<Integer> ratingRange = validateRatingRange(requestDto.minRating(), requestDto.maxRating());

		Page<GetReviewResponseDto> reviewPage = reviewRepository.findAllReviewsWithComments(
			requestDto.storeId(),
			ratingRange.get(0),
			ratingRange.get(1),
			pageable
		);

		return reviewPage;
	}

	@Transactional(rollbackFor = Exception.class)
	public void deleteReview(Long userId, Long reviewID) {

		Review review = reviewRepository.findReviewById(reviewID);

		if (review.getUser().getId() != userId) {
			throw new AccessDeniedException(ErrorCode.REVIEW_ACCESS_DENIED);
		}

		review.deleteReview();

	}

	@Transactional(rollbackFor = Exception.class)
	public void deleteReviewComment(Long ownerId, Long commentId) {
		ReviewComment reviewComment = reviewCommentRepository.findReviewComment(commentId);

		if (reviewComment.getOwner().getId() != ownerId) {
			throw new AccessDeniedException(ErrorCode.COMMENT_ACCESS_DENIED);
		}

		reviewComment.deleteReviewComment();
	}

	private List<Integer> validateRatingRange(Integer minRating, Integer maxRating) {
		int minRange = (minRating == null) ? 1 : minRating;
		int maxRange = (maxRating == null) ? 5 : maxRating;

		if (minRange > maxRange) {
			throw new RatingRangeException();
		}

		return List.of(minRange, maxRange);
	}
}
