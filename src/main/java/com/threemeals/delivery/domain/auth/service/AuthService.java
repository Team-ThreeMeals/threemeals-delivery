package com.threemeals.delivery.domain.auth.service;

import static com.threemeals.delivery.config.error.ErrorCode.*;
import static com.threemeals.delivery.config.util.Token.*;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.threemeals.delivery.config.PasswordEncoder;
import com.threemeals.delivery.config.jwt.TokenProvider;
import com.threemeals.delivery.domain.auth.dto.request.LoginRequestDto;
import com.threemeals.delivery.domain.auth.dto.request.SignupRequestDto;
import com.threemeals.delivery.domain.auth.dto.response.LoginResponseDto;
import com.threemeals.delivery.domain.auth.dto.response.SignupResponseDto;
import com.threemeals.delivery.domain.auth.dto.response.UpdateTokenResponseDto;
import com.threemeals.delivery.domain.auth.exception.AuthenticationException;
import com.threemeals.delivery.domain.auth.exception.InvalidTokenException;
import com.threemeals.delivery.domain.common.exception.InvalidRequestException;
import com.threemeals.delivery.domain.user.entity.Role;
import com.threemeals.delivery.domain.user.entity.User;
import com.threemeals.delivery.domain.user.repository.UserRepository;
import com.threemeals.delivery.domain.user.service.UserService;

import lombok.RequiredArgsConstructor;
@Slf4j
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
		log.info("유저 등급: {}", findUser.getRole());

		if (passwordEncoder.matches(requestDto.password(), findUser.getPassword()) == false) {
			throw new AuthenticationException(INVALID_CREDENTIALS);
		}

		String accessToken = tokenProvider.generateToken(findUser, ACCESS_TOKEN_TYPE, ACCESS_TOKEN_DURATION);
		String refreshToken = tokenProvider.generateToken(findUser, REFRESH_TOKEN_TYPE, REFRESH_TOKEN_DURATION);

		return new LoginResponseDto(accessToken, refreshToken);
	}

	public UpdateTokenResponseDto refreshAccessToken(String refreshToken) {

		tokenProvider.validateToken(refreshToken);
		if (tokenProvider.isRefreshToken(refreshToken) == false) {
			throw new InvalidTokenException();
		}

		Long userId = tokenProvider.getUserId(refreshToken);
		User findUser = userService.getUserById(userId);

		String newAccessToken = tokenProvider.generateToken(findUser, ACCESS_TOKEN_TYPE, ACCESS_TOKEN_DURATION);
		return new UpdateTokenResponseDto(newAccessToken);
	}
}
