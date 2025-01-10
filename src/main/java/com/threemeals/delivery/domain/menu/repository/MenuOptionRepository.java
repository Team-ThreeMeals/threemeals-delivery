package com.threemeals.delivery.domain.menu.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.threemeals.delivery.domain.menu.dto.response.MenuOptionResponseDto;
import com.threemeals.delivery.domain.menu.entity.MenuOption;

public interface MenuOptionRepository extends JpaRepository<MenuOption, Long> {

	@Query("select "
		+ "new com.threemeals.delivery.domain.menu.dto.response.MenuOptionResponseDto("
		+ "mo.id, mo.menuOptionName, mo.description, mo.menuOptionPrice, mo.menuOptionImgUrl"
		+ ") " +
		"from MenuOption mo where mo.menu.id = :menuId and mo.isDeleted = false")
	List<MenuOptionResponseDto> findByMenu_Id(Long menuId);
}
