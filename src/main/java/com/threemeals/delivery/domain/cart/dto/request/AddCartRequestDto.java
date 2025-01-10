package com.threemeals.delivery.domain.cart.dto.request;

import lombok.Data;

import java.util.List;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

@Data
public class AddCartRequestDto {
    private Long storeId; // 가게 ID
    private List<AddCartItemRequestDto> cartItems; // 장바구니 항목 리스트

    @JsonCreator
    public AddCartRequestDto(
            @JsonProperty("storeId") Long storeId,
            @JsonProperty("cartItems") List<AddCartItemRequestDto> cartItems) {
        this.storeId = storeId;
        this.cartItems = cartItems;
    }

    // Getters and setters
    public Long getStoreId() {
        return storeId;
    }

    public void setStoreId(Long storeId) {
        this.storeId = storeId;
    }

    public List<AddCartItemRequestDto> getCartItems() {
        return cartItems;
    }

    public void setCartItems(List<AddCartItemRequestDto> cartItems) {
        this.cartItems = cartItems;
    }
}
