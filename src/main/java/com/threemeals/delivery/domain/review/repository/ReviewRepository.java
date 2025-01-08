package com.threemeals.delivery.domain.review.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.threemeals.delivery.domain.review.entity.Review;

public interface ReviewRepository extends JpaRepository<Review, Long> {
}
