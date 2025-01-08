package com.threemeals.delivery.domain.cart.dto.request;

import lombok.Data;

import java.util.List;

@Data
public class AddCartItemOptionRequestDto {
    private Long optionId; // 옵션 ID
    private Integer additionalPrice; // 추가 금액
}
