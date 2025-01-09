package com.threemeals.delivery.domain.review.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.threemeals.delivery.config.error.ErrorCode;
import com.threemeals.delivery.domain.common.exception.NotFoundException;
import com.threemeals.delivery.domain.review.entity.Review;

public interface ReviewRepository extends JpaRepository<Review, Long>, ReviewRepositoryCustom {

	Optional<Review> findById(Long reviewId);

	default Review findReviewById(Long reviewId) {
		return findById(reviewId)
			.orElseThrow(() -> new NotFoundException(ErrorCode.REVIEW_NOT_FOUND));
	}
}
