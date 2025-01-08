package com.threemeals.delivery.domain.cart.controller;

import com.threemeals.delivery.domain.auth.UserPrincipal;
import com.threemeals.delivery.domain.auth.annotation.Authentication;
import com.threemeals.delivery.domain.cart.dto.request.AddCartRequestDto;
import com.threemeals.delivery.domain.cart.dto.response.CartResponseDto;
import com.threemeals.delivery.domain.cart.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;


    @GetMapping
    public CartResponseDto getCart(@Authentication UserPrincipal userPrincipal) {
        return cartService.getCart(userPrincipal);
    }

    @PostMapping
    public void addToCart(@Authentication UserPrincipal userPrincipal, @RequestBody AddCartRequestDto requestDto) {
        cartService.addToCart(userPrincipal, requestDto);
    }

    @DeleteMapping
    public void clearCart(@Authentication UserPrincipal userPrincipal) {
        cartService.clearCartAfterOrder(userPrincipal);
    }
}
