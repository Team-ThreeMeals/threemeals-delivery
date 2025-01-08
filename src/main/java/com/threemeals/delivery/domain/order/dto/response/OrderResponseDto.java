package com.threemeals.delivery.domain.order.dto.response;

import com.threemeals.delivery.domain.order.entity.Order;
import com.threemeals.delivery.domain.order.entity.OrderItem;
import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.stream.Collectors;

@Data
@Builder
public class OrderResponseDto {
    private Long orderId;
    private Long userId;
    private Long storeId;
    private String status;
    private Integer totalPrice;
    private String deliveryAddress;
    private List<OrderItemResponseDto> orderItems;

    public static OrderResponseDto from(Order order) {
        return OrderResponseDto.builder()
                .orderId(order.getId())
                .userId(order.getUser().getId())
                .storeId(order.getStore().getId())
                .status(order.getStatus().name())
                .totalPrice(order.getTotalPrice())
                .deliveryAddress(order.getDeliveryAddress())
                .orderItems(order.getOrderItems().stream()
                        .map(OrderItemResponseDto::from)
                        .collect(Collectors.toList()))
                .build();
    }

    @Data
    @Builder
    public static class OrderItemResponseDto {
        private Long menuId;
        private Integer quantity;

        public static OrderItemResponseDto from(OrderItem orderItem) {
            return OrderItemResponseDto.builder()
                    .menuId(orderItem.getMenu().getId())
                    .quantity(orderItem.getQuantity())
                    .build();
        }
    }
}
