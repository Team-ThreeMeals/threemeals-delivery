package com.threemeals.delivery.domain.order.service;

import com.threemeals.delivery.domain.auth.UserPrincipal;
import com.threemeals.delivery.domain.order.dto.request.CreateOrderRequestDto;
import com.threemeals.delivery.domain.order.dto.response.OrderResponseDto;
import com.threemeals.delivery.domain.order.entity.Order;
import com.threemeals.delivery.domain.order.entity.OrderItem;
import com.threemeals.delivery.domain.order.entity.OrderItemOption;
import com.threemeals.delivery.domain.order.entity.OrderStatus;
import com.threemeals.delivery.domain.order.repository.OrderRepository;
import com.threemeals.delivery.domain.cart.entity.Cart;
import com.threemeals.delivery.domain.cart.entity.CartItem;
import com.threemeals.delivery.domain.cart.entity.CartItemOption;
import com.threemeals.delivery.domain.cart.repository.CartRepository;
import com.threemeals.delivery.domain.store.entity.Store;
import com.threemeals.delivery.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final CartRepository cartRepository;

    @Transactional
    public OrderResponseDto createOrder(UserPrincipal userPrincipal) {
        Long userId = userPrincipal.getUserId();

        // 장바구니 데이터 가져오기
        Cart cart = cartRepository.findCart(userId)
                .orElseThrow(() -> new IllegalArgumentException("장바구니가 비어 있습니다."));

        // 최소 주문 금액 확인
        Store store = cart.getStore();
        if (cart.getTotalPrice() < store.getMinimumOrderPrice()) {
            throw new IllegalArgumentException("최소 주문 금액을 충족하지 못했습니다.");
        }

        // 주문 생성
        Order order = Order.builder()
                .user(cart.getUser())
                .store(cart.getStore())
                .status(OrderStatus.CONFIRMING)
                .totalPrice(cart.getTotalPrice())
                .deliveryAddress(cart.getUser().getAddress()) // 유저 주소를 사용
                .build();

        // 주문 항목 추가
        for (CartItem cartItem : cart.getCartItems()) {
            OrderItem orderItem = new OrderItem(order, cartItem.getMenu(), cartItem.getQuantity());

            // 주문 항목 옵션 추가
            for (CartItemOption cartItemOption : cartItem.getCartItemOptions()) {
                OrderItemOption orderItemOption = new OrderItemOption(
                        order.getUser(),                     // User: Order의 사용자
                        order.getStore(),                    // Store: Order의 가게
                        order.getStatus(),                   // OrderStatus: 현재 주문 상태
                        order.getTotalPrice(),               // Integer: 주문 총 금액
                        order.getDeliveryAddress()           // String: 배달 주소
                );

                orderItem.getOrderItemOptions().add(orderItemOption);
            }


            order.getOrderItems().add(orderItem);
        }

        // 주문 저장
        orderRepository.save(order);

        // 장바구니 비우기
        cartRepository.deleteCart(userId);

        return OrderResponseDto.from(order);
    }

    @Transactional(readOnly = true)
    public List<OrderResponseDto> getUserOrders(UserPrincipal userPrincipal) {
        Long userId = userPrincipal.getUserId();
        return orderRepository.findById(userId)
                .stream()
                .map(OrderResponseDto::from)
                .collect(Collectors.toList());
    }

    @Transactional
    public void updateOrderStatus(Long orderId, OrderStatus status) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 주문입니다."));
        order.updateStatus(status);
    }
}
