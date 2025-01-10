package com.threemeals.delivery.domain.storeLike.dto.request;

import com.threemeals.delivery.domain.store.entity.Store;
import com.threemeals.delivery.domain.storeLike.entity.StoreLike;
import com.threemeals.delivery.domain.user.entity.User;

public record StoreLikeRequestDto(
	Long storeId // 요청받을 가게 ID
) {

	public StoreLike toEntity(User user, Store store) {
		return new StoreLike(store, user, true); // 활성 상태로 설정
	}

}
