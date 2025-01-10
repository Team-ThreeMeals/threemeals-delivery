package com.threemeals.delivery.domain.cart.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.threemeals.delivery.domain.cart.dto.request.*;
import com.threemeals.delivery.domain.cart.dto.response.CartItemOptionResponseDto;
import com.threemeals.delivery.domain.cart.dto.response.CartItemResponseDto;
import com.threemeals.delivery.domain.cart.dto.response.CartResponseDto;
import com.threemeals.delivery.domain.cart.entity.Cart;
import com.threemeals.delivery.domain.cart.entity.CartItem;
import com.threemeals.delivery.domain.cart.entity.CartItemOption;
import com.threemeals.delivery.domain.cart.repository.CartRepository;
import com.threemeals.delivery.domain.menu.entity.Menu;
import com.threemeals.delivery.domain.menu.entity.MenuOption;
import com.threemeals.delivery.domain.menu.repository.MenuOptionRepository;
import com.threemeals.delivery.domain.menu.repository.MenuRepository;
import com.threemeals.delivery.domain.order.repository.OrderItemOptionRepository;
import com.threemeals.delivery.domain.order.repository.OrderItemRepository;
import com.threemeals.delivery.domain.order.repository.OrderRepository;
import com.threemeals.delivery.domain.user.entity.User;
import com.threemeals.delivery.domain.store.entity.Store;
import com.threemeals.delivery.domain.user.repository.UserRepository;
import org.springframework.data.redis.core.RedisTemplate;

import com.threemeals.delivery.domain.store.repository.StoreRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;
import java.util.Collections;
import com.threemeals.delivery.domain.order.entity.Order;
import com.threemeals.delivery.domain.order.entity.OrderItem;
import com.threemeals.delivery.domain.order.entity.OrderItemOption;
import com.threemeals.delivery.domain.order.entity.OrderStatus;


import java.util.List;

@Service
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final OrderItemOptionRepository orderItemOptionRepository;
    private final CartRepository cartRepository;
    private final RedisTemplate<String, String> redisTemplate;
    private final StoreRepository storeRepository;
    private final MenuRepository menuRepository;
    private final MenuOptionRepository menuOptionRepository;
    private final UserRepository userRepository;

    @Override
    public CartResponseDto getCart(Long userId) {
        Cart cart = cartRepository.findCart(userId)
                .orElseThrow(() -> new IllegalArgumentException("장바구니가 비어 있습니다."));

        List<CartItemResponseDto> cartItems = cart.getCartItems().stream()
                .map(item -> new CartItemResponseDto(
                        item.getMenu().getId(),
                        item.getQuantity(),
                        item.getCartItemOptions().stream()
                                .map(option -> new CartItemOptionResponseDto(
                                        option.getId(),
                                        option.getAdditionalPrice()
                                ))
                                .toList()
                ))
                .toList();

        return new CartResponseDto(cart.getId(), cartItems);
    }

    @Override
    public void addToCart(Long userId, AddCartRequestDto requestDto) {
        // Redis에서 Cart 조회 또는 새로 생성
        // Menu 조회
        Store store = storeRepository.findById(requestDto.getStoreId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 메뉴입니다. ID: " + requestDto.getStoreId()));


        Cart cart = cartRepository.findCart(userId).orElseGet(() -> {
            Cart newCart = Cart.builder()
                    .userId(userId)
                    .store(store)
                    .totalPrice(0) // 초기 총 가격
                    .build();
            return newCart; // BaseEntity의 Auditing에 의해 createdAt, updatedAt 설정
        });

        // CartItems를 반복 처리
        for (AddCartItemRequestDto item : requestDto.getCartItems()) {
            Long menuId = item.getMenuId(); // AddCartItemRequestDto의 getMenuId() 메서드 호출
            Integer quantity = item.getQuantity();
            List<AddCartItemOptionRequestDto> options = Optional.ofNullable(item.getOptions()).orElse(Collections.emptyList());

            // Menu 조회
            Menu menu = menuRepository.findById(menuId)
                    .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 메뉴입니다. ID: " + menuId));

            // CartItem 생성
            CartItem cartItem = CartItem.builder()
                    .cart(cart)
                    .menu(menu)
                    .quantity(quantity)
                    .build();

            // 옵션 추가
            for (AddCartItemOptionRequestDto optionRequest : options) {
                MenuOption option = menuOptionRepository.findById(optionRequest.getOptionId())
                        .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 옵션입니다. ID: " + optionRequest.getOptionId()));

                CartItemOption cartItemOption = CartItemOption.builder()
                        .id(option.getId()) // ID는 null로 설정하면 Redis에 저장 시 자동 생성
                        .cartItem(cartItem)
                        .option(option)
                        .additionalPrice(optionRequest.getAdditionalPrice())
                        .build();

                cartItem.getCartItemOptions().add(cartItemOption);
            }

            // Cart에 CartItem 추가 (cart.addCartItem은 Cart의 메서드)
            cart.addCartItem(cartItem);
        }

        // Cart 저장
        cartRepository.saveCart(userId, cart); // Redis에 저장
    }

    @Override
    public void updateCartItem(Long userId, UpdateCartRequestDto requestDto) {
        // Redis에서 장바구니 가져오기
        Cart cart = cartRepository.findCart(userId)
                .orElseThrow(() -> new IllegalArgumentException("장바구니가 비어 있습니다."));


        // CartItems 처리
        for (UpdateCartItemRequestDto cartItemDto : requestDto.getCartItems()) {
            Long menuId = cartItemDto.getMenuId();

            // 메뉴 ID로 CartItem 찾기
            CartItem cartItem = cart.getCartItems().stream()
                    .filter(item -> item.getMenu().getId().equals(menuId))
                    .findFirst()
                    .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 메뉴 항목입니다. Menu ID: " + menuId));

            // 수량 업데이트
            cartItem.setQuantity(cartItemDto.getQuantity());

            // 기존 옵션 제거
            cartItem.getCartItemOptions().clear();

            // 새로운 옵션 추가
            for (AddCartItemOptionRequestDto optionRequest : Optional.ofNullable(cartItemDto.getOptions())
                    .orElse(Collections.emptyList())) {
                MenuOption option = menuOptionRepository.findById(optionRequest.getOptionId())
                        .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 옵션입니다. Option ID: " + optionRequest.getOptionId()));

                CartItemOption cartItemOption = CartItemOption.builder()
                        .cartItem(cartItem)
                        .id(option.getId())
                        .additionalPrice(optionRequest.getAdditionalPrice())
                        .build();

                cartItem.getCartItemOptions().add(cartItemOption);
            }
        }

        // 전체 총 가격 업데이트
        cart.updateTotalPrice();

        // Redis에 저장
        cartRepository.saveCart(userId, cart);
    }


    @Override
    public void deleteCartItem(Long userId, Long menuId) {
        // 장바구니 확인
        Cart cart = cartRepository.findCart(userId)
                .orElseThrow(() -> new IllegalArgumentException("장바구니가 비어 있습니다."));

        // 해당 MenuId를 가진 CartItem 찾기
        CartItem cartItem = cart.getCartItems().stream()
                .filter(item -> item.getMenu().getId().equals(menuId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 메뉴 항목입니다."));

        // CartItem 제거
        cart.removeCartItem(cartItem);

        // Redis에 저장
        cartRepository.saveCart(userId, cart);
    }

    @Override
    public void clearCart(Long userId) {
        cartRepository.deleteCart(userId);
    }

    @Transactional
    public void createOrderFromCart(Long userId, String address) {
        // Redis에서 장바구니 가져오기
        Cart cart = cartRepository.findCart(userId)
                .orElseThrow(() -> new IllegalArgumentException("장바구니가 비어 있습니다."));

        // Validate User
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));

        // Validate Store (cart에서 storeId를 가져와야 함)
        Store store = storeRepository.findById(cart.getStore().getId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 가게입니다."));

        // Create Order
        Order order = Order.builder()
                .user(user)
                .store(store)
                .deliveryAddress(address)
                .status(OrderStatus.CONFIRMING)
                .totalPrice(cart.getTotalPrice())
                .build();
        order = orderRepository.save(order);

        // OrderItems와 OrderItemOptions 저장
        for (CartItem cartItem : cart.getCartItems()) {
            // 메뉴 확인
            Menu menu = menuRepository.findById(cartItem.getMenu().getId())
                    .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 메뉴입니다. ID: " + cartItem.getMenu().getId()));

            // OrderItem 생성
            OrderItem orderItem = OrderItem.builder()
                    .order(order)
                    .menu(menu)
                    .quantity(cartItem.getQuantity())
                    .build();
            orderItem = orderItemRepository.save(orderItem);

            // OrderItemOption 생성
            for (CartItemOption cartItemOption : cartItem.getCartItemOptions()) {
                // 옵션 확인
                MenuOption menuOption = menuOptionRepository.findById(cartItemOption.getOption().getId())
                        .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 옵션입니다. ID: " + cartItemOption.getOption().getId()));

                OrderItemOption orderItemOption = OrderItemOption.builder()
                        .orderItem(orderItem)
                        .menuOption(menuOption)
                        .additionalPrice(cartItemOption.getAdditionalPrice())
                        .build();
                orderItemOptionRepository.save(orderItemOption);
            }
        }

        // Redis 장바구니 데이터 삭제
        cartRepository.deleteCart(userId);
    }






}
