package com.threemeals.delivery.domain.order;

import com.threemeals.delivery.domain.auth.UserPrincipal;
import com.threemeals.delivery.domain.order.service.OrderService;
import com.threemeals.delivery.domain.order.dto.response.OrderResponseDto;
import com.threemeals.delivery.domain.order.entity.Order;
import com.threemeals.delivery.domain.order.repository.OrderRepository;
import com.threemeals.delivery.domain.store.entity.Store;
import com.threemeals.delivery.domain.user.entity.User;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class OrderServiceTest {

    @InjectMocks
    private OrderService orderService;

    @Mock
    private OrderRepository orderRepository;

    public OrderServiceTest() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateOrder() {
        // Given
        Long userId = 1L;
        User user = new User(userId, "test@example.com", "Test User");
        Store store = new Store(2L, "Test Store", 10000);
        Order order = Order.builder().user(user).store(store).totalPrice(15000).build();

        when(orderRepository.save(any(Order.class))).thenReturn(order);

        // When
        OrderResponseDto response = orderService.createOrder(new UserPrincipal(userId, null));

        // Then
        assertNotNull(response);
        assertEquals(15000, response.getTotalPrice());
    }

    @Test
    void testGetUserOrders() {
        // Given
        Long userId = 1L;
        Order order = new Order();
        when(orderRepository.findById(userId)).thenReturn(List.of(order));

        // When
        List<OrderResponseDto> responses = orderService.getUserOrders(new UserPrincipal(userId, null));

        // Then
        assertFalse(responses.isEmpty());
    }
}
