package com.threemeals.delivery.domain.menu.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.threemeals.delivery.domain.auth.UserPrincipal;
import com.threemeals.delivery.domain.auth.annotation.Authentication;
import com.threemeals.delivery.domain.menu.dto.request.MenuRequestDto;
import com.threemeals.delivery.domain.menu.dto.response.MenuResponseDto;
import com.threemeals.delivery.domain.menu.service.MenuService;
import com.threemeals.delivery.domain.user.annotation.StoreOwnerOnly;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequiredArgsConstructor
@Slf4j
public class MenuApiController {

	private final MenuService menuService;

	// 가게에 있는 모든 메뉴 조회
	@GetMapping("/menus")
	public ResponseEntity<Page<MenuResponseDto>> getStoreAllMenus(
		@RequestParam Long storeId,
		@RequestParam(defaultValue = "1") int page,
		@RequestParam(defaultValue = "10") int size) {

		Pageable pageable = PageRequest.of(page, size); // 이것도 1페이부터 시작하게 하려면 별도 DTO 객체 만들어야 할 듯.

		Page<MenuResponseDto> allMenus = menuService.getAllMenuInStore(storeId, pageable);
		return ResponseEntity.ok(allMenus);
	}

	// 새로운 메뉴 추가
	@StoreOwnerOnly
	@PostMapping("/menus")
	public ResponseEntity<MenuResponseDto> addNewMenu(
		@RequestParam Long storeId,
		@Authentication UserPrincipal userPrincipal,
		@Valid @RequestBody MenuRequestDto requestDto) {

		MenuResponseDto response = menuService.addMenu(storeId, userPrincipal.getUserId(), requestDto);
		return ResponseEntity.status(HttpStatus.CREATED)
			.body(response);
	}

	// 메뉴 업데이트
	@StoreOwnerOnly
	@PutMapping("/menus/{menuId}")
	public ResponseEntity<MenuResponseDto> updateMenu(
		@PathVariable Long menuId,
		@Authentication UserPrincipal userPrincipal,
		@Valid @RequestBody MenuRequestDto requestDto) {

		MenuResponseDto response = menuService.updateMenu(userPrincipal.getUserId(), menuId, requestDto);
		return ResponseEntity.ok(response);
	}

	// 메뉴 삭제
	@StoreOwnerOnly
	@DeleteMapping("/menus/{menuId}")
	public ResponseEntity<Void> deleteMenu(
		@PathVariable Long menuId,
		@Authentication UserPrincipal userPrincipal
	) {

		menuService.deleteMenu(userPrincipal.getUserId(), menuId);
		return ResponseEntity.ok()
			.build();
	}

}
