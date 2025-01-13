package com.threemeals.delivery.domain.order.dto.request;

import com.threemeals.delivery.domain.order.entity.OrderStatus;

public class UpdateOrderStatusRequestDto {
    private OrderStatus newStatus; // Enum 타입으로 주문 상태를 지정합니다.

    // Getter for newStatus
    public OrderStatus getNewStatus() {
        return newStatus;
    }

    // Setter for newStatus
    public void setNewStatus(OrderStatus newStatus) {
        this.newStatus = newStatus;
    }
}
