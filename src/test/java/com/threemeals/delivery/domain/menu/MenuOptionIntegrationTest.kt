package com.threemeals.delivery.domain.menu

import com.fasterxml.jackson.databind.ObjectMapper
import com.threemeals.delivery.config.error.ErrorCode.*
import com.threemeals.delivery.config.jwt.TokenProvider
import com.threemeals.delivery.config.util.Token.*
import com.threemeals.delivery.domain.common.exception.AccessDeniedException
import com.threemeals.delivery.domain.menu.dto.request.MenuOptionRequestDto
import com.threemeals.delivery.domain.menu.entity.Category
import com.threemeals.delivery.domain.menu.entity.Menu
import com.threemeals.delivery.domain.menu.entity.MenuOption
import com.threemeals.delivery.domain.menu.exception.DeletedMenuOptionException
import com.threemeals.delivery.domain.menu.repository.MenuOptionRepository
import com.threemeals.delivery.domain.menu.repository.MenuRepository
import com.threemeals.delivery.domain.store.entity.Store
import com.threemeals.delivery.domain.store.repository.StoreRepository
import com.threemeals.delivery.domain.user.entity.Role
import com.threemeals.delivery.domain.user.entity.User
import com.threemeals.delivery.domain.user.repository.UserRepository
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.transaction.annotation.Transactional
import java.time.LocalTime
import kotlin.math.log
import kotlin.test.Test

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class MenuOptionIntegrationTest {

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
    lateinit var menuOptionRepository: MenuOptionRepository

    @Autowired
    lateinit var tokenProvider: TokenProvider

    @BeforeEach
    fun setup() {
        menuRepository.deleteAllInBatch()
        userRepository.deleteAllInBatch()
        storeRepository.deleteAllInBatch()
    }

    fun makeOwner(role: Role): User {
        return userRepository.save(
            User.builder()
                .username("user")
                .email("user@email.com")
                .password("Password1!")
                .address("사랑시 고백구 행복동")
                .profileImgUrl("https://~~")
                .role(role)
                .build()
        )
    }

    fun makeStore(owner: User): Store {
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

    fun makeMenu(store: Store): Menu {
        val mockMenu = Menu.builder()
            .category(Category.PIZZA)
            .menuName("menuName")
            .description("description")
            .price(15000)
            .menuImgUrl("https:~~")
            .build()

        mockMenu.store = store
        return menuRepository.save(mockMenu)
    }

    fun makeMenuOption(menu: Menu): MenuOption {
        val mockMenuOption = MenuOption.builder()
            .menuOptionName("originMenuOptionName")
            .description("originDescription")
            .menuOptionPrice(4800)
            .menuOptionImgUrl("https://~")
            .build()

        mockMenuOption.menu = menu
        return menuOptionRepository.save(mockMenuOption)
    }

    fun makeMenuOptionRequestDto(): MenuOptionRequestDto {
        return MenuOptionRequestDto(
            "menuOptionName",
            "description",
            4900,
            "https://~~"
        )
    }

    @Test
    fun `메뉴 옵션 추가에 성공한다`() {
        // given
        val requestDto = makeMenuOptionRequestDto()
        val content = objectMapper.writeValueAsString(requestDto)

        val mockOwner = makeOwner(Role.STORE_OWNER)
        val mockStore = makeStore(mockOwner)
        val mockMenu = makeMenu(mockStore)

        // when
        val result = mockMvc.perform(
            post("/menus/{menuId}/menuoptions", mockMenu.id)
                .header(
                    AUTHORIZATION_HEADER,
                    BEARER_PREFIX + tokenProvider.generateToken(mockOwner, ACCESS_TOKEN_TYPE, ACCESS_TOKEN_DURATION)
                )
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(content)
        )

        val findMenuOption = menuOptionRepository.findAll().get(0)

        // then
        result.andExpect(status().isCreated)
            .andExpect(jsonPath("$.menuOptionId").value(findMenuOption.id))
            .andExpect(jsonPath("$.menuOptionName").value(requestDto.menuOptionName))
            .andExpect(jsonPath("$.description").value(requestDto.description))
            .andExpect(jsonPath("$.menuOptionPrice").value(requestDto.menuOptionPrice))
            .andExpect(jsonPath("$.menuOptionImgUrl").value(requestDto.menuOptionImgUrl))
    }

    @Test
    fun `메뉴 옵션 변경에 성공한다`() {
        // given
        val mockOwner = makeOwner(role = Role.STORE_OWNER)
        val mockStore = makeStore(mockOwner)
        val mockMenu = makeMenu(mockStore)
        val mockMenuOption = makeMenuOption(mockMenu)

        val requestDto = makeMenuOptionRequestDto()
        val content = objectMapper.writeValueAsString(requestDto)

        // when
        val result = mockMvc.perform(
            put("/menus/menuoptions/{menuOptionId}", mockMenuOption.id)
                .header(
                    AUTHORIZATION_HEADER,
                    BEARER_PREFIX + tokenProvider.generateToken(mockOwner, ACCESS_TOKEN_TYPE, ACCESS_TOKEN_DURATION)
                )
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(content)
        )

        val findMenuOption = menuOptionRepository.findAll().get(0)

        // then
        result.andExpect(status().isOk)
            .andExpect(jsonPath("$.menuOptionId").value(findMenuOption.id))
            .andExpect(jsonPath("$.menuOptionName").value(requestDto.menuOptionName))
            .andExpect(jsonPath("$.description").value(requestDto.description))
            .andExpect(jsonPath("$.menuOptionPrice").value(requestDto.menuOptionPrice))
            .andExpect(jsonPath("$.menuOptionImgUrl").value(requestDto.menuOptionImgUrl))
    }

    @Test
    fun `메뉴 옵션 삭제에 성공한다`() {
        // given
        val mockOwner = makeOwner(role = Role.STORE_OWNER)
        val mockStore = makeStore(mockOwner)
        val mockMenu = makeMenu(mockStore)
        val mockMenuOption = makeMenuOption(mockMenu)

        // when
        val result = mockMvc.perform(
            delete("/menus/menuoptions/{menuOptionId}", mockMenuOption.id)
                .header(
                    AUTHORIZATION_HEADER,
                    BEARER_PREFIX + tokenProvider.generateToken(mockOwner, ACCESS_TOKEN_TYPE, ACCESS_TOKEN_DURATION)
                )
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
        )

        val deletedMenuOption = menuOptionRepository.findAll().get(0)

        // then
        result.andExpect(status().isOk)
        assertThat(deletedMenuOption.isDeleted).isTrue()
    }

    @Test
    fun `메뉴 옵션이 삭제 상태이면 수정에 실패한다`() {
        // given
        val mockOwner = makeOwner(Role.STORE_OWNER)
        val mockStore = makeStore(mockOwner)
        val mockMenu = makeMenu(mockStore)
        val mockMenuOption = makeMenuOption(mockMenu)

        val requestDto = makeMenuOptionRequestDto()
        val content = objectMapper.writeValueAsString(requestDto)

        mockMenuOption.deleteMe()

        // when
        val result = mockMvc.perform(
            put("/menus/menuoptions/{menuOptionId}", mockMenuOption.id)
                .header(
                    AUTHORIZATION_HEADER,
                    BEARER_PREFIX + tokenProvider.generateToken(mockOwner, ACCESS_TOKEN_TYPE, ACCESS_TOKEN_DURATION)
                )
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(content)
        )

        // then
        result.andExpect(status().isForbidden)
            .andExpect(jsonPath("$.message").value(MENU_OPTION_DELETED.message))
            .andExpect(jsonPath("$.code").value(MENU_OPTION_DELETED.code))

        val exception = result.andReturn().resolvedException as? DeletedMenuOptionException
        assertThat(exception).isNotNull
        assertThat(exception?.message).isEqualTo(MENU_OPTION_DELETED.message)
    }

    @Test
    fun `사장님 계정이 아닌 상태에서 메뉴 데이터를 쓰거나 변경하려고 하면 예외가 발생한다`() {
        // given
        val mockUser = makeOwner(Role.USER)
        val mockStore = makeStore(mockUser)
        val mockMenu = makeMenu(mockStore)
        val mockMenuOption = makeMenuOption(mockMenu)

        // when
        val result = mockMvc.perform(
            delete("/menus/menuoptions/{menuOptionId}", mockMenuOption.id)
                .header(
                    AUTHORIZATION_HEADER,
                    BEARER_PREFIX + tokenProvider.generateToken(mockUser, ACCESS_TOKEN_TYPE, ACCESS_TOKEN_DURATION)
                )
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
        )

        // then
        result.andExpect(status().isForbidden)

        val exception = result.andReturn().resolvedException as? AccessDeniedException
        assertThat(exception).isNotNull
        assertThat(exception?.message).isEqualTo(ACCESS_DENIED.message)
    }


}