package com.threemeals.delivery.domain.review.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.threemeals.delivery.domain.review.dto.response.GetReviewResponseDto;

public interface ReviewRepositoryCustom {
	Page<GetReviewResponseDto> findAllReviewsWithComments(Long storeId, int minRating, int maxRating, Pageable pageable);
}
