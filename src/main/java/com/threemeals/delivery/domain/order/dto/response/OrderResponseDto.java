package com.threemeals.delivery.domain.order.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.util.List;

@Data
@AllArgsConstructor
public class OrderResponseDto {
    private Long orderId; // 주문 ID
    private Long storeId; // 가게 ID
    private String status; // 주문 상태
    private Integer totalPrice; // 총 금액
    private String deliveryAddress; // 배달 주소
    private List<OrderItemResponseDto> orderItems; // 주문 항목 리스트
}