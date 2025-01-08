package com.threemeals.delivery.domain.cart.service;

import com.threemeals.delivery.domain.auth.UserPrincipal;
import com.threemeals.delivery.domain.cart.dto.request.AddCartRequestDto;
import com.threemeals.delivery.domain.cart.dto.request.AddCartItemRequestDto;
import com.threemeals.delivery.domain.cart.dto.request.AddCartItemOptionRequestDto;
import com.threemeals.delivery.domain.cart.dto.response.CartResponseDto;
import com.threemeals.delivery.domain.cart.entity.Cart;
import com.threemeals.delivery.domain.cart.entity.CartItem;
import com.threemeals.delivery.domain.cart.entity.CartItemOption;
import com.threemeals.delivery.domain.cart.repository.CartRepository;
import com.threemeals.delivery.domain.menu.entity.Menu;
import com.threemeals.delivery.domain.menu.entity.MenuOption;
import com.threemeals.delivery.domain.store.entity.Store;
import com.threemeals.delivery.domain.store.repository.StoreRepository;
import com.threemeals.delivery.domain.user.entity.User;
import com.threemeals.delivery.domain.user.repository.UserRepository;
import com.threemeals.delivery.domain.menu.repository.MenuRepository;
import com.threemeals.delivery.domain.menu.repository.MenuOptionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CartService {

    private final CartRepository cartRepository;
    private final UserRepository userRepository;
    private final StoreRepository storeRepository;
    private final MenuRepository menuRepository;
    private final MenuOptionRepository menuOptionRepository;

    public CartResponseDto getCart(UserPrincipal userPrincipal) {
        Long userId = userPrincipal.getUserId();
        Cart cart = cartRepository.findCart(userId)
                .orElseThrow(() -> new IllegalArgumentException("장바구니가 비어 있습니다."));
        return CartResponseDto.from(cart);
    }

    public void addToCart(UserPrincipal userPrincipal, AddCartRequestDto requestDto) {
        Long userId = userPrincipal.getUserId();
        Long storeId = requestDto.getStoreId();

        // User와 Store 조회
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));
        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 가게입니다."));

        // Cart 조회 또는 생성
        Cart cart = cartRepository.findCart(userId)
                .orElse(Cart.builder()
                        .user(user) // User 객체 전달
                        .store(store) // Store 객체 전달
                        .totalPrice(0)
                        .build());

        // 다른 가게 메뉴인지 확인 후 초기화
        if (!cart.getStore().equals(store)) {
            cart.getCartItems().clear();
            cart.setTotalPrice(0);
            cart.setStore(store);
        }

        // CartItem 생성 및 추가
        for (AddCartItemRequestDto cartItemRequest : requestDto.getCartItems()) {
            Menu menu = menuRepository.findById(cartItemRequest.getMenuId())
                    .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 메뉴입니다."));

            CartItem cartItem = new CartItem(cart, menu, cartItemRequest.getQuantity());

            // CartItemOption 생성 및 추가
            for (AddCartItemOptionRequestDto optionRequest : cartItemRequest.getOptions()) {
                MenuOption menuOption = menuOptionRepository.findById(optionRequest.getOptionId())
                        .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 옵션입니다."));

                CartItemOption cartItemOption = new CartItemOption(cartItem, menuOption, optionRequest.getAdditionalPrice());
                cartItem.getCartItemOptions().add(cartItemOption);
            }

            // CartItem을 Cart에 추가
            cart.getCartItems().add(cartItem);
        }

        // 총 금액 업데이트
        updateTotalPrice(cart);

        // Redis에 저장
        cartRepository.saveCart(userId, cart);
    }

    public void clearCartAfterOrder(UserPrincipal userPrincipal) {
        Long userId = userPrincipal.getUserId();
        cartRepository.deleteCart(userId);
    }

    private void updateTotalPrice(Cart cart) {
        int totalPrice = cart.getCartItems().stream()
                .mapToInt(item -> {
                    int itemPrice = item.getMenu().getPrice() * item.getQuantity();
                    int optionsPrice = item.getCartItemOptions().stream()
                            .mapToInt(CartItemOption::getAdditionalPrice)
                            .sum();
                    return itemPrice + optionsPrice;
                })
                .sum();
        cart.setTotalPrice(totalPrice);
    }
}
