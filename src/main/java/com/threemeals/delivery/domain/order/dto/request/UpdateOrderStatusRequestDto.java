package com.threemeals.delivery.domain.order.dto.request;

import lombok.Data;

@Data
public class UpdateOrderStatusRequestDto {
    private String status; // 주문 상태 (e.g., "조리 중", "배달 중")
}
