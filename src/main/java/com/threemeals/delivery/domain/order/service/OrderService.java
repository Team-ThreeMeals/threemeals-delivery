package com.threemeals.delivery.domain.order.service;

import com.threemeals.delivery.domain.order.dto.request.CreateOrderRequestDto;
import com.threemeals.delivery.domain.order.dto.request.UpdateOrderStatusRequestDto;
import com.threemeals.delivery.domain.order.dto.response.OrderDetailResponseDto;
import com.threemeals.delivery.domain.order.dto.response.MessageResponse;
import com.threemeals.delivery.domain.order.dto.response.OrderResponseDto;

import java.util.List;

public interface OrderService {
    MessageResponse createOrder(Long userId, CreateOrderRequestDto requestDto);
    List<OrderResponseDto> getOrdersByUserId(Long userId);
    OrderDetailResponseDto getOrderDetail(Long orderId);
    MessageResponse updateOrderStatus(Long orderId, UpdateOrderStatusRequestDto requestDto);
    MessageResponse cancelOrder(Long orderId);
}
