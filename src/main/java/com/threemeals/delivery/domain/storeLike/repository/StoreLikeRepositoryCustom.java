package com.threemeals.delivery.domain.storeLike.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.threemeals.delivery.domain.storeLike.entity.StoreLike;

public interface StoreLikeRepositoryCustom {

	// 유저와 특정 가게의 엔티티 조회
	StoreLike findByUserIdAndStoreId(Long userId, Long storeId);

	// 사용자가 좋아요한 가게 목록 조회
	Page<StoreLike> findAllByUserIdAndIsActiveTrue(Long userId, Pageable pageable);
}
