package com.threemeals.delivery.domain.menu.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.threemeals.delivery.domain.menu.dto.response.MenuResponseDto;

public interface MenuRepositoryForQueryDSL {

	boolean existsByMenuIdAndOwnerId(Long menuId, Long ownerId);

	Page<MenuResponseDto> findAllMenuByStoreId(Long storeId, Pageable pageable);

	void deleteAllMenuOptionsByMenuId(Long menuId);

}
