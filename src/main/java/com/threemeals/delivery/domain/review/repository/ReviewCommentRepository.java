package com.threemeals.delivery.domain.review.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.threemeals.delivery.domain.review.entity.ReviewComment;

public interface ReviewCommentRepository extends JpaRepository<ReviewComment, Long> {
	@Query("SELECT rc " +
		"FROM ReviewComment rc " +
		"WHERE rc.isDeleted = false " +
		"AND rc.review.id " +
		"IN :reviewIds")
	List<ReviewComment> findByReviewIds(@Param("reviewIds") List<Long> reviewIds);
}
