package com.threemeals.delivery.domain.order.service;

import com.threemeals.delivery.domain.order.dto.request.CreateOrderRequestDto;
import com.threemeals.delivery.domain.order.dto.request.UpdateOrderStatusRequestDto;
import com.threemeals.delivery.domain.order.dto.response.OrderDetailResponseDto;
import com.threemeals.delivery.domain.order.dto.response.OrderResponseDto;
import com.threemeals.delivery.domain.order.dto.response.OrderItemResponseDto;
import com.threemeals.delivery.domain.order.dto.response.OrderItemOptionResponseDto;
import com.threemeals.delivery.domain.order.dto.response.MessageResponse;
import com.threemeals.delivery.domain.order.entity.Order;
import com.threemeals.delivery.domain.order.entity.OrderItem;
import com.threemeals.delivery.domain.order.entity.OrderItemOption;
import com.threemeals.delivery.domain.order.entity.OrderStatus;
import com.threemeals.delivery.domain.order.repository.OrderRepository;
import com.threemeals.delivery.domain.menu.entity.Menu;
import com.threemeals.delivery.domain.menu.entity.MenuOption;
import com.threemeals.delivery.domain.menu.repository.MenuRepository;
import com.threemeals.delivery.domain.menu.repository.MenuOptionRepository;
import com.threemeals.delivery.domain.store.entity.Store;
import com.threemeals.delivery.domain.store.repository.StoreRepository;
import com.threemeals.delivery.domain.user.entity.User;
import com.threemeals.delivery.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final MenuRepository menuRepository;
    private final MenuOptionRepository menuOptionRepository;
    private final StoreRepository storeRepository;
    private final UserRepository userRepository;

    @Transactional
    @Override
    public MessageResponse createOrder(Long userId, CreateOrderRequestDto requestDto) {
        // Validate Store
        Store store = storeRepository.findById(requestDto.getStoreId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 가게입니다."));

        // Validate User
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));

        // Create Order
        Order order = Order.builder()
                .user(user)
                .store(store)
                .deliveryAddress(requestDto.getDeliveryAddress())
                .status(OrderStatus.CONFIRMING)
                .totalPrice(requestDto.getTotalPrice())
                .build();

        // Validate and Add Order Items
        for (var orderItemDto : requestDto.getOrderItems()) {
            Menu menu = menuRepository.findById(orderItemDto.getMenuId())
                    .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 메뉴입니다."));

            OrderItem orderItem = OrderItem.builder()
                    .order(order)
                    .menu(menu)
                    .quantity(orderItemDto.getQuantity())
                    .build();

            // Validate and Add Order Item Options
            for (var optionDto : orderItemDto.getOptions()) {
                MenuOption option = menuOptionRepository.findById(optionDto.getOptionId())
                        .orElseThrow(() -> new IllegalArgumentException(
                                String.format("존재하지 않는 옵션입니다. (옵션 ID: %d)", optionDto.getOptionId())));

                OrderItemOption orderItemOption = OrderItemOption.builder()
                        .orderItem(orderItem)
                        .menuOption(option)
                        .additionalPrice(optionDto.getAdditionalPrice())
                        .build();

                orderItem.getOrderItemOptions().add(orderItemOption);
            }

            order.getOrderItems().add(orderItem);
        }

        orderRepository.save(order);
        return new MessageResponse("Order placed successfully.");
    }


    @Transactional(readOnly = true)
    @Override
    public List<OrderResponseDto> getOrdersByUserId(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));
        List<Order> orders = orderRepository.findAllByUser(user);


        return orders.stream().map(order -> new OrderResponseDto(
                order.getId(),
                order.getStore().getId(),
                order.getStatus().toString(),
                order.getTotalPrice(),
                order.getDeliveryAddress(),
                order.getOrderItems().stream()
                        .map(item -> new OrderItemResponseDto(
                                item.getMenu().getId(),
                                menuRepository.findById(item.getMenu().getId())
                                        .map(Menu::getMenuName)
                                        .orElse("Unknown Menu"),
                                item.getQuantity(),
                                item.getMenu().getPrice(),
                                item.getOrderItemOptions().stream()
                                        .map(option -> new OrderItemOptionResponseDto(
                                                option.getMenuOption().getId(),
                                                menuOptionRepository.findById(option.getMenuOption().getId())
                                                        .map(MenuOption::getMenuOptionName)
                                                        .orElse("Unknown Option"),
                                                option.getAdditionalPrice()
                                        ))
                                        .collect(Collectors.toList())
                        ))
                        .collect(Collectors.toList())
        )).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    @Override
    public OrderDetailResponseDto getOrderDetail(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 주문입니다."));

        return new OrderDetailResponseDto(
                order.getId(),
                order.getStore().getId(),
                order.getStatus().toString(),
                order.getTotalPrice(),
                order.getDeliveryAddress(),
                order.getOrderItems().stream()
                        .map(item -> new OrderItemResponseDto(
                                item.getMenu().getId(),
                                menuRepository.findById(item.getMenu().getId())
                                        .map(Menu::getMenuName)
                                        .orElse("Unknown Menu"),
                                item.getQuantity(),
                                item.getMenu().getPrice(),
                                item.getOrderItemOptions().stream()
                                        .map(option -> new OrderItemOptionResponseDto(
                                                option.getMenuOption().getId(),
                                                menuOptionRepository.findById(option.getMenuOption().getId())
                                                        .map(MenuOption::getMenuOptionName)
                                                        .orElse("Unknown Option"),
                                                option.getAdditionalPrice()
                                        ))
                                        .collect(Collectors.toList())
                        ))
                        .collect(Collectors.toList())
        );
    }

    @Transactional
    @Override
    public MessageResponse updateOrderStatus(Long orderId, UpdateOrderStatusRequestDto requestDto) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 주문입니다."));

        order.updateStatus(OrderStatus.valueOf(requestDto.getStatus().toUpperCase()));
        return new MessageResponse("Order status updated successfully.");
    }

    @Transactional
    @Override
    public MessageResponse cancelOrder(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 주문입니다."));

        order.updateStatus(OrderStatus.CANCELLED);
        return new MessageResponse("Order cancelled successfully.");
    }

}
