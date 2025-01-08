package com.threemeals.delivery.domain.auth.dto.response;

import com.threemeals.delivery.domain.user.entity.User;

public record SignupResponseDto(
	Long id,
	String username,
	String email,
	String address
) {

	public static SignupResponseDto fromEntity(User user) {
		return new SignupResponseDto(
			user.getId(),
			user.getUsername(),
			user.getEmail(),
			user.getAddress()
		);
	}
}
