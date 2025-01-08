package com.threemeals.delivery.domain.order.dto.request;

import lombok.Data;

@Data
public class CreateOrderItemOptionRequestDto {
    private Long optionId;
    private Integer additionalPrice;
}
