package com.threemeals.delivery.domain.user.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record UpdateUserRequestDto(

	@NotBlank(message = "이름은 필수 입력값입니다.")
	@Size(message = "100자 이하로 입력해주세요.", max = 100)
	String username,

	@NotBlank(message = "비밀번호는 필수 입력값입니다.")
	@Pattern(
		regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,30}$",
		message = "비밀번호는 최소 8자 이상, 대소문자, 숫자, 특수문자를 포함해야 합니다."
	)
	String password,

	String profileImgUrl,

	@NotBlank(message = "주소는 필수로 입력하셔야 합니다.")
	String address
) {

}
