package com.threemeals.delivery.domain.storeLike.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.threemeals.delivery.domain.auth.UserPrincipal;
import com.threemeals.delivery.domain.auth.annotation.Authentication;
import com.threemeals.delivery.domain.storeLike.dto.request.StoreLikeRequestDto;
import com.threemeals.delivery.domain.storeLike.dto.response.StoreLikeResponseDto;
import com.threemeals.delivery.domain.storeLike.service.StoreLikeService;

import lombok.AllArgsConstructor;

@RestController
@AllArgsConstructor
@RequestMapping("/store-likes")
public class StoreLikeApiController {

	private final StoreLikeService storeLikeService;

	@PostMapping
	public ResponseEntity<StoreLikeResponseDto> toggleStoreLike(
		@RequestBody StoreLikeRequestDto requestDto,
		@Authentication UserPrincipal userPrincipal) {
		StoreLikeResponseDto response = storeLikeService.toggleStoreLike(userPrincipal.getUserId(),
			requestDto.storeId());
		return ResponseEntity.ok(response);
	}

	@GetMapping
	public ResponseEntity<Page<StoreLikeResponseDto>> getUserLikedStores(
		@Authentication UserPrincipal userPrincipal,
		@RequestParam(defaultValue = "1") int page,
		@RequestParam(defaultValue = "5") int size) {

		// 시작 페이지를 1로
		Pageable adjustedPageable = PageRequest.of(page - 1, size);
		// 현재 사용자의 좋아요한 가게 목록 조회
		Page<StoreLikeResponseDto> response = storeLikeService.getUserLikedStores(userPrincipal.getUserId(),
			adjustedPageable);
		return ResponseEntity.ok(response);
	}

}
