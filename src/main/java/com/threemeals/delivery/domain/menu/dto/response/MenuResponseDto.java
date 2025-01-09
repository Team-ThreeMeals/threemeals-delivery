package com.threemeals.delivery.domain.menu.dto.response;

import com.threemeals.delivery.domain.menu.entity.Menu;

public record MenuResponseDto(
	Long menuId,
	String menuName,
	String description,
	Integer price,
	String menuImgUrl
) {

	public static MenuResponseDto fromEntity(Menu menu) {
		return new MenuResponseDto(
			menu.getId(),
			menu.getMenuName(),
			menu.getDescription(),
			menu.getPrice(),
			menu.getMenuImgUrl()
		);
	}
}
