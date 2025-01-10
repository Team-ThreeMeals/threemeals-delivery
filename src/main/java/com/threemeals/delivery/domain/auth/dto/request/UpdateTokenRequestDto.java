package com.threemeals.delivery.domain.auth.dto.request;

import jakarta.validation.constraints.NotBlank;

public record UpdateTokenRequestDto(
	@NotBlank(message = "토큰 값은 필수입니다")
	String refreshToken
) {
}
