package com.threemeals.delivery.domain.order.controller;

import com.threemeals.delivery.domain.auth.UserPrincipal;
import com.threemeals.delivery.domain.auth.annotation.Authentication;
import com.threemeals.delivery.domain.order.dto.request.CreateOrderRequestDto;
import com.threemeals.delivery.domain.order.dto.request.UpdateOrderStatusRequestDto;
import com.threemeals.delivery.domain.order.dto.response.OrderDetailResponseDto;
import com.threemeals.delivery.domain.order.dto.response.OrderResponseDto;
import com.threemeals.delivery.domain.order.dto.response.MessageResponse;
import com.threemeals.delivery.domain.order.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    public ResponseEntity<MessageResponse> createOrder(
            @Authentication UserPrincipal userPrincipal,
            @RequestBody CreateOrderRequestDto requestDto) {
        orderService.createOrder(userPrincipal.getUserId(), requestDto);
        return ResponseEntity.status(201).body(new MessageResponse("Order placed successfully."));
    }

    @GetMapping("/{userId}")
    public ResponseEntity<List<OrderResponseDto>> getUserOrders(@PathVariable Long userId) {
        List<OrderResponseDto> orders = orderService.getOrdersByUserId(userId);
        return ResponseEntity.ok(orders);
    }

    @GetMapping("/details/{orderId}")
    public ResponseEntity<OrderDetailResponseDto> getOrderDetails(@PathVariable Long orderId) {
        OrderDetailResponseDto orderDetails = orderService.getOrderDetail(orderId);
        return ResponseEntity.ok(orderDetails);
    }

    @PutMapping("/status/{orderId}")
    public ResponseEntity<MessageResponse> updateOrderStatus(
            @PathVariable Long orderId,
            @Authentication UserPrincipal userPrincipal,
            @RequestBody UpdateOrderStatusRequestDto requestDto) {
        orderService.updateOrderStatus(orderId, requestDto);
        return ResponseEntity.ok(new MessageResponse("Order status updated successfully."));
    }

    @DeleteMapping("/{orderId}")
    public ResponseEntity<MessageResponse> cancelOrder(@PathVariable Long orderId) {
        orderService.cancelOrder(orderId);
        return ResponseEntity.ok(new MessageResponse("Order cancelled successfully."));
    }
}
