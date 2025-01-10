package com.threemeals.delivery.domain.storeLike.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.threemeals.delivery.domain.store.entity.Store;
import com.threemeals.delivery.domain.store.service.StoreService;
import com.threemeals.delivery.domain.storeLike.dto.response.StoreLikeResponseDto;
import com.threemeals.delivery.domain.storeLike.entity.StoreLike;
import com.threemeals.delivery.domain.storeLike.repository.StoreLikeRepository;
import com.threemeals.delivery.domain.user.entity.User;
import com.threemeals.delivery.domain.user.service.UserService;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class StoreLikeService {

	private final StoreLikeRepository storeLikeRepository;
	private final UserService userService;
	private final StoreService storeService;

	@Transactional
	public StoreLikeResponseDto toggleStoreLike(Long userId, Long storeId) {
		User user = userService.getUserById(userId);
		Store store = storeService.getStoreById(storeId);

		// 현재 좋아요 상태 확인
		StoreLike storeLike = storeLikeRepository.findByUserIdAndStoreId(userId, storeId);

		if (storeLike == null) {
			// 좋아요를 처음 추가하는 경우
			storeLike = new StoreLike(store, user, true);
			storeLikeRepository.save(storeLike);
			return new StoreLikeResponseDto(store.getId(), user.getId(), store.getStoreName(), true);
		}

		// 활성/비활성 토글
		storeLike.setIsActive(!storeLike.getIsActive());
		return new StoreLikeResponseDto(store.getId(), user.getId(), store.getStoreName(), storeLike.getIsActive());
	}

	public Page<StoreLikeResponseDto> getUserLikedStores(Long userId, Pageable pageable) {
		// 활성화된 좋아요 목록 조회
		Page<StoreLike> likedStores = storeLikeRepository.findAllByUserIdAndIsActiveTrue(userId, pageable);

		return likedStores.map(StoreLikeResponseDto::fromEntity);
	}

}
