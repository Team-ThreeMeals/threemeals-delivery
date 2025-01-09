package com.threemeals.delivery.domain.review.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.threemeals.delivery.config.error.ErrorCode;
import com.threemeals.delivery.domain.common.exception.NotFoundException;
import com.threemeals.delivery.domain.review.entity.Review;

public interface ReviewRepository extends JpaRepository<Review, Long> {

	Optional<Review> findById(Long reviewId);

	default Review findReviewById(Long reviewId) {
		return findById(reviewId)
			.orElseThrow(() -> new NotFoundException(ErrorCode.REVIEW_NOT_FOUND));
	}

	@Query("SELECT r " +
		"FROM Review r " +
		"WHERE r.store.id = :storeId " +
		"AND r.isDeleted = false ")
	Page<Review> findAllStoreReviews(@Param("storeId") Long storeId, Pageable pageable);
}
