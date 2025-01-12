package com.threemeals.delivery.domain.auth

import com.fasterxml.jackson.databind.ObjectMapper
import com.threemeals.delivery.config.PasswordEncoder
import com.threemeals.delivery.config.error.ErrorCode.TOKEN_EXPIRED
import com.threemeals.delivery.config.error.ErrorCode.USER_ALREADY_EXISTS
import com.threemeals.delivery.config.jwt.TokenProvider
import com.threemeals.delivery.config.util.Token.REFRESH_TOKEN_DURATION
import com.threemeals.delivery.config.util.Token.REFRESH_TOKEN_TYPE
import com.threemeals.delivery.domain.auth.dto.request.LoginRequestDto
import com.threemeals.delivery.domain.auth.dto.request.SignupRequestDto
import com.threemeals.delivery.domain.auth.dto.request.UpdateTokenRequestDto
import com.threemeals.delivery.domain.auth.dto.response.SignupResponseDto
import com.threemeals.delivery.domain.user.entity.Role
import com.threemeals.delivery.domain.user.entity.User
import com.threemeals.delivery.domain.user.repository.UserRepository
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.ResultActions
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultHandlers.print
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import org.springframework.transaction.annotation.Transactional
import java.time.Duration

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class AuthIntegrationTest {

    @Autowired
    lateinit var mockMvc: MockMvc

    @Autowired
    lateinit var objectMapper: ObjectMapper

    @Autowired
    lateinit var userRepository: UserRepository

    @Autowired
    lateinit var passwordEncoder: PasswordEncoder

    @Autowired
    lateinit var tokenProvider: TokenProvider

    @BeforeEach
    fun setup() {
        userRepository.deleteAllInBatch()
    }

    private fun makeSignupRequestDto(): SignupRequestDto {
        return SignupRequestDto(
            "user@email.com",
            "user",
            "Password1!",
            "https://3meals.com",
            "사랑시 고백구 행복동"
        )
    }

    private fun makeMockUser(): User {
        return User.builder()
            .username("user")
            .password(passwordEncoder.encode("Password1!"))
            .email("user@email.com")
            .profileImgUrl("https://static.3meals.com/asd123asdaz.jpg")
            .address("사랑시 고백구 행복동")
            .role(Role.USER)
            .build()
    }

    @Test
    fun `유저 회원가입에 성공한다`() {
        // given
        val requestBody = objectMapper.writeValueAsString(makeSignupRequestDto())

        // when
        val result: ResultActions = mockMvc.perform(
            post("/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody)
                .accept(MediaType.APPLICATION_JSON)
        )

        val findUser = userRepository.findAll()[0]
        val contentAsString = result.andReturn().response.contentAsString
        val actualResponse = objectMapper.readValue(contentAsString, SignupResponseDto::class.java)

        // then
        result.andExpect(status().isCreated)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andDo(print())

        assertThat(actualResponse.id).isEqualTo(findUser.id)
        assertThat(actualResponse.address).isEqualTo(findUser.address)
        assertThat(actualResponse.email).isEqualTo(findUser.email)
        assertThat(actualResponse.username).isEqualTo(findUser.username)
    }

    @Test
    fun `사장님 회원가입에 성공한다`() {
        // given
        val requestBody = objectMapper.writeValueAsString(makeSignupRequestDto())

        // when
        val result: ResultActions = mockMvc.perform(
            post("/signup/store-owner")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody)
                .accept(MediaType.APPLICATION_JSON)
        )

        val findUser = userRepository.findAll()[0]
        val contentAsString = result.andReturn().response.contentAsString
        val actualResponse = objectMapper.readValue(contentAsString, SignupResponseDto::class.java)

        // then
        result.andExpect(status().isCreated)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andDo(print())

        assertThat(findUser.role).isEqualTo(Role.STORE_OWNER)
        assertThat(actualResponse.id).isEqualTo(findUser.id)
        assertThat(actualResponse.address).isEqualTo(findUser.address)
        assertThat(actualResponse.email).isEqualTo(findUser.email)
        assertThat(actualResponse.username).isEqualTo(findUser.username)
    }

    @Test
    fun `이메일이 중복되면 회원가입에 실패한다`() {
        // given
        val requestBody = objectMapper.writeValueAsString(makeSignupRequestDto())
        userRepository.save(makeMockUser())

        // when
        val result: ResultActions = mockMvc.perform(
            post("/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody)
                .accept(MediaType.APPLICATION_JSON)
        )

        // then
        result.andExpect(status().isConflict)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.message").value(USER_ALREADY_EXISTS.message))
            .andExpect(jsonPath("$.code").value(USER_ALREADY_EXISTS.code))
            .andDo(print())
    }

    @Test
    fun `로그인에 성공한다`() {
        // given
        val savedUser = userRepository.save(makeMockUser())
        val requestDto = LoginRequestDto(savedUser.email, "Password1!")
        val requestBody = objectMapper.writeValueAsString(requestDto)

        // when
        val result: ResultActions = mockMvc.perform(
            post("/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody)
                .accept(MediaType.APPLICATION_JSON)
        )

        // then
        result.andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.accessToken").exists())
            .andExpect(jsonPath("$.refreshToken").exists())
            .andDo(print())
    }

    @Test
    fun `accessToken 재발급에 성공한다`() {
        // given
        val savedUser = userRepository.save(makeMockUser())
        val refreshToken = tokenProvider.generateToken(savedUser, REFRESH_TOKEN_TYPE, REFRESH_TOKEN_DURATION)
        val requestDto = UpdateTokenRequestDto(refreshToken)
        val requestBody = objectMapper.writeValueAsString(requestDto)

        // when
        val result: ResultActions = mockMvc.perform(
            post("/refresh-token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody)
                .accept(MediaType.APPLICATION_JSON)
        )

        // then
        result.andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.accessToken").exists())
            .andDo(print())
    }

    @Test
    fun `accessToken 재발급에 실패한다`() {
        // given
        val duration = Duration.ofNanos(-1)
        val savedUser = userRepository.save(makeMockUser())
        val refreshToken = tokenProvider.generateToken(savedUser, REFRESH_TOKEN_TYPE, duration)
        val requestDto = UpdateTokenRequestDto(refreshToken)
        val requestBody = objectMapper.writeValueAsString(requestDto)

        // when
        val result: ResultActions = mockMvc.perform(
            post("/refresh-token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody)
                .accept(MediaType.APPLICATION_JSON)
        )

        // then
        result.andExpect(status().isUnauthorized)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.message").value(TOKEN_EXPIRED.message))
            .andExpect(jsonPath("$.code").value(TOKEN_EXPIRED.code))
            .andDo(print())
    }
}