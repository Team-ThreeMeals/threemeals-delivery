package com.threemeals.delivery.domain.cart.dto.request;

import java.util.List;

public class UpdateCartRequestDto {
    private List<UpdateCartItemRequestDto> cartItems; // 여러 메뉴를 처리하기 위해 리스트로 구성

    // 기본 생성자 및 Getter/Setter
    public UpdateCartRequestDto() {}

    public List<UpdateCartItemRequestDto> getCartItems() {
        return cartItems;
    }

    public void setCartItems(List<UpdateCartItemRequestDto> cartItems) {
        this.cartItems = cartItems;
    }
}
