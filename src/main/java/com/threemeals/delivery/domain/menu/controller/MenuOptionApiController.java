package com.threemeals.delivery.domain.menu.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.threemeals.delivery.domain.auth.UserPrincipal;
import com.threemeals.delivery.domain.auth.annotation.Authentication;
import com.threemeals.delivery.domain.menu.dto.request.MenuOptionRequestDto;
import com.threemeals.delivery.domain.menu.dto.response.GetMenuWithOptionsResponseDto;
import com.threemeals.delivery.domain.menu.dto.response.MenuOptionResponseDto;
import com.threemeals.delivery.domain.menu.service.MenuOptionService;
import com.threemeals.delivery.domain.user.annotation.StoreOwnerOnly;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class MenuOptionApiController {

	private final MenuOptionService menuOptionService;

	// 메뉴랑 메뉴 옵션이 섞여서 API controller 선택을 어디로 할지 헷갈리네...
	// 특정 메뉴 1개 + 해당 메뉴에 대한 서브 옵션 모두 가져오기
	@GetMapping("/menus/{menuId}")
	public ResponseEntity<GetMenuWithOptionsResponseDto> getMenuWithOptions(
		@PathVariable Long menuId
	) {

		GetMenuWithOptionsResponseDto response = menuOptionService.getMenuWithOptions(menuId);
		return ResponseEntity.ok(response);
	}

	// 메뉴 옵션 추가
	@StoreOwnerOnly
	@PostMapping("/menus/{menuId}/menuoptions")
	public ResponseEntity<MenuOptionResponseDto> addMenuOption(
		@PathVariable Long menuId,
		@Authentication UserPrincipal userPrincipal,
		@Valid @RequestBody MenuOptionRequestDto requestDto
	) {

		MenuOptionResponseDto response = menuOptionService.addMenuOption(userPrincipal.getUserId(), menuId, requestDto);
		return ResponseEntity.status(HttpStatus.CREATED)
			.body(response);
	}

	// 메뉴 옵션 변경
	@StoreOwnerOnly
	@PutMapping("/menus/menuoptions/{menuOptionId}")
	public ResponseEntity<MenuOptionResponseDto> updateMenuOption(
		@PathVariable Long menuOptionId,
		@Authentication UserPrincipal userPrincipal,
		@Valid @RequestBody MenuOptionRequestDto requestDto
	) {

		MenuOptionResponseDto response
			= menuOptionService.updateMenuOption(userPrincipal.getUserId(), menuOptionId, requestDto);
		return ResponseEntity.ok(response);
	}

	// 메뉴 옵션 삭제
	@StoreOwnerOnly
	@DeleteMapping("/menus/menuoptions/{menuOptionId}")
	public ResponseEntity<Void> deleteMenuOption(
		@PathVariable Long menuOptionId,
		@Authentication UserPrincipal userPrincipal
	) {

		menuOptionService.deleteMenuOption(userPrincipal.getUserId(), menuOptionId);
		return ResponseEntity.status(HttpStatus.OK)
			.build();
	}

}
