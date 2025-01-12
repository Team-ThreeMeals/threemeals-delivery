package com.threemeals.delivery.domain.auth;

import static com.threemeals.delivery.config.error.ErrorCode.*;
import static com.threemeals.delivery.config.util.Token.*;
import static org.assertj.core.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.Duration;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.threemeals.delivery.config.PasswordEncoder;
import com.threemeals.delivery.config.jwt.TokenProvider;
import com.threemeals.delivery.domain.auth.dto.request.LoginRequestDto;
import com.threemeals.delivery.domain.auth.dto.request.SignupRequestDto;
import com.threemeals.delivery.domain.auth.dto.request.UpdateTokenRequestDto;
import com.threemeals.delivery.domain.auth.dto.response.SignupResponseDto;
import com.threemeals.delivery.domain.user.entity.Role;
import com.threemeals.delivery.domain.user.entity.User;
import com.threemeals.delivery.domain.user.repository.UserRepository;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class AuthIntegrationTest {

	@Autowired
	MockMvc mockMvc;

	@Autowired
	ObjectMapper objectMapper;

	@Autowired
	UserRepository userRepository;

	@Autowired
	PasswordEncoder passwordEncoder;

	@Autowired
	TokenProvider tokenProvider;

	@BeforeEach
	void setup() {
		userRepository.deleteAllInBatch();
	}

	SignupRequestDto makeSignupRequestDto() {
		return new SignupRequestDto(
			"user@email.com",
			"user",
			"Password1!",
			"https://3meals.com",
			"사랑시 고백구 행복동"
		);
	}

	User makeMockUser() {
		return User.builder()
			.username("user")
			.password(passwordEncoder.encode("Password1!"))
			.email("user@email.com")
			.profileImgUrl("https://static.3meals.com/asd123asdaz.jpg")
			.address("사랑시 고백구 행복동")
			.role(Role.USER)
			.build();
	}

	@Test
	void 유저_회원가입에_성공한다() throws Exception {
		// given
		String requestBody = objectMapper.writeValueAsString(makeSignupRequestDto());

		// when
		ResultActions result = mockMvc.perform(post("/signup")
			.contentType(MediaType.APPLICATION_JSON)
			.content(requestBody)
			.accept(MediaType.APPLICATION_JSON)
		);

		User findUser = userRepository.findAll().get(0);

		String contentAsString = result.andReturn().getResponse().getContentAsString();
		SignupResponseDto actualResponse = objectMapper.readValue(contentAsString, SignupResponseDto.class);

		// then
		result.andExpect(status().isCreated())
			.andExpect(content().contentType(MediaType.APPLICATION_JSON))
			.andDo(print());

		assertThat(actualResponse.id()).isEqualTo(findUser.getId());
		assertThat(actualResponse.address()).isEqualTo(findUser.getAddress());
		assertThat(actualResponse.email()).isEqualTo(findUser.getEmail());
		assertThat(actualResponse.username()).isEqualTo(findUser.getUsername());
	}

	@Test
	void 사장님_회원가입에_성공한다() throws Exception {
	    // given
		String requestBody = objectMapper.writeValueAsString(makeSignupRequestDto());

		// when
		ResultActions result = mockMvc.perform(post("/signup/store-owner")
			.contentType(MediaType.APPLICATION_JSON)
			.content(requestBody)
			.accept(MediaType.APPLICATION_JSON)
		);

		User findUser = userRepository.findAll().get(0);

		String contentAsString = result.andReturn().getResponse().getContentAsString();
		SignupResponseDto actualResponse = objectMapper.readValue(contentAsString, SignupResponseDto.class);

		// then
		result.andExpect(status().isCreated())
			.andExpect(content().contentType(MediaType.APPLICATION_JSON))
			.andDo(print());

		assertThat(findUser.getRole()).isEqualTo(Role.STORE_OWNER);
		assertThat(actualResponse.id()).isEqualTo(findUser.getId());
		assertThat(actualResponse.address()).isEqualTo(findUser.getAddress());
		assertThat(actualResponse.email()).isEqualTo(findUser.getEmail());
		assertThat(actualResponse.username()).isEqualTo(findUser.getUsername());
	}

	@Test
	void 이메일이_중복되면_회원가입에_실패한다() throws Exception {
	    // given
		String requestBody = objectMapper.writeValueAsString(makeSignupRequestDto());

		User savedUser = userRepository.save(makeMockUser());

		// when
		ResultActions result = mockMvc.perform(post("/signup")
			.contentType(MediaType.APPLICATION_JSON)
			.content(requestBody)
			.accept(MediaType.APPLICATION_JSON)
		);

		// then
		result.andExpect(status().isConflict())
			.andExpect(content().contentType(MediaType.APPLICATION_JSON))
			.andExpect(jsonPath("$.message").value(USER_ALREADY_EXISTS.getMessage()))
			.andExpect(jsonPath("$.code").value(USER_ALREADY_EXISTS.getCode()))
			.andDo(print());
	}

	@Test
	void 로그인에_성공한다() throws Exception {
	    // given
		User savedUser = userRepository.save(makeMockUser());

		LoginRequestDto requestDto = new LoginRequestDto(savedUser.getEmail(), "Password1!");
		String requestBody = objectMapper.writeValueAsString(requestDto);

		// when
		ResultActions result = mockMvc.perform(post("/login")
			.contentType(MediaType.APPLICATION_JSON)
			.content(requestBody)
			.accept(MediaType.APPLICATION_JSON)
		);

		// then
		result.andExpect(status().isOk())
			.andExpect(content().contentType(MediaType.APPLICATION_JSON))
			.andExpect(jsonPath("$.accessToken").exists())
			.andExpect(jsonPath("$.refreshToken").exists())
			.andDo(print());
	}
	
	@Test
	void accessToken_재발급에_성공한다() throws Exception {
	    // given
		User savedUser = userRepository.save(makeMockUser());
		String refreshToken = tokenProvider.generateToken(savedUser, REFRESH_TOKEN_TYPE, REFRESH_TOKEN_DURATION);
		UpdateTokenRequestDto requestDto = new UpdateTokenRequestDto(refreshToken);
		String requestBody = objectMapper.writeValueAsString(requestDto);

		// when
		ResultActions result = mockMvc.perform(post("/refresh-token")
			.contentType(MediaType.APPLICATION_JSON)
			.content(requestBody)
			.accept(MediaType.APPLICATION_JSON)
		);

		// then
		result.andExpect(status().isOk())
			.andExpect(content().contentType(MediaType.APPLICATION_JSON))
			.andExpect(jsonPath("$.accessToken").exists())
			.andDo(print());
	}

	@Test
	void accessToken_재발급에_실패한다() throws Exception {
		// given
		Duration duration = Duration.ofNanos(-1);
		User savedUser = userRepository.save(makeMockUser());
		String refreshToken = tokenProvider.generateToken(savedUser, REFRESH_TOKEN_TYPE, duration);
		UpdateTokenRequestDto requestDto = new UpdateTokenRequestDto(refreshToken);
		String requestBody = objectMapper.writeValueAsString(requestDto);

		// when
		ResultActions result = mockMvc.perform(post("/refresh-token")
			.contentType(MediaType.APPLICATION_JSON)
			.content(requestBody)
			.accept(MediaType.APPLICATION_JSON)
		);

		// then
		result.andExpect(status().isUnauthorized())
			.andExpect(content().contentType(MediaType.APPLICATION_JSON))
			.andExpect(jsonPath("$.message").value(TOKEN_EXPIRED.getMessage()))
			.andExpect(jsonPath("$.code").value(TOKEN_EXPIRED.getCode()))
			.andDo(print());
	}
}
