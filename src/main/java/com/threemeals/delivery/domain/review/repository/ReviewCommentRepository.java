package com.threemeals.delivery.domain.review.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.threemeals.delivery.config.error.ErrorCode;
import com.threemeals.delivery.domain.common.exception.NotFoundException;
import com.threemeals.delivery.domain.review.entity.ReviewComment;

public interface ReviewCommentRepository extends JpaRepository<ReviewComment, Long> {
	@Query("SELECT rc " +
		"FROM ReviewComment rc " +
		"WHERE rc.review.id " +
		"IN :reviewIds " +
		"ORDER BY rc.createdAt ASC")
	List<ReviewComment> findByReviewIds(@Param("reviewIds") List<Long> reviewIds);

	Optional<ReviewComment> findById (Long reviewCommentId);

	default ReviewComment findReviewComment(Long reviewCommentId) {
		return findById(reviewCommentId)
			.orElseThrow(() -> new NotFoundException(ErrorCode.COMMENT_NOT_FOUND));
	}
}
