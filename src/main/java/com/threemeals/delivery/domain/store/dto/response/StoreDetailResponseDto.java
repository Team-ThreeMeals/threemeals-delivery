package com.threemeals.delivery.domain.store.dto.response;

import java.time.LocalTime;

import org.springframework.data.domain.Page;

import com.threemeals.delivery.domain.menu.dto.response.MenuResponseDto;
import com.threemeals.delivery.domain.store.entity.Store;

public record StoreDetailResponseDto(
	Long id,
	Long ownerId,
	String storeName,
	String storeProfileImgUrl,
	String address,
	LocalTime openingTime,
	LocalTime closingTime,
	Integer deliverTip,
	Integer minOrderPrice,
	Page<MenuResponseDto> menus
) {

	public static StoreDetailResponseDto toDto(Store store, Page<MenuResponseDto> menus) {
		return new StoreDetailResponseDto(
			store.getId(),
			store.getOwner().getId(),
			store.getStoreName(),
			store.getStoreProfileImgUrl(),
			store.getAddress(),
			store.getOpeningTime(),
			store.getClosingTime(),
			store.getDeliveryTip(),
			store.getMinOrderPrice(),
			menus
		);
	}
}
