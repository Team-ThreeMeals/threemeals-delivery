package com.threemeals.delivery.domain.store.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.LocalTime;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.threemeals.delivery.domain.common.exception.AccessDeniedException;
import com.threemeals.delivery.domain.menu.repository.MenuRepository;
import com.threemeals.delivery.domain.store.dto.request.SaveStoreRequestDto;
import com.threemeals.delivery.domain.store.entity.Store;
import com.threemeals.delivery.domain.store.exception.StoreAlreadyClosedException;
import com.threemeals.delivery.domain.store.exception.StoreLimitExceededException;
import com.threemeals.delivery.domain.store.repository.StoreRepository;
import com.threemeals.delivery.domain.user.entity.User;

class StoreServiceTest {

	@ExtendWith(MockitoExtension.class)
	@Mock
	private StoreRepository storeRepository;

	@Mock
	private MenuRepository menuRepository;

	@InjectMocks
	private StoreService storeService;

	@Test
	void 사장_한명이_가게를_3개_초과로_생성할경우_예외가_발생한다() {

		// Given
		SaveStoreRequestDto requestDto = new SaveStoreRequestDto("My Store", "profile.jpg", "123 Street",
			LocalTime.of(9, 0, 0),
			LocalTime.of(23, 0, 0), 3000, 15000);
		Long userId = 1L;

		// 이미 가게 개수가 3개라고 설정
		when(storeRepository.countByOwnerIdAndIsClosedFalse(userId)).thenReturn(3L);

		// saveStore 메서드를 호출했을때 StoreLimitExceededException 가 발생하는지 확인
		assertThrows(StoreLimitExceededException.class, () -> {
			storeService.saveStore(requestDto, userId);
		});

		// countByOwnerIdAndIsClosedFalse 1번만 호출되었는지 확인
		// 다른 메서드 호출이 없었는지 확인
		verify(storeRepository, times(1)).countByOwnerIdAndIsClosedFalse(userId);
		verifyNoMoreInteractions(storeRepository);

	}

	@Test
	void 폐업한_가게일경우_조회_할_수_없다() {
		// Given
		Long storeId = 1L;
		Store mockStore = mock(Store.class);
		when(storeRepository.findByIdOrThrow(storeId)).thenReturn(mockStore);
		doThrow(new StoreAlreadyClosedException()).when(mockStore).validateIsClosed();

		// When & Then
		assertThrows(StoreAlreadyClosedException.class, () ->
			storeService.getStoreById(storeId));

	}

	@Test
	void 다른_사람의_가게는_삭제_할_수_없다() {
		// Given
		Long storeId = 1L;
		Long userId = 2L; // 요청한 유저 ID
		User storeOwner = mock(User.class);
		when(storeOwner.getId()).thenReturn(3L); // 가게 주인될사람의 ID는 3L

		Store mockStore = mock(Store.class);
		when(mockStore.getOwner()).thenReturn(storeOwner); // 가게 주인을 설정

		when(storeRepository.findByIdOrThrow(storeId)).thenReturn(mockStore);
		// When & Then

		assertThrows(AccessDeniedException.class, () ->
			storeService.deleteStore(storeId, userId));

		// Mock 호출 검증
		verify(storeRepository, times(1)).findByIdOrThrow(storeId);
		verifyNoMoreInteractions(storeRepository);
	}

	@Test
	void 이미_폐업한_가게는_삭제_할_수_없다() {
		// Given
		Long storeId = 1L;
		Long userId = 1L;
		User storeOwner = mock(User.class);
		Store mockStore = mock(Store.class);

		when(storeOwner.getId()).thenReturn(userId); // 가게 주인 ID
		when(mockStore.getOwner()).thenReturn(storeOwner);
		when(mockStore.getIsClosed()).thenReturn(true); // 가게가 폐업 상태

		when(storeRepository.findByIdOrThrow(storeId)).thenReturn(mockStore);

		// Then
		assertThrows(StoreAlreadyClosedException.class, () ->
			storeService.deleteStore(storeId, userId));
	}

}