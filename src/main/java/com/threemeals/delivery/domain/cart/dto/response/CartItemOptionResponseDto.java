package com.threemeals.delivery.domain.cart.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CartItemOptionResponseDto {
    private Long optionId; // 메뉴 옵션 ID
    private Integer additionalPrice; // 추가 가격
}
