package com.threemeals.delivery.domain.menu.dto.request;

import com.threemeals.delivery.domain.menu.entity.Category;
import com.threemeals.delivery.domain.menu.entity.Menu;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record MenuRequestDto(
	@NotBlank(message = "음식 카테고리는 필수 값입니다.")
	String category,

	@NotBlank(message = "메뉴 제목은 필수 값입니다.")
	String menuName,

	String description,

	@NotNull(message = "메뉴 가격은 필수 값입니다,")
	Integer price,

	String menuImgUrl

) {

	public Menu toEntity() {
		return Menu.builder()
			.category(Category.of(category))
			.menuName(menuName)
			.description(description)
			.price(price)
			.menuImgUrl(menuImgUrl)
			.build();
	}
}
