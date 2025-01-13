package com.threemeals.delivery.domain.store.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.threemeals.delivery.domain.store.entity.Store;

public interface StoreRepositoryCustom {

	// 사장님이 소유한 가게 수
	long countByOwnerIdAndIsClosedFalse(Long ownerId);

	// 가게명에 특정 문자열이 포함되고 폐업하지 않은 가게를 조회
	Page<Store> findByStoreNameContainingAndIsClosedFalse(String storeName, Pageable pageable);

	Page<Store> findByIsClosedFalse(Pageable pageable);
}
