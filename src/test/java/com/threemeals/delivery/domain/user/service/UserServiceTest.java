package com.threemeals.delivery.domain.user.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import com.threemeals.delivery.config.PasswordEncoder;
import com.threemeals.delivery.domain.user.dto.request.DeleteUserRequestDto;
import com.threemeals.delivery.domain.user.dto.request.UpdateUserRequestDto;
import com.threemeals.delivery.domain.user.dto.response.UserResponseDto;
import com.threemeals.delivery.domain.user.entity.Role;
import com.threemeals.delivery.domain.user.entity.User;
import com.threemeals.delivery.domain.user.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

	@Mock
	UserRepository userRepository;

	@Mock
	PasswordEncoder passwordEncoder;

	@Spy
	@InjectMocks
	UserService userService;

	@Test
	void 사용자_정보_업데이트_성공() {
		// given
		Long userId = 1L;
		User existingUser = User.builder()
			.username("강성욱")
			.password("123")
			.role(Role.USER)
			.address("뉴욕특별시")
			.build();

		ReflectionTestUtils.setField(existingUser, "id", userId);

		UpdateUserRequestDto requestDto = new UpdateUserRequestDto("변경된_이름", "새로운_비밀번호",  "새로운_이미지_URL", "변경된 주소");

		when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));
		when(passwordEncoder.encode(any(String.class))).thenReturn(requestDto.password());

		// when
		UserResponseDto response = userService.updateUser(existingUser.getId(), requestDto);

		// then
		assertThat(existingUser.getUsername()).isEqualTo(response.username());
		assertThat(existingUser.getAddress()).isEqualTo(response.address());
		assertThat(existingUser.getProfileImgUrl()).isEqualTo(response.profileImgUrl());
		assertThat(existingUser.getPassword()).isEqualTo(requestDto.password());

		verify(userService, times(1)).getUserById(userId);
	}

	@Test
	void 회원_삭제_성공() {
	    // given
		// given
		Long userId = 1L;
		User existingUser = User.builder()
			.username("강성욱")
			.password("123")
			.role(Role.USER)
			.address("뉴욕특별시")
			.build();

		ReflectionTestUtils.setField(existingUser, "id", userId);

		DeleteUserRequestDto requestDto = new DeleteUserRequestDto(existingUser.getPassword());

		when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));
		when(passwordEncoder.matches(anyString(), anyString())).thenReturn(true);

		// when
		userService.deleteUser(existingUser.getId(), requestDto);


	    // then
		assertThat(existingUser.getIsDeleted()).isTrue();

	}

	@Test
	void 유저_정보_업데이트_테스트() {
	    // given
		UpdateUserRequestDto requestDto = new UpdateUserRequestDto(
			"user1", "123", null, "뉴욕특별시"
		);

		User existingUser = User.builder()
			.username("user1")
			.password("123")
			.role(Role.USER)
			.address("뉴욕특별시")
			.build();
		ReflectionTestUtils.setField(existingUser, "id", 1L);

		UserResponseDto expectedResult = UserResponseDto.fromEntity(existingUser);

		when(userRepository.findById(anyLong())).thenReturn(Optional.of(existingUser));
		when(passwordEncoder.encode(anyString())).thenReturn(requestDto.password());

	    // when
		UserResponseDto actualResult = userService.updateUser(existingUser.getId(), requestDto);

		// then
		assertThat(actualResult)
			.usingRecursiveComparison()
			.isEqualTo(expectedResult);
	}

}