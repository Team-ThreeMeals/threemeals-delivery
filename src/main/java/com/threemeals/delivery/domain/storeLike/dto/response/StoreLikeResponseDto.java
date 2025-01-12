package com.threemeals.delivery.domain.storeLike.dto.response;

import com.threemeals.delivery.domain.storeLike.entity.StoreLike;

public record StoreLikeResponseDto(
	Long storeId,
	Long userId,
	String storeName,
	Boolean isActive
) {
	public static StoreLikeResponseDto fromEntity(StoreLike storeLike) {
		return new StoreLikeResponseDto(
			storeLike.getStore().getId(),
			storeLike.getUser().getId(),
			storeLike.getStore().getStoreName(),
			true
		);
	}
}
