package com.threemeals.delivery.domain.cart.dto.request;

import lombok.Data;

import java.util.List;

@Data
public class AddCartRequestDto {
    private Long storeId; // 가게 ID
    private List<AddCartItemRequestDto> cartItems; // 장바구니 항목 리스트
}
