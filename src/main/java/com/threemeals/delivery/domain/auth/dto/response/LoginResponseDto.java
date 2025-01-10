package com.threemeals.delivery.domain.auth.dto.response;

public record LoginResponseDto(
	String accessToken,
	String refreshToken
) {
}
