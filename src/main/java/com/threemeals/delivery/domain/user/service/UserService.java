package com.threemeals.delivery.domain.user.service;

import static com.threemeals.delivery.config.error.ErrorCode.*;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import com.threemeals.delivery.domain.common.exception.EntityAlreadyExistsException;
import com.threemeals.delivery.domain.common.exception.NotFoundException;
import com.threemeals.delivery.domain.user.entity.User;
import com.threemeals.delivery.domain.user.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class UserService {

	private final UserRepository userRepository;


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
