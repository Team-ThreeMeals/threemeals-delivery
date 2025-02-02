package com.threemeals.delivery.domain.menu.service;

import static com.threemeals.delivery.config.error.ErrorCode.*;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.threemeals.delivery.domain.common.exception.AccessDeniedException;
import com.threemeals.delivery.domain.common.exception.NotFoundException;
import com.threemeals.delivery.domain.menu.dto.request.MenuRequestDto;
import com.threemeals.delivery.domain.menu.dto.response.MenuResponseDto;
import com.threemeals.delivery.domain.menu.entity.Menu;
import com.threemeals.delivery.domain.menu.entity.MenuOption;
import com.threemeals.delivery.domain.menu.repository.MenuOptionRepository;
import com.threemeals.delivery.domain.menu.repository.MenuRepository;
import com.threemeals.delivery.domain.store.entity.Store;
import com.threemeals.delivery.domain.store.repository.StoreRepository;
import com.threemeals.delivery.domain.store.service.StoreService;
import com.threemeals.delivery.domain.user.entity.User;
import com.threemeals.delivery.domain.user.service.UserService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MenuService {

	private final MenuRepository menuRepository;
	private final UserService userService;
	private final StoreService storeService;

	// 하나의 스토에 있는 모든 메뉴 가져오기
	public Page<MenuResponseDto> getAllMenuInStore(Long storeId, Pageable pageable) {
		return menuRepository.findAllMenuByStoreId(storeId, pageable);
	}

	@Transactional
	public MenuResponseDto addMenu(Long storeId, Long ownerId, MenuRequestDto requestDto) {

		Store findStore = storeService.getStoreById(storeId);
		if (findStore.getOwner().getId() != ownerId) { // 이렇게 비교하는 건 살짝 별로다. 쿼리를 한 번 더 쏴야 해서 `N+1` 문제 발생할 수 있음.
			throw new AccessDeniedException();
		}

		Menu newMenu = requestDto.toEntity();
		newMenu.setStore(findStore);

		Menu savedMenu = menuRepository.save(newMenu);
		return MenuResponseDto.fromEntity(savedMenu);
	}

	@Transactional
	public MenuResponseDto updateMenu(Long ownerId, Long menuId, MenuRequestDto requestDto) {

		Menu findMenu = getMenuById(menuId);
		validateMenuBelongsToOwner(menuId, ownerId);

		findMenu.updateMe(requestDto);
		return MenuResponseDto.fromEntity(findMenu);
	}

	// 메뉴 삭제시, 서브 메뉴 옵션 모두 삭제
	@Transactional
	public void deleteMenu(Long ownerId, Long menuId) {

		Menu findMenu = getMenuById(menuId);
		validateMenuBelongsToOwner(menuId, ownerId);

		findMenu.deleteMe();
		menuRepository.deleteAllMenuOptionsByMenuId(menuId);
	}

	public Menu getMenuById(Long menuId) {
		Menu findMenu = menuRepository.findById(menuId)
			.orElseThrow(() -> new NotFoundException(MENU_NOT_FOUND));

		findMenu.validateIsDeleted();
		return findMenu;
	}

	/*
	 * 스토어를 건드리지 못해서 우선, 메뉴레포에서 만들어야겠다. 나중에 바꾸자
	 * 스토어를 가져온 다음, 스토어를 가져와서, Owner 비교해야 함
	 */
	protected void validateMenuBelongsToOwner(Long menuId, Long ownerId) {
		if (menuRepository.existsByMenuIdAndOwnerId(menuId, ownerId) == false) { // 해당 메뉴가 owner가 운영하는 가게 메뉴인지 확인
			throw new AccessDeniedException();
		}
	}

}
