package com.threemeals.delivery.domain.store.controller;

import java.util.List;

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
import com.threemeals.delivery.domain.store.dto.response.StoreResponseDto;
import com.threemeals.delivery.domain.store.service.StoreService;
import com.threemeals.delivery.domain.user.annotation.StoreOwnerOnly;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;

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
		Long userId = userPrincipal.getUserId();

		StoreResponseDto responseDto = storeService.saveStore(requestDto, userId);
		return ResponseEntity.ok(responseDto);
	}

	// 다건 조회 (가게명으로 검색)
	@GetMapping
	public ResponseEntity<List<StoreResponseDto>> getStores(@RequestParam String name) {
		List<StoreResponseDto> stores = storeService.getStoresByName(name);
		return ResponseEntity.ok(stores);
	}

	@StoreOwnerOnly
	@PutMapping("/{storeId}")
	public ResponseEntity<StoreResponseDto> updateStore(
		@PathVariable Long storeId,
		@Valid @RequestBody SaveStoreRequestDto requestDto,
		@Authentication UserPrincipal userPrincipal
	) {
		Long userId = userPrincipal.getUserId(); // 현재 로그인한 사용자 ID
		StoreResponseDto updatedStore = storeService.updateStore(storeId, requestDto, userId);
		return ResponseEntity.ok(updatedStore);
	}

	@StoreOwnerOnly
	@DeleteMapping("/{storeId}")
	public ResponseEntity<Void> deleteStore(
		@PathVariable Long storeId,
		@Authentication UserPrincipal userPrincipal
	) {
		Long userId = userPrincipal.getUserId(); // 현재 로그인한 사용자 ID
		storeService.deleteStore(storeId, userId);
		return ResponseEntity.noContent().build();
	}

}
