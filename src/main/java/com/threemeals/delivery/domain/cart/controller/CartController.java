package com.threemeals.delivery.domain.cart.controller;

import com.threemeals.delivery.domain.auth.UserPrincipal;
import com.threemeals.delivery.domain.auth.annotation.Authentication;
import com.threemeals.delivery.domain.cart.dto.request.AddCartRequestDto;
import com.threemeals.delivery.domain.cart.dto.request.UpdateCartRequestDto;
import com.threemeals.delivery.domain.cart.dto.response.CartResponseDto;
import com.threemeals.delivery.domain.cart.dto.request.AddCartItemRequestDto;
import com.threemeals.delivery.domain.cart.dto.request.UpdateCartItemRequestDto;
import com.threemeals.delivery.domain.cart.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("carts")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    @GetMapping
    public CartResponseDto getCart(@Authentication UserPrincipal userPrincipal) {
        return cartService.getCart(userPrincipal.getUserId());
    }

    @PostMapping("/items")
    public void addToCart(@Authentication UserPrincipal userPrincipal, @RequestBody AddCartRequestDto requestDto) {
        cartService.addToCart(userPrincipal.getUserId(), requestDto);
    }

    @PutMapping("/items/{menuId}")
    public void updateCartItem(@Authentication UserPrincipal userPrincipal, @RequestBody UpdateCartRequestDto requestDto) {
        cartService.updateCartItem(userPrincipal.getUserId(), requestDto);
    }

    @DeleteMapping("/items/{menuId}")
    public void deleteCartItem(@Authentication UserPrincipal userPrincipal, @PathVariable Long menuId) {
        cartService.deleteCartItem(userPrincipal.getUserId(), menuId);
    }

    @DeleteMapping
    public void clearCart(@Authentication UserPrincipal userPrincipal) {
        cartService.clearCart(userPrincipal.getUserId());
    }

    @PostMapping("/ordersubmit")
    public void createOrderFromCart(@Authentication UserPrincipal userPrincipal, @RequestBody String address) {
        cartService.createOrderFromCart(userPrincipal.getUserId(), address);
    }

}
