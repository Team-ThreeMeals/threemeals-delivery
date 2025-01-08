package com.threemeals.delivery.domain.store.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.threemeals.delivery.domain.store.entity.Store;

public interface StoreRepository extends JpaRepository<Store, Long> {
	long countByOwnerIdAndIsClosedFalse(Long ownerId); // 사장님이 소유한 가게 수

	List<Store> findByStoreNameContaining(String name); // 가게명 검색 메서드

	// 가게명에 특정 문자열이 포함되고 폐업하지 않은 가게를 조회
	List<Store> findByStoreNameContainingAndIsClosedFalse(String storeName);
}
