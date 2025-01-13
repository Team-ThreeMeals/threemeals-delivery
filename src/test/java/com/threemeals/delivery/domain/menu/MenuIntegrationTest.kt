package com.threemeals.delivery.domain.menu

import com.fasterxml.jackson.databind.ObjectMapper
import com.threemeals.delivery.config.error.ErrorCode.ACCESS_DENIED
import com.threemeals.delivery.config.jwt.TokenProvider
import com.threemeals.delivery.config.util.Token.*
import com.threemeals.delivery.domain.menu.dto.request.MenuRequestDto
import com.threemeals.delivery.domain.menu.dto.response.MenuResponseDto
import com.threemeals.delivery.domain.menu.entity.Category
import com.threemeals.delivery.domain.menu.entity.Menu
import com.threemeals.delivery.domain.menu.repository.MenuRepository
import com.threemeals.delivery.domain.store.entity.Store
import com.threemeals.delivery.domain.store.repository.StoreRepository
import com.threemeals.delivery.domain.user.entity.Role
import com.threemeals.delivery.domain.user.entity.User
import com.threemeals.delivery.domain.user.repository.UserRepository
import jakarta.persistence.EntityManager
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultHandlers.print
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import org.springframework.transaction.annotation.Transactional
import java.time.LocalTime
import java.util.*

@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class MenuIntegrationTest {

    @Autowired
    lateinit var mockMvc: MockMvc

    @Autowired
    lateinit var objectMapper: ObjectMapper

    @Autowired
    lateinit var menuRepository: MenuRepository

    @Autowired
    lateinit var userRepository: UserRepository

    @Autowired
    lateinit var storeRepository: StoreRepository

    @Autowired
    lateinit var tokenProvider: TokenProvider

    @BeforeEach
    fun setup() {
        menuRepository.deleteAllInBatch()
        userRepository.deleteAllInBatch()
        storeRepository.deleteAllInBatch()
    }

    fun makeMockOwner(): User {
        return userRepository.save(
            User.builder()
                .username("user")
                .email("user@email.com")
                .password("Password1!")
                .address("사랑시 고백구 행복동")
                .profileImgUrl("https://~~")
                .role(Role.STORE_OWNER)
                .build()
        )
    }

    fun makeMockStore(owner: User): Store {
        return storeRepository.save(
            Store.builder()
                .owner(owner)
                .storeName("storeName")
                .storeProfileImgUrl("https://~~")
                .address("address")
                .openingTime(LocalTime.now())
                .closingTime(LocalTime.now())
                .deliveryTip(5000)
                .minOrderPrice(15000)
                .build()
        )
    }

    fun makeMockMenu(store: Store): Menu {
        val mockMenu = Menu.builder()
            .category(Category.PIZZA)
            .menuName("menuName")
            .description("description")
            .price(15000)
            .menuImgUrl("https:~~")
            .build()

        mockMenu.store = store
        return mockMenu
    }

    fun makeAccessToken(owner: User): String {
        return tokenProvider.generateToken(owner, ACCESS_TOKEN_TYPE, ACCESS_TOKEN_DURATION)
    }

    fun makeMenuRequestDto(): MenuRequestDto {
        return MenuRequestDto(
            "KOREAN",
            "menuName",
            "description",
            15000,
            "https://~~"
        )
    }


    @Test
    fun `메뉴 추가에 성공한다`() {
        // given
        val content = objectMapper.writeValueAsString(makeMenuRequestDto())

        val savedOwner = userRepository.save(makeMockOwner())
        val savedStore = storeRepository.save(makeMockStore(savedOwner))

        // when
        val result = mockMvc.perform(
            post("/menus")
                .param("storeId", savedStore.id.toString())
                .header(AUTHORIZATION_HEADER, BEARER_PREFIX + makeAccessToken(savedOwner))
                .contentType(MediaType.APPLICATION_JSON)
                .content(content)
                .accept(MediaType.APPLICATION_JSON)
        )

        val contentAsString = result.andReturn().response.contentAsString
        val actualResponse = objectMapper.readValue(contentAsString, MenuResponseDto::class.java)
        val findMenu = menuRepository.findAll().get(0)

        // then
        result.andExpect(status().isCreated)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.menuId").value(findMenu.id))
            .andExpect(jsonPath("$.menuName").value(actualResponse.menuName))
            .andExpect(jsonPath("$.description").value(actualResponse.description))
            .andExpect(jsonPath("$.price").value(actualResponse.price))
            .andExpect(jsonPath("$.menuImgUrl").value(actualResponse.menuImgUrl))
            .andDo(print())
    }

    @Test
    fun `메뉴 업데이트에 성공한다`() {
        // given
        val requestDto = makeMenuRequestDto()
        val content = objectMapper.writeValueAsString(requestDto)

        val savedOwner = userRepository.save(makeMockOwner())
        val savedStore = storeRepository.save(makeMockStore(savedOwner))
        val savedMenu = menuRepository.save(makeMockMenu(savedStore))

        // when
        val result = mockMvc.perform(
            put("/menus/{menuId}", savedMenu.id)
                .header(AUTHORIZATION_HEADER, BEARER_PREFIX + makeAccessToken(savedOwner))
                .contentType(MediaType.APPLICATION_JSON)
                .content(content)
                .accept(MediaType.APPLICATION_JSON)
        )

        val contentAsString = result.andReturn().response.contentAsString
        val actualResponse = objectMapper.readValue(contentAsString, MenuResponseDto::class.java)
        val findMenu = menuRepository.findAll().get(0)

        // then
        result.andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.menuId").value(findMenu.id))
            .andExpect(jsonPath("$.menuName").value(actualResponse.menuName))
            .andExpect(jsonPath("$.description").value(actualResponse.description))
            .andExpect(jsonPath("$.price").value(actualResponse.price))
            .andExpect(jsonPath("$.menuImgUrl").value(actualResponse.menuImgUrl))
            .andDo(print())
    }

    @Test
    fun `권한이 없으면 메뉴 접근에 실패한다`() {
        // given
        val requestDto = makeMenuRequestDto()
        val content = objectMapper.writeValueAsString(requestDto)

        val firstOwner = userRepository.save(makeMockOwner())
        val firstOwnerStore = storeRepository.save(makeMockStore(firstOwner))
        val firstOwnerSavedMenu = menuRepository.save(makeMockMenu(firstOwnerStore))

        val secondOwner = userRepository.save(makeMockOwner())
        val secondOwnerStore = storeRepository.save(makeMockStore(firstOwner))

        // when
        val result = mockMvc.perform(
            put("/menus/{menuId}", firstOwnerSavedMenu.id)
                .header(AUTHORIZATION_HEADER, BEARER_PREFIX + makeAccessToken(secondOwner))
                .contentType(MediaType.APPLICATION_JSON)
                .content(content)
                .accept(MediaType.APPLICATION_JSON)
        )

        // then
        result.andExpect(status().isForbidden)
            .andExpect(jsonPath("$.message").value(ACCESS_DENIED.message))
            .andExpect(jsonPath("$.code").value(ACCESS_DENIED.code))

    }

    @Test
    fun `메뉴 삭제에 성공한다`() {
        // given
        val savedOwner = userRepository.save(makeMockOwner())
        val savedStore = storeRepository.save(makeMockStore(savedOwner))
        val savedMenu = menuRepository.save(makeMockMenu(savedStore))

        // when
        val result = mockMvc.perform(
            delete("/menus/{menuId}", savedMenu.id)
                .header(AUTHORIZATION_HEADER, BEARER_PREFIX + makeAccessToken(savedOwner))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
        )

        // then
        result.andExpect(status().isOk)
    }


}