package com.threemeals.delivery.domain.store.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.threemeals.delivery.domain.auth.UserPrincipal;
import com.threemeals.delivery.domain.auth.annotation.Authentication;
import com.threemeals.delivery.domain.store.dto.request.SaveStoreRequestDto;
import com.threemeals.delivery.domain.store.dto.response.StoreDetailResponseDto;
import com.threemeals.delivery.domain.store.dto.response.StoreResponseDto;
import com.threemeals.delivery.domain.store.service.StoreService;
import com.threemeals.delivery.domain.user.annotation.StoreOwnerOnly;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping("/stores")
public class StoreApiController {

	private final StoreService storeService;

	@StoreOwnerOnly
	@PostMapping
	public ResponseEntity<StoreResponseDto> saveStore(
		@Valid @RequestBody SaveStoreRequestDto requestDto,
		@Authentication UserPrincipal userPrincipal
	) {
		log.info("가게 생성 시도, name={} tip ={}",requestDto.storeName(),requestDto.deliveryTip());
		Long userId = userPrincipal.getUserId();

		StoreResponseDto responseDto = storeService.saveStore(requestDto, userId);
		return ResponseEntity.ok(responseDto);
	}

	// 다건 조회 (가게명으로 검색)
	@GetMapping
	public ResponseEntity<Page<StoreResponseDto>> getStores(
		@RequestParam String name,
		@RequestParam(defaultValue = "1") int page,
		@RequestParam(defaultValue = "5") int size) {

		// 시작 페이지를 1로
		Pageable adjustedPageable = PageRequest.of(page - 1, size);

		Page<StoreResponseDto> stores = storeService.getStoresByName(name, adjustedPageable);
		return ResponseEntity.ok(stores);
	}

	// 단건 조회 (가게 ID로 조회하면 메뉴랑 같이 조회)
	@GetMapping("/{storeId}")
	public ResponseEntity<StoreDetailResponseDto> getStoreWithMenus(
		@PathVariable Long storeId,
		@RequestParam(defaultValue = "1") int page,
		@RequestParam(defaultValue = "5") int size
	) {
		Pageable pageable = PageRequest.of(page - 1, size);

		StoreDetailResponseDto response = storeService.getStoreWithMenusById(storeId, pageable);
		return ResponseEntity.ok(response);
	}

	@StoreOwnerOnly
	@PutMapping("/{storeId}")
	public ResponseEntity<StoreResponseDto> updateStore(
		@PathVariable Long storeId,
		@Valid @RequestBody SaveStoreRequestDto requestDto,
		@Authentication UserPrincipal userPrincipal
	) {
		Long userId = userPrincipal.getUserId();
		StoreResponseDto updatedStore = storeService.updateStore(storeId, requestDto, userId);
		return ResponseEntity.ok(updatedStore);
	}

	@StoreOwnerOnly
	@DeleteMapping("/{storeId}")
	public ResponseEntity<Void> deleteStore(
		@PathVariable Long storeId,
		@Authentication UserPrincipal userPrincipal
	) {
		Long userId = userPrincipal.getUserId();
		storeService.deleteStore(storeId, userId);
		return ResponseEntity.noContent().build();
	}

}
