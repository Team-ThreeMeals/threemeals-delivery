package com.threemeals.delivery.domain.menu.dto.response;

import java.util.List;

public record GetMenuWithOptionsResponseDto(
	MenuResponseDto menuResponseDto,
	List<MenuOptionResponseDto> menuOptions
) {

}
