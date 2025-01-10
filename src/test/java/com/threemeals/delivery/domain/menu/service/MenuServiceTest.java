package com.threemeals.delivery.domain.menu.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.util.ReflectionTestUtils.*;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.threemeals.delivery.config.error.ErrorCode;
import com.threemeals.delivery.domain.menu.dto.request.MenuRequestDto;
import com.threemeals.delivery.domain.menu.dto.response.MenuResponseDto;
import com.threemeals.delivery.domain.menu.entity.Category;
import com.threemeals.delivery.domain.menu.entity.Menu;
import com.threemeals.delivery.domain.menu.exception.DeletedMenuException;
import com.threemeals.delivery.domain.menu.repository.MenuRepository;
import com.threemeals.delivery.domain.store.entity.Store;
import com.threemeals.delivery.domain.store.service.StoreService;
import com.threemeals.delivery.domain.user.entity.User;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@ExtendWith(MockitoExtension.class)
class MenuServiceTest {

	@Mock
	MenuRepository menuRepository;

	@Mock
	StoreService storeService;

	@InjectMocks
	MenuService menuService;

	@Test
	void 메뉴를_성공적으로_추가() {
		// given
		MenuRequestDto requestDto = makeMenuRequestDto();
		User mockOwner = makeMockOwner();
		Store mockStore = makeMockStore(mockOwner);
		Menu mockMenu = makeMockMenu(requestDto);
		MenuResponseDto expectedResponse = MenuResponseDto.fromEntity(mockMenu);

		when(storeService.getStoreById(anyLong())).thenReturn(mockStore);
		when(menuRepository.save(any(Menu.class))).thenReturn(mockMenu);

		// when
		MenuResponseDto actualResponse = menuService.addMenu(mockStore.getId(), mockOwner.getId(), requestDto);

		// then
		assertThat(actualResponse)
			.usingRecursiveComparison()
			.isEqualTo(expectedResponse);
	}

	@Test
	void 메뉴_업데이트_성공() {
		// given
		MenuRequestDto requestDto = makeMenuRequestDto();
		User mockOwner = makeMockOwner();
		Menu mockMenu = makeMockMenu(requestDto);
		MenuResponseDto expectedResponse = MenuResponseDto.fromEntity(mockMenu);

		when(menuRepository.findById(anyLong())).thenReturn(Optional.of(mockMenu));
		when(menuRepository.existsByMenuIdAndOwnerId(anyLong(), anyLong())).thenReturn(true);

		// when
		MenuResponseDto actualResponse = menuService.updateMenu(mockOwner.getId(), mockMenu.getId(), requestDto);

		// then
		assertThat(actualResponse)
			.usingRecursiveComparison()
			.isEqualTo(expectedResponse);
	}

	@Test
	void 메뉴_삭제_성공() {
		// given
		MenuRequestDto requestDto = makeMenuRequestDto();
		Menu mockMenu = makeMockMenu(requestDto);
		User mockOwner = makeMockOwner();

		when(menuRepository.findById(anyLong())).thenReturn(Optional.of(mockMenu));
		when(menuRepository.existsByMenuIdAndOwnerId(anyLong(), anyLong())).thenReturn(true);

		// when
		menuService.deleteMenu(mockMenu.getId(), mockOwner.getId());

		// then
		assertThat(mockMenu.getIsDeleted()).isTrue();
	}

	@Test
	void 삭제된_메뉴를_조회할_시_예외가_발생한다() {
	    // given
		MenuRequestDto requestDto = makeMenuRequestDto();
		Menu mockMenu = makeMockMenu(requestDto);
		setField(mockMenu, "isDeleted", true);

		when(menuRepository.findById(anyLong())).thenReturn(Optional.of(mockMenu));

		// when & then
		assertThatThrownBy(() -> menuService.getMenuById(anyLong()))
			.isInstanceOf(DeletedMenuException.class)
			.hasMessage(ErrorCode.MENU_DELETED.getMessage());
	}

	private Menu makeMockMenu(MenuRequestDto requestDto) {
		Menu mockMenu = Menu.builder()
			.menuName(requestDto.menuName())
			.category(Category.of(requestDto.category()))
			.menuImgUrl(requestDto.menuImgUrl())
			.price(requestDto.price())
			.description(requestDto.description())
			.build();
		setField(mockMenu, "id", 1L);
		return mockMenu;
	}

	private Store makeMockStore(User mockOwner) {
		Store mockStore = Store.builder().build();
		setField(mockStore, "id", 1L);
		setField(mockStore, "owner", mockOwner);
		return mockStore;
	}

	private User makeMockOwner() {
		User mockOwner = User.builder().build();
		setField(mockOwner, "id", 1L);
		return mockOwner;
	}

	private MenuRequestDto makeMenuRequestDto() {
		MenuRequestDto requestDto = new MenuRequestDto(
			"KOREAN",
			"불닭좋아",
			"삼양 짱짱",
			12000,
			"https:~~~"
		);
		return requestDto;
	}
}