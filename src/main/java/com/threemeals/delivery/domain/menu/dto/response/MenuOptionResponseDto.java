package com.threemeals.delivery.domain.menu.dto.response;

import com.threemeals.delivery.domain.menu.entity.MenuOption;

public record MenuOptionResponseDto(
	Long menuOptionId,
	String menuOptionName,
	String description,
	Integer menuOptionPrice,
	String menuOptionImgUrl
) {

	public static MenuOptionResponseDto fromEntity(MenuOption menuOption) {
		return new MenuOptionResponseDto(
			menuOption.getId(),
			menuOption.getMenuOptionName(),
			menuOption.getDescription(),
			menuOption.getMenuOptionPrice(),
			menuOption.getMenuOptionImgUrl()
		);
	}
}
