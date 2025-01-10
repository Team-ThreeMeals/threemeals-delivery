package com.threemeals.delivery.domain.storeLike.dto.response;

public record StoreLikeResponseDto(
	Long storeId,
	Long userId,
	String storeName,
	Boolean isActive
) {

}
