package com.threemeals.delivery.domain.auth.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import com.threemeals.delivery.config.PasswordEncoder;
import com.threemeals.delivery.config.error.ErrorCode;
import com.threemeals.delivery.config.jwt.TokenProvider;
import com.threemeals.delivery.domain.auth.dto.request.SignupRequestDto;
import com.threemeals.delivery.domain.auth.dto.response.SignupResponseDto;
import com.threemeals.delivery.domain.common.exception.EntityAlreadyExistsException;
import com.threemeals.delivery.domain.user.entity.User;
import com.threemeals.delivery.domain.user.repository.UserRepository;
import com.threemeals.delivery.domain.user.service.UserService;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

	@Mock
	UserService userService;

	@Mock
	UserRepository userRepository;

	@Mock
	PasswordEncoder passwordEncoder;

	@InjectMocks
	AuthService authService;

	@Test
	void 회원가입_성공() {
	    // given
		SignupRequestDto requestDto = new SignupRequestDto(
			"email",
			"username",
			"password",
			null,
			"주소"
		);

		String encodedPassword = "암호화";
		User mockUser = User.builder()
			.username(requestDto.username())
			.password(encodedPassword)
			.email(requestDto.email())
			.profileImgUrl(requestDto.profileImgUrl())
			.address(requestDto.address())
			.build();

		ReflectionTestUtils.setField(mockUser, "id", 1L);

		SignupResponseDto expectedResponse = SignupResponseDto.fromEntity(mockUser);

		doNothing().when(userService).validateEmailAvailability(anyString());
		when(passwordEncoder.encode(anyString())).thenReturn(encodedPassword);
		when(userRepository.save(any(User.class))).thenReturn(mockUser);

		// when
		SignupResponseDto actualResponse = authService.createUser(requestDto);

		// then
		assertThat(actualResponse)
			.usingRecursiveComparison()
			.isEqualTo(expectedResponse);

		verify(userService, times(1)).validateEmailAvailability(anyString());
	}

	// 의미 없는 테스트인 듯 ㅋㅋㅋ
	@Test
	void 중복된_이메일로_회원가입시_실패() {
		// given
		SignupRequestDto requestDto = new SignupRequestDto(
			"email",
			"username",
			"password",
			null,
			"주소"
		);

		doThrow(new EntityAlreadyExistsException(ErrorCode.USER_ALREADY_EXISTS))
			.when(userService).validateEmailAvailability(anyString());

		// when & then
		assertThatThrownBy(() -> authService.createUser(requestDto))
			.isInstanceOf(EntityAlreadyExistsException.class)
			.hasMessage(ErrorCode.USER_ALREADY_EXISTS.getMessage());
	}

}