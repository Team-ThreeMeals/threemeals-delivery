package com.threemeals.delivery.domain.cart.service;

import com.threemeals.delivery.domain.cart.dto.request.AddCartItemRequestDto;
import com.threemeals.delivery.domain.cart.dto.request.AddCartRequestDto;
import com.threemeals.delivery.domain.cart.dto.request.UpdateCartItemRequestDto;
import com.threemeals.delivery.domain.cart.dto.request.UpdateCartRequestDto;
import com.threemeals.delivery.domain.cart.dto.response.CartResponseDto;


public interface CartService {
    CartResponseDto getCart(Long userId);

    void addToCart(Long userId, AddCartRequestDto requestDto);
    void updateCartItem(Long userId, UpdateCartRequestDto requestDto);
    void clearCart(Long userId);
    void deleteCartItem(Long userId, Long menuId);
    void createOrderFromCart(Long userId, String address);
}
