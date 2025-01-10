package com.threemeals.delivery.domain.store.dto.response;

import java.time.LocalDateTime;
import java.time.LocalTime;

import com.threemeals.delivery.domain.store.entity.Store;

public record StoreResponseDto(
	Long id,
	Long ownerId,
	String storeName,
	String storeProfileImgUrl,
	String address,
	LocalTime openingTime,
	LocalTime closingTime,
	Integer deliverTip,
	Boolean isClosed,
	LocalDateTime updatedAt,
	Integer minOrderPrice
) {
	public static StoreResponseDto toDto(Store store) {
		return new StoreResponseDto(
			store.getId(),
			store.getOwner().getId(),
			store.getStoreName(),
			store.getStoreProfileImgUrl(),
			store.getAddress(),
			store.getOpeningTime(),
			store.getClosingTime(),
			store.getDeliveryTip(),
			store.getIsClosed(),
			store.getUpdatedAt(),
			store.getMinOrderPrice()
		);
	}
}
