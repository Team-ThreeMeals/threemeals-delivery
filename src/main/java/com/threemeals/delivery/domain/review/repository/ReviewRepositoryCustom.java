package com.threemeals.delivery.domain.review.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import com.threemeals.delivery.domain.review.dto.response.GetReviewResponseDto;

public interface ReviewRepositoryCustom {
	Page<GetReviewResponseDto> findAllReviewsByStoreId(Long storeId, PageRequest pageRequest);
}
