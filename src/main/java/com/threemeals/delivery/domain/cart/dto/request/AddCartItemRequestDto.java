package com.threemeals.delivery.domain.cart.dto.request;

import lombok.Data;

import java.util.List;

@Data
public class AddCartItemRequestDto {
    private Long menuId; // 메뉴 ID
    private Integer quantity; // 수량
    private List<AddCartItemOptionRequestDto> options; // 옵션 리스트
}
