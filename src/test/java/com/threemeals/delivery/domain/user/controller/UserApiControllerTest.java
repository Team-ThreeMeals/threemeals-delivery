package com.threemeals.delivery.domain.user.controller;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.hibernate.annotations.processing.Exclude;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.core.MethodParameter;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.threemeals.delivery.config.JpaConfig;
import com.threemeals.delivery.config.jwt.JwtProperties;
import com.threemeals.delivery.config.jwt.TokenProvider;
import com.threemeals.delivery.domain.auth.UserPrincipal;
import com.threemeals.delivery.domain.auth.annotation.Authentication;
import com.threemeals.delivery.domain.user.dto.request.UpdateUserRequestDto;
import com.threemeals.delivery.domain.user.dto.response.UserResponseDto;
import com.threemeals.delivery.domain.user.entity.Role;
import com.threemeals.delivery.domain.user.entity.User;
import com.threemeals.delivery.domain.user.service.UserService;

@Import({TokenProvider.class, JwtProperties.class})
@WebMvcTest(value = UserApiController.class, excludeAutoConfiguration = JpaConfig.class)
@AutoConfigureMockMvc(addFilters = false) // 필터 비활성화
class UserApiControllerTest {

	static final String baseUrl = "/users";

	@MockitoBean
	UserService userService;

	@Autowired
	MockMvc mockMvc;

	@Autowired
	ObjectMapper objectMapper;


	@Test
	void 유저_업데이트_컨트롤러_호출() throws Exception {
		// given
		UpdateUserRequestDto requestDto = new UpdateUserRequestDto(
			"변경된_이름",
			"새로운_비밀번호",
			"새로운_이미지_URL",
			"변경된 주소"
		);
		UserResponseDto expectedResponse = new UserResponseDto(
			1L,
			requestDto.username(),
			"email",
			requestDto.address(),
			requestDto.profileImgUrl()
		);

		Long userId = 1L;
		User existingUser = User.builder()
			.username("강성욱")
			.password("123")
			.role(Role.USER)
			.address("뉴욕특별시")
			.build();

		when(userService.updateUser(anyLong(), any(UpdateUserRequestDto.class))).thenReturn(expectedResponse);

		// when
		ResultActions result = mockMvc.perform(
			put(baseUrl)
				.content(objectMapper.writeValueAsString(requestDto))
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)
		);

		// then
		result.andExpect(status().isOk());

		assertThat(result)
			.isInstanceOf(UserResponseDto.class)
			.usingRecursiveComparison()
			.isEqualTo(expectedResponse);
	}

}