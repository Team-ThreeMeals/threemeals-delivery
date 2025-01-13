package com.threemeals.delivery.domain.order.service;

import com.threemeals.delivery.domain.order.dto.request.CreateOrderRequestDto;
import com.threemeals.delivery.domain.order.dto.request.UpdateOrderStatusRequestDto;
import com.threemeals.delivery.domain.order.dto.response.OrderDetailResponseDto;
import com.threemeals.delivery.domain.order.dto.response.MessageResponse;
import com.threemeals.delivery.domain.order.dto.response.OrderResponseDto;
import com.threemeals.delivery.domain.order.entity.OrderStatus;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface OrderService {
    List<OrderResponseDto> getOrdersByUserId(Long userId);
    OrderDetailResponseDto getOrderDetail(Long orderId);
    MessageResponse updateOrderStatus(Long userId, Long orderId, OrderStatus newStatus);
    MessageResponse cancelOrder(Long orderId);
}
