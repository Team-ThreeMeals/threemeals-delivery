package com.threemeals.delivery.domain.menu.service;

import static com.threemeals.delivery.config.error.ErrorCode.*;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestParam;

import com.threemeals.delivery.domain.common.exception.AccessDeniedException;
import com.threemeals.delivery.domain.common.exception.NotFoundException;
import com.threemeals.delivery.domain.menu.dto.request.MenuRequestDto;
import com.threemeals.delivery.domain.menu.dto.response.MenuResponseDto;
import com.threemeals.delivery.domain.menu.entity.Menu;
import com.threemeals.delivery.domain.menu.repository.MenuRepository;
import com.threemeals.delivery.domain.store.entity.Store;
import com.threemeals.delivery.domain.store.repository.StoreRepository;
import com.threemeals.delivery.domain.user.entity.User;
import com.threemeals.delivery.domain.user.service.UserService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MenuService {

	private final MenuRepository menuRepository;
	private final UserService userService;
	private final StoreRepository storeRepository; // 바꿔야 함

	// 하나의 스토에 있는 모든 메뉴 가져오기
	public Page<MenuResponseDto> getAllMenuInStore(Long storeId, Pageable pageable) {
		return menuRepository.findAllMenuByStoreId(storeId, pageable);
	}

	@Transactional
	public MenuResponseDto createMenu(Long ownerId, MenuRequestDto requestDto) {
		User findOwner = userService.getOwnerById(ownerId);

		// get store (나중에 서비스에서 단건 조회하는 걸로 바꾸자)
		Store store = storeRepository.findById(findOwner.getId())
			.orElseThrow(() -> new NotFoundException(STORE_NOT_FOUND));

		Menu newMenu = requestDto.toEntity();
		newMenu.setStore(store);

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

	@Transactional
	public void deleteMenu(Long ownerId, Long menuId) {

		Menu findMenu = getMenuById(menuId);
		validateMenuBelongsToOwner(menuId, ownerId);

		findMenu.deleteMe();
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
	private void validateMenuBelongsToOwner(Long menuId, Long ownerId) {
		if (menuRepository.existsByMenuIdAndOwnerId(menuId, ownerId) == false) { // 해당 메뉴가 owner가 운영하는 가게 메뉴인지 확인
			throw new AccessDeniedException();
		}
	}

}
