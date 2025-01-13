package com.threemeals.delivery.domain.user.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.threemeals.delivery.domain.auth.UserPrincipal;
import com.threemeals.delivery.domain.auth.annotation.Authentication;
import com.threemeals.delivery.domain.user.dto.request.DeleteUserRequestDto;
import com.threemeals.delivery.domain.user.dto.request.UpdateUserRequestDto;
import com.threemeals.delivery.domain.user.dto.response.UserResponseDto;
import com.threemeals.delivery.domain.user.service.UserService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class UserApiController {

	private final UserService userService;

	@PutMapping("/users")
	public ResponseEntity<UserResponseDto> update(
		@Authentication UserPrincipal userPrincipal,
		@Valid @RequestBody UpdateUserRequestDto requestDto) {

		UserResponseDto response = userService.updateUser(userPrincipal.getUserId(), requestDto);
		return ResponseEntity.ok(response);
	}

	@DeleteMapping("/users")
	public ResponseEntity<Void> delete(
		@Authentication UserPrincipal userPrincipal,
		@Valid @RequestBody DeleteUserRequestDto requestDto
	) {

		userService.deleteUser(userPrincipal.getUserId(), requestDto);
		return ResponseEntity
			.ok()
			.build();
	}

}
