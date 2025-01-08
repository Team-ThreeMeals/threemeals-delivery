package com.threemeals.delivery.domain.order.controller;

import com.threemeals.delivery.domain.auth.UserPrincipal;
import com.threemeals.delivery.domain.auth.annotation.Authentication;
import com.threemeals.delivery.domain.order.dto.request.CreateOrderRequestDto;
import com.threemeals.delivery.domain.order.dto.response.OrderResponseDto;
import com.threemeals.delivery.domain.order.entity.OrderStatus;
import com.threemeals.delivery.domain.order.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;


    @PostMapping
    public OrderResponseDto createOrder(@Authentication UserPrincipal userPrincipal) {
        return orderService.createOrder(userPrincipal);
    }


    @GetMapping
    public List<OrderResponseDto> getUserOrders(@Authentication UserPrincipal userPrincipal) {
        return orderService.getUserOrders(userPrincipal);
    }


    @PatchMapping("/{orderId}/status")
    public void updateOrderStatus(@PathVariable Long orderId,
                                  @RequestParam OrderStatus status) {
        orderService.updateOrderStatus(orderId, status);
    }
}
