package com.threemeals.delivery.domain.review.service;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
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
import com.threemeals.delivery.domain.review.exception.ReviewNotAllowedException;
import com.threemeals.delivery.domain.review.exception.StoreAccessException;
import com.threemeals.delivery.domain.review.repository.ReviewCommentRepository;
import com.threemeals.delivery.domain.review.repository.ReviewRepository;
import com.threemeals.delivery.domain.store.entity.Store;
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

	/**
	 * 1. 리뷰 페이지로 불러와
	 * 2. 해당 리뷰 아이디 추출해
	 * 3. 그 리뷰에 해당하는 댓글들을 그룹화? 해
	 *
	 * + 쿼리로 가능할까? review를 기준으로 groupBy 쓰면?
	 */
	public Page<GetReviewResponseDto> getStoreAllReviews(GetReviewRequestDto requestDto, Pageable pageable) {

		// 임시.  store에 해당 메서드가 존재하면 그걸로 변경
		Store store = storeRepository.findById(requestDto.storeId())
			.orElseThrow(() -> new NotFoundException(ErrorCode.STORE_NOT_FOUND));

		Page<Review> reviewPage = reviewRepository.findAllStoreReviews(requestDto.storeId(), pageable);

		List<Long> reviewIds = reviewPage.getContent().stream()
			.map(Review::getId)
			.toList();

		Map<Review, List<ReviewCommentResponseDto>> commentsGroupedByReview =
			reviewCommentRepository.findByReviewIds(reviewIds).stream()
				.collect(Collectors.groupingBy(
					ReviewComment::getReview,
					Collectors.mapping(ReviewCommentResponseDto::fromReviewCommentEntity,
						Collectors.toList())
				));

		List<GetReviewResponseDto> reviewList = reviewPage.getContent().stream()
			.map(review -> new GetReviewResponseDto(
				ReviewResponseDto.fromReviewEntity(review),
				commentsGroupedByReview.getOrDefault(review,
					Collections.emptyList())
			))
			.toList();

		return PageableExecutionUtils.getPage(reviewList, pageable, reviewPage::getTotalElements);
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
}
