package com.threemeals.delivery.domain.menu.dto.request;

import com.threemeals.delivery.domain.menu.entity.MenuOption;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record MenuOptionRequestDto(
	@NotBlank(message = "옵션명은 필수 값입니다.")
	String menuOptionName,

	String description,

	@NotNull(message = "옵션 가격은 필수 값입니다")
	Integer menuOptionPrice,

	String menuOptionImgUrl
) {

	public MenuOption toEntity() {
		return MenuOption.builder()
			.menuOptionName(menuOptionName)
			.description(description)
			.menuOptionPrice(menuOptionPrice)
			.menuOptionImgUrl(menuOptionImgUrl)
			.build();
	}
}
