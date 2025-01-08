package com.threemeals.delivery.domain.auth.dto.request;

import com.threemeals.delivery.domain.user.entity.Role;
import com.threemeals.delivery.domain.user.entity.User;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record SignupRequestDto(

	@NotBlank(message = "이메일은 필수 입력값입니다.")
	@Email(message = "이메일 형식이 아닙니다.")
	String email,

	@NotBlank(message = "이름은 필수 입력값입니다.")
	@Size(message = "100자 이하로 입력해주세요.", max = 100)
	String username,

	@NotBlank(message = "비밀번호는 필수 입력값입니다.")
	// @Pattern(
	// 	regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,30}$",
	// 	message = "비밀번호는 최소 8자 이상, 대소문자, 숫자, 특수문자를 포함해야 합니다."
	// )
	String password,

	String profileImgUrl,

	@NotBlank(message = "주소는 필수로 입력하셔야 합니다.")
	String address
) {

	public User toEntityAsUser(String encodedPassword) {
		return User.builder()
			.username(username)
			.email(email)
			.password(encodedPassword)
			.role(Role.USER)
			.address(address)
			.build();
	}

	public User toEntityAsStoreOwner(String encodedPassword) {
		return User.builder()
			.username(username)
			.email(email)
			.password(encodedPassword)
			.role(Role.STORE_OWNER)
			.address(address)
			.build();
	}
}
