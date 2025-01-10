package com.threemeals.delivery.domain.cart.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.threemeals.delivery.domain.menu.entity.Menu;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class CartItem implements Serializable {

    private Long id; // Redis용 UUID

    @JsonBackReference // 순환 참조 방지
    private Cart cart;

    private Menu menu;
    private Integer quantity;
    private List<CartItemOption> cartItemOptions = new ArrayList<>();

    @Builder
    public CartItem(Long id, Cart cart, Menu menu, Integer quantity, List<CartItemOption> cartItemOptions) {
        this.id = id;
        this.cart = cart;
        this.menu = menu;
        this.quantity = quantity;
        this.cartItemOptions = cartItemOptions != null ? cartItemOptions : new ArrayList<>();
    }

    @JsonCreator
    public static CartItem create(
            @JsonProperty("id") Long id,
            @JsonProperty("cart") Cart cart,
            @JsonProperty("menu") Menu menu,
            @JsonProperty("quantity") Integer quantity,
            @JsonProperty("cartItemOptions") List<CartItemOption> cartItemOptions) {
        return CartItem.builder()
                .id(id)
                .cart(cart)
                .menu(menu)
                .quantity(quantity)
                .cartItemOptions(cartItemOptions)
                .build();
    }

    @JsonIgnore
    public int getTotalPrice() {
        int optionsPrice = cartItemOptions.stream()
                .mapToInt(CartItemOption::getAdditionalPrice)
                .sum();
        return (menu.getPrice() + optionsPrice) * quantity;
    }

    public void setQuantity(Integer quantity) {
        if (quantity == null || quantity <= 0) {
            throw new IllegalArgumentException("수량은 1 이상이어야 합니다.");
        }
        this.quantity = quantity;
    }
}
