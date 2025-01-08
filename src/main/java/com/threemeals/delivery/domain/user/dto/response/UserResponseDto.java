package com.threemeals.delivery.domain.user.dto.response;

import com.threemeals.delivery.domain.user.entity.User;

public record UserResponseDto(
	Long id,
	String username,
	String email,
	String address
) {

	public static UserResponseDto fromEntity(User user) {
		return new UserResponseDto(
			user.getId(),
			user.getUsername(),
			user.getEmail(),
			user.getAddress()
		);
	}
}
