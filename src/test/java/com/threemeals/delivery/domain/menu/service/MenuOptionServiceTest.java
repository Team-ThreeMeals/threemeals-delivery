package com.threemeals.delivery.domain.menu.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.util.ReflectionTestUtils.*;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.threemeals.delivery.config.error.ErrorCode;
import com.threemeals.delivery.domain.menu.dto.request.MenuOptionRequestDto;
import com.threemeals.delivery.domain.menu.dto.response.MenuOptionResponseDto;
import com.threemeals.delivery.domain.menu.entity.Menu;
import com.threemeals.delivery.domain.menu.entity.MenuOption;
import com.threemeals.delivery.domain.menu.exception.DeletedMenuOptionException;
import com.threemeals.delivery.domain.menu.repository.MenuOptionRepository;
import com.threemeals.delivery.domain.user.entity.User;

@ExtendWith(MockitoExtension.class)
class MenuOptionServiceTest {

	@Mock
	MenuService menuService;

	@Mock
	MenuOptionRepository menuOptionRepository;

	@InjectMocks
	MenuOptionService menuOptionService;

	@Test
	void 메뉴_옵션을_성공적으로_추가한다() {
		// given
		Menu mockMenu = makeMockMenu();
		User mockOwner = makeMockOwner();

		MenuOptionRequestDto requestDto = makeRequestDto();
		MenuOption mockMenuOption = makeMockMenuOption(mockMenu, requestDto);

		MenuOptionResponseDto expectedResponse = MenuOptionResponseDto.fromEntity(mockMenuOption);

		when(menuService.getMenuById(anyLong())).thenReturn(mockMenu);
		doNothing().when(menuService).validateMenuBelongsToOwner(anyLong(), anyLong());
		when(menuOptionRepository.save(any(MenuOption.class))).thenReturn(mockMenuOption);

		// when
		MenuOptionResponseDto actualResponse = menuOptionService.addMenuOption(mockOwner.getId(), mockMenu.getId(),
			requestDto);

		// then
		assertThat(actualResponse)
			.usingRecursiveComparison()
			.isEqualTo(expectedResponse);
	}

	@Test
	void 메뉴_옵션_변경에_성공한다() {
		// given
		MenuOptionRequestDto requestDto = makeRequestDto();
		Menu mockMenu = makeMockMenu();
		MenuOption mockMenuOption = makeMockMenuOption(mockMenu, requestDto);

		MenuOptionResponseDto expectedResponse = MenuOptionResponseDto.fromEntity(mockMenuOption);

		when(menuOptionRepository.findById(anyLong())).thenReturn(Optional.of(mockMenuOption));
		doNothing().when(menuService).validateMenuBelongsToOwner(anyLong(), anyLong());

		// when
		MenuOptionResponseDto actualResponse = menuOptionService.updateMenuOption(1L, mockMenuOption.getId(),
			requestDto);

		// then
		assertThat(actualResponse)
			.usingRecursiveComparison()
			.isEqualTo(expectedResponse);
	}

	@Test
	void 삭제된_메뉴_옵션을_조회할_시_예외가_발생한다() {
		// given
		MenuOption mockMenuOption = MenuOption.builder().build();
		setField(mockMenuOption, "isDeleted", true);

		when(menuOptionRepository.findById(anyLong())).thenReturn(Optional.of(mockMenuOption));

		// when & then
		assertThatThrownBy(() -> menuOptionService.getMenuOptionById(anyLong()))
			.isInstanceOf(DeletedMenuOptionException.class)
			.hasMessage(ErrorCode.MENU_OPTION_DELETED.getMessage());
	}

	@Test
	void 메뉴_옵션_삭제에_성공한다() {
		// given
		MenuOptionRequestDto requestDto = makeRequestDto();
		Menu mockMenu = makeMockMenu();
		MenuOption mockMenuOption = makeMockMenuOption(mockMenu, requestDto);

		doNothing().when(menuService).validateMenuBelongsToOwner(anyLong(), anyLong());
		when(menuOptionRepository.findById(anyLong())).thenReturn(Optional.ofNullable(mockMenuOption));

		// when
		menuOptionService.deleteMenuOption(anyLong(), mockMenuOption.getId());

		// then
		assertThat(mockMenuOption.getIsDeleted()).isTrue();
	}

	public MenuOptionRequestDto makeRequestDto() {
		return new MenuOptionRequestDto(
			"menuOptionName",
			"description",
			4500,
			"menuOptionImgUrl"
		);

	}

	public Menu makeMockMenu() {
		Menu mockMenu = Menu.builder()
			.build();
		setField(mockMenu, "id", 1L);
		return mockMenu;
	}

	public MenuOption makeMockMenuOption(Menu mockMenu, MenuOptionRequestDto requestDto) {

		MenuOption mockMenuOption = MenuOption.builder()
			.menuOptionName(requestDto.menuOptionName())
			.menuOptionPrice(requestDto.menuOptionPrice())
			.menuOptionImgUrl(requestDto.menuOptionImgUrl())
			.description(requestDto.description())
			.build();

		setField(mockMenuOption, "id", 1L);
		setField(mockMenuOption, "isDeleted", false);

		return mockMenuOption;
	}

	public User makeMockOwner() {
		User mockOwner = User.builder().build();
		setField(mockOwner, "id", 1L);
		return mockOwner;
	}

}