package com.threemeals.delivery.domain.store.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.threemeals.delivery.config.error.ErrorCode;
import com.threemeals.delivery.domain.common.exception.NotFoundException;
import com.threemeals.delivery.domain.store.entity.Store;

public interface StoreRepository extends JpaRepository<Store, Long>, StoreRepositoryCustom {
	default Store findByIdOrThrow(Long storeId) {
		return findById(storeId)
			.orElseThrow(() -> new NotFoundException(ErrorCode.STORE_NOT_FOUND));
	}
}
