package com.threemeals.delivery.domain.order.dto.request;

import lombok.Data;

import java.util.List;

@Data
public class CreateOrderRequestDto {
    private Long userId;
    private Long storeId;
    private Integer totalPrice;
    private String deliveryAddress;
    private List<OrderItemRequestDto> orderItems;

    @Data
    public static class OrderItemRequestDto {
        private Long menuId;
        private Integer quantity;
        private List<OrderItemOptionRequestDto> options;
    }

    @Data
    public static class OrderItemOptionRequestDto {
        private Long optionId;
        private Integer additionalPrice;
    }
}
