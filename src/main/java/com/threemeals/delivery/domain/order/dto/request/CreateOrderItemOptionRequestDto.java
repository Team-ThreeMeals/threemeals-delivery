package com.threemeals.delivery.domain.order.dto.request;

import lombok.Data;
import lombok.Getter;

@Data
@Getter
public class CreateOrderItemOptionRequestDto {
    private Long optionId;
    private Integer additionalPrice;
}
