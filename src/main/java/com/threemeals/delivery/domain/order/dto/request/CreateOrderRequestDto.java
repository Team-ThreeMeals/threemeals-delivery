package com.threemeals.delivery.domain.order.dto.request;

import lombok.Data;
import lombok.Getter;
import java.util.List;

@Data
@Getter
public class CreateOrderRequestDto {
    private Long userId;
    private Long storeId;
    private String deliveryAddress;
    private Integer totalPrice;
    private List<CreateOrderItemRequestDto> orderItems;
}

