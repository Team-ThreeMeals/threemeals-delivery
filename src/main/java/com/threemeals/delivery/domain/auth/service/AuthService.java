package com.threemeals.delivery.domain.auth.service;

import static com.threemeals.delivery.config.error.ErrorCode.*;
import static com.threemeals.delivery.config.util.Token.*;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.threemeals.delivery.config.PasswordEncoder;
import com.threemeals.delivery.config.jwt.TokenProvider;
import com.threemeals.delivery.domain.auth.dto.request.LoginRequestDto;
import com.threemeals.delivery.domain.auth.dto.request.SignupRequestDto;
import com.threemeals.delivery.domain.auth.dto.response.LoginResponseDto;
import com.threemeals.delivery.domain.auth.dto.response.SignupResponseDto;
import com.threemeals.delivery.domain.auth.exception.AuthenticationException;
import com.threemeals.delivery.domain.user.entity.User;
import com.threemeals.delivery.domain.user.repository.UserRepository;
import com.threemeals.delivery.domain.user.service.UserService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthService {

	private final UserService userService; // User 도메인에 너무 많은 의존을 해서 좋지는 않다.
	private final UserRepository userRepository;
	private final TokenProvider tokenProvider;
	private final PasswordEncoder passwordEncoder;


	@Transactional
	public SignupResponseDto createUser(SignupRequestDto requestDto) {

		userService.validateEmailAvailability(requestDto.email());

		String encodedPassword = passwordEncoder.encode(requestDto.password());
		User savedUser = userRepository.save(requestDto.toEntityAsUser(encodedPassword));

		return SignupResponseDto.fromEntity(savedUser);
	}

	@Transactional
	public SignupResponseDto createStoreOwner(SignupRequestDto requestDto) {

		userService.validateEmailAvailability(requestDto.email());

		String encodedPassword = passwordEncoder.encode(requestDto.password());
		User savedStoreOwner = userRepository.save(requestDto.toEntityAsStoreOwner(encodedPassword));

		return SignupResponseDto.fromEntity(savedStoreOwner);
	}


	public LoginResponseDto authenticate(LoginRequestDto requestDto) {
		User findUser = userService.getUserByEmail(requestDto.email());

		if (passwordEncoder.matches(requestDto.password(), findUser.getPassword()) == false) {
			throw new AuthenticationException(INVALID_CREDENTIALS);
		}

		String accessToken = BEARER_PREFIX + tokenProvider.generateToken(findUser, ACCESS_TOKEN_DURATION);
		String refreshToken = BEARER_PREFIX + tokenProvider.generateToken(findUser, REFRESH_TOKEN_DURATION);

		return new LoginResponseDto(accessToken, refreshToken);
	}
}
