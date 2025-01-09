package com.threemeals.delivery.domain.review.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.threemeals.delivery.config.error.ErrorCode;
import com.threemeals.delivery.domain.common.exception.NotFoundException;
import com.threemeals.delivery.domain.review.entity.ReviewComment;

public interface ReviewCommentRepository extends JpaRepository<ReviewComment, Long> {

	Optional<ReviewComment> findById (Long reviewCommentId);

	default ReviewComment findReviewComment(Long reviewCommentId) {
		return findById(reviewCommentId)
			.orElseThrow(() -> new NotFoundException(ErrorCode.COMMENT_NOT_FOUND));
	}
}
