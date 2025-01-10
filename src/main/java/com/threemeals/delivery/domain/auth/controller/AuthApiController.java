package com.threemeals.delivery.domain.auth.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.threemeals.delivery.domain.auth.dto.request.LoginRequestDto;
import com.threemeals.delivery.domain.auth.dto.request.SignupRequestDto;
import com.threemeals.delivery.domain.auth.dto.request.UpdateTokenRequestDto;
import com.threemeals.delivery.domain.auth.dto.response.LoginResponseDto;
import com.threemeals.delivery.domain.auth.dto.response.SignupResponseDto;
import com.threemeals.delivery.domain.auth.dto.response.UpdateTokenResponseDto;
import com.threemeals.delivery.domain.auth.service.AuthService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class AuthApiController {

	private final AuthService authService;

	@PostMapping("/signup")
	public ResponseEntity<SignupResponseDto> signup(@Valid @RequestBody SignupRequestDto requestDto) {

		SignupResponseDto response = authService.createUser(requestDto);
		return ResponseEntity.status(HttpStatus.CREATED)
			.body(response);
	}

	@PostMapping("/signup/store-owner")
	public ResponseEntity<SignupResponseDto> singUpStoreOwner(@Valid @RequestBody SignupRequestDto requestDto) {

		SignupResponseDto response = authService.createStoreOwner(requestDto);
		return ResponseEntity.status(HttpStatus.CREATED)
			.body(response);
	}

	@PostMapping("/login")
	public ResponseEntity<LoginResponseDto> login(@Valid @RequestBody LoginRequestDto requestDto) {

		LoginResponseDto response = authService.authenticate(requestDto);
		return ResponseEntity.ok(response);
	}

	@PostMapping("/refresh-token")
	public ResponseEntity<UpdateTokenResponseDto> createNewAccessToken(@Valid @RequestBody UpdateTokenRequestDto requestDto) {

		UpdateTokenResponseDto response = authService.refreshAccessToken(requestDto.refreshToken());
		return ResponseEntity.ok(response);
	}
}
