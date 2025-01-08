package com.threemeals.delivery.domain.user.service;

import static com.threemeals.delivery.config.error.ErrorCode.*;

import jdk.jfr.TransitionTo;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.threemeals.delivery.config.PasswordEncoder;
import com.threemeals.delivery.domain.auth.exception.AuthenticationException;
import com.threemeals.delivery.domain.common.exception.EntityAlreadyExistsException;
import com.threemeals.delivery.domain.common.exception.NotFoundException;
import com.threemeals.delivery.domain.user.dto.request.DeleteUserRequestDto;
import com.threemeals.delivery.domain.user.dto.request.UpdateUserRequestDto;
import com.threemeals.delivery.domain.user.dto.response.UserResponseDto;
import com.threemeals.delivery.domain.user.entity.User;
import com.threemeals.delivery.domain.user.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class UserService {

	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;

	@Transactional
	public UserResponseDto updateUser(Long userId, UpdateUserRequestDto requestDto) {

		User findUser = getUserById(userId);
		findUser.validateIsDeleted();

		String encodedPassword = passwordEncoder.encode(requestDto.password());
		findUser.updateMe(requestDto, encodedPassword);

		return UserResponseDto.fromEntity(findUser);
	}

	@Transactional
	public void deleteUser(Long userId, DeleteUserRequestDto requestDto) {

		User findUser = getUserById(userId);
		findUser.validateIsDeleted();

		if (passwordEncoder.matches(requestDto.confirmPassword(), findUser.getPassword()) == false) {
			throw new AuthenticationException(INVALID_CREDENTIALS);
		}

		findUser.deleteMe();
	}

	public User getUserById(Long userId) {
		return userRepository.findById(userId)
			.orElseThrow(() -> new NotFoundException(USER_NOT_FOUND));
	}

	public User getUserByEmail(String email) {
		return userRepository.findByEmail(email)
			.orElseThrow(() -> new NotFoundException(USER_NOT_FOUND));
	}

	public void validateEmailAvailability(String email) {
		if (userRepository.existsByEmail(email)) {
			throw new EntityAlreadyExistsException(USER_ALREADY_EXISTS);
		}
	}
}
