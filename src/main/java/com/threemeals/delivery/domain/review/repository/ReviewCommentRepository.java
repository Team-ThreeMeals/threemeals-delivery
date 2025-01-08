package com.threemeals.delivery.domain.review.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.threemeals.delivery.domain.review.entity.ReviewComment;

public interface ReviewCommentRepository extends JpaRepository<ReviewComment, Long> {
}
