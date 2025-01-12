package com.threemeals.delivery.domain.user

import com.fasterxml.jackson.databind.ObjectMapper
import com.threemeals.delivery.config.PasswordEncoder
import com.threemeals.delivery.config.error.ErrorCode.USER_DELETED
import com.threemeals.delivery.config.jwt.TokenProvider
import com.threemeals.delivery.config.util.Token.*
import com.threemeals.delivery.domain.user.dto.request.DeleteUserRequestDto
import com.threemeals.delivery.domain.user.dto.request.UpdateUserRequestDto
import com.threemeals.delivery.domain.user.dto.response.UserResponseDto
import com.threemeals.delivery.domain.user.entity.Role
import com.threemeals.delivery.domain.user.entity.User
import com.threemeals.delivery.domain.user.repository.UserRepository
import jakarta.persistence.EntityManager
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.ResultActions
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put
import org.springframework.test.web.servlet.result.MockMvcResultHandlers.print
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import org.springframework.transaction.annotation.Transactional

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class UserIntegrationTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @Autowired
    private lateinit var userRepository: UserRepository

    @Autowired
    lateinit var tokenProvider: TokenProvider

    @Autowired
    lateinit var passwordEncoder: PasswordEncoder

    @Autowired
    lateinit var entityManager: EntityManager

    @BeforeEach
    fun setup() {
        userRepository.deleteAllInBatch()
    }

    private fun makeMockUser(): User {
        return userRepository.save(
            User.builder()
                .username("user")
                .email("user@email.com")
                .password(passwordEncoder.encode("Password1!"))
                .address("사랑시 고백구 행복동")
                .profileImgUrl("https://~~")
                .role(Role.USER)
                .build()
        )
    }

    @Test
    fun `사용자 회원 정보 변경에 성공한다`() {
        // given
        val mockUser = makeMockUser()

        val requestDto = UpdateUserRequestDto(
            "change user",
            "Password1!",
            "https://~~~",
            "사랑시 고백구 행복동~~"
        )

        val expectedResponse = UserResponseDto(
            mockUser.id,
            requestDto.username,
            mockUser.email,
            requestDto.address,
            requestDto.profileImgUrl
        )

        // when
        val result: ResultActions = mockMvc.perform(
            put("/users")
                .header(AUTHORIZATION_HEADER, BEARER_PREFIX + tokenProvider.generateToken(mockUser, ACCESS_TOKEN_TYPE, ACCESS_TOKEN_DURATION))
                .content(objectMapper.writeValueAsString(requestDto))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
        )

        // then
        result.andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andDo(print())

        val contentAsString = result.andReturn().response.contentAsString
        val actualResult = objectMapper.readValue(contentAsString, UserResponseDto::class.java)

        assertThat(actualResult)
            .usingRecursiveComparison()
            .isInstanceOf(UserResponseDto::class.java)
            .isEqualTo(expectedResponse)
    }

    @Test
    fun `유저 삭제에 성공한다`() {
        // given
        val mockUser = makeMockUser()
        val requestDto = DeleteUserRequestDto("Password1!")

        // when
        val result = mockMvc.perform(
            delete("/users")
                .header(
                    AUTHORIZATION_HEADER,
                    BEARER_PREFIX + tokenProvider.generateToken(mockUser, ACCESS_TOKEN_TYPE, ACCESS_TOKEN_DURATION)
                )
                .content(objectMapper.writeValueAsString(requestDto))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
        )

        // then
        result.andExpect(status().isOk)

        val findUser = userRepository.findAll().get(0)
        assertThat(findUser.isDeleted).isTrue()
    }

    @Test
    fun `계정이 삭제 처리되면 계정 접근에 실패한다`() {
        // given
        val mockUser = makeMockUser()
        mockUser.deleteMe()

        val requestDto = UpdateUserRequestDto(
            "change user",
            "Password1!",
            "https://~~~",
            "사랑시 고백구 행복동~~"
        )

        // when
        val result = mockMvc.perform(
            put("/users")
                .header(
                    AUTHORIZATION_HEADER,
                    BEARER_PREFIX + tokenProvider.generateToken(mockUser, ACCESS_TOKEN_TYPE, ACCESS_TOKEN_DURATION)
                )
                .content(objectMapper.writeValueAsString(requestDto))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
        )

        // then
        result.andExpect(status().isForbidden)
            .andExpect(jsonPath("$.message").value(USER_DELETED.message))
            .andExpect(jsonPath("$.code").value(USER_DELETED.code))
    }
}