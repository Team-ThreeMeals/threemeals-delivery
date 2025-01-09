package com.threemeals.delivery.domain.menu.service;

import static com.threemeals.delivery.config.error.ErrorCode.*;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.threemeals.delivery.domain.common.exception.NotFoundException;
import com.threemeals.delivery.domain.menu.dto.request.MenuOptionRequestDto;
import com.threemeals.delivery.domain.menu.dto.response.GetMenuWithOptionsResponseDto;
import com.threemeals.delivery.domain.menu.dto.response.MenuOptionResponseDto;
import com.threemeals.delivery.domain.menu.dto.response.MenuResponseDto;
import com.threemeals.delivery.domain.menu.entity.Menu;
import com.threemeals.delivery.domain.menu.entity.MenuOption;
import com.threemeals.delivery.domain.menu.repository.MenuOptionRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MenuOptionService {

	private final MenuService menuService;
	private final MenuOptionRepository menuOptionRepository;

	// 메인 메뉴 + 메인 옵션 가져오기
	public GetMenuWithOptionsResponseDto getMenuWithOptions(Long menuId) {

		Menu findMenu = menuService.getMenuById(menuId);
		MenuResponseDto menuResponseDto = MenuResponseDto.fromEntity(findMenu);

		List<MenuOptionResponseDto> menuOptionDtos = menuOptionRepository.findByMenu_Id(menuId);

		return new GetMenuWithOptionsResponseDto(menuResponseDto, menuOptionDtos);
	}

	// 메뉴 옵션 추가
	@Transactional
	public MenuOptionResponseDto addMenuOption(Long ownerId, Long menuId, MenuOptionRequestDto requestDto) {

		Menu findMenu = menuService.getMenuById(menuId);
		menuService.validateMenuBelongsToOwner(menuId, ownerId);

		MenuOption newMenuOption = requestDto.toEntity();
		newMenuOption.setMenu(findMenu);

		MenuOption savedMenuOption = menuOptionRepository.save(newMenuOption);
		return MenuOptionResponseDto.fromEntity(savedMenuOption);
	}

	// 메뉴 옵션 수정
	@Transactional
	public MenuOptionResponseDto updateMenuOption(Long ownerId, Long menuOptionId, MenuOptionRequestDto requestDto) {

		MenuOption findMenuOption = getMenuOptionById(menuOptionId);

		menuService.validateMenuBelongsToOwner(findMenuOption.getId(), ownerId);

		findMenuOption.updateMe(requestDto);
		return MenuOptionResponseDto.fromEntity(findMenuOption);
	}

	// 메뉴 옵션 가져오기
	public MenuOption getMenuOptionById(Long menuOptionId) {
		MenuOption findMenuOption = menuOptionRepository.findById(menuOptionId)
			.orElseThrow(() -> new NotFoundException(MENU_OPTION_NOT_FOUND));

		findMenuOption.validateIsDeleted();
		return findMenuOption;
	}

	// 메뉴 옵션 삭제
	@Transactional
	public void deleteMenuOption(Long ownerId, Long menuOptionId) {

		MenuOption findMenuOption = getMenuOptionById(menuOptionId);

		menuService.validateMenuBelongsToOwner(findMenuOption.getId(), ownerId);

		findMenuOption.deleteMe();
	}

}
