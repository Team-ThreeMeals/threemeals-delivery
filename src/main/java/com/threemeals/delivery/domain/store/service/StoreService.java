package com.threemeals.delivery.domain.store.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.threemeals.delivery.config.error.ErrorCode;
import com.threemeals.delivery.domain.common.exception.AccessDeniedException;
import com.threemeals.delivery.domain.menu.dto.response.MenuResponseDto;
import com.threemeals.delivery.domain.menu.repository.MenuRepository;
import com.threemeals.delivery.domain.store.dto.request.SaveStoreRequestDto;
import com.threemeals.delivery.domain.store.dto.response.StoreDetailResponseDto;
import com.threemeals.delivery.domain.store.dto.response.StoreResponseDto;
import com.threemeals.delivery.domain.store.entity.Store;
import com.threemeals.delivery.domain.store.exception.StoreAlreadyClosedException;
import com.threemeals.delivery.domain.store.exception.StoreLimitExceededException;
import com.threemeals.delivery.domain.store.repository.StoreRepository;
import com.threemeals.delivery.domain.user.entity.User;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class StoreService {
	private final MenuRepository menuRepository;
	private final StoreRepository storeRepository;
	@PersistenceContext
	private EntityManager entityManager;

	@Transactional
	public StoreResponseDto saveStore(SaveStoreRequestDto requestDto, Long userId) {

		// 사장님이 소유한 가게 수 확인
		long storeCount = storeRepository.countByOwnerIdAndIsClosedFalse(userId);
		if (storeCount >= 3) {
			throw new StoreLimitExceededException();
		}

		User owner = entityManager.getReference(User.class, userId);

		Store store = requestDto.toEntity(owner);

		Store savedStore = storeRepository.save(store);
		return StoreResponseDto.toDto(savedStore);
	}

	public Store getStoreById(Long storeId) {
		Store findStore = storeRepository.findByIdOrThrow(storeId);

		findStore.validateIsClosed();
		return findStore;
	}

	public Page<StoreResponseDto> getStoresByName(String name, Pageable pageable) {
		Page<Store> stores = storeRepository.findByStoreNameContainingAndIsClosedFalse(name, pageable);
		return stores.map(StoreResponseDto::toDto);
	}

	public StoreDetailResponseDto getStoreWithMenusById(Long storeId, Pageable pageable) {
		Store store = storeRepository.findByIdOrThrow(storeId);

		Page<MenuResponseDto> menus = menuRepository.findAllMenuByStoreId(storeId, pageable);

		return StoreDetailResponseDto.toDto(store, menus);
	}

	@Transactional
	public StoreResponseDto updateStore(Long storeId, @Valid SaveStoreRequestDto requestDto, Long userId) {
		Store store = storeRepository.findByIdOrThrow(storeId);

		if (!store.getOwner().getId().equals(userId)) {
			throw new AccessDeniedException(ErrorCode.STORE_ACCESS_DENIED);
		}

		store.update(
			requestDto.storeName(),
			requestDto.storeProfileImgUrl(),
			requestDto.address(),
			requestDto.openingTime(),
			requestDto.closingTime(),
			requestDto.deliveryTip(),
			requestDto.minOrderPrice()
		);

		Store updatedStore = storeRepository.save(store);
		return StoreResponseDto.toDto(updatedStore);
	}

	@Transactional
	public void deleteStore(Long storeId, Long userId) {

		Store store = storeRepository.findByIdOrThrow(storeId);
		if (!store.getOwner().getId().equals(userId)) {
			throw new AccessDeniedException(ErrorCode.STORE_ACCESS_DENIED);
		}

		if (store.getIsClosed() == true) {
			throw new StoreAlreadyClosedException();
		}

		// 소프트 삭제
		store.storeClosed();
		storeRepository.save(store);
	}

}
