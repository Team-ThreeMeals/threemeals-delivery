package com.threemeals.delivery.domain.cart.dto.request;

import lombok.Data;

import java.util.List;

@Data
public class UpdateCartItemRequestDto {
    private Long menuId;
    private Integer quantity; // 수정할 수량
    private List<AddCartItemOptionRequestDto> options; // 수정할 옵션 리스트
}
