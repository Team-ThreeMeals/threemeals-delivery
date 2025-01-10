package com.threemeals.delivery.domain.cart.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.threemeals.delivery.domain.menu.entity.MenuOption;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
public class CartItemOption implements Serializable {

    private Long id; // Redis용 UUID

    @JsonBackReference // 순환 참조 방지
    private CartItem cartItem;

    private MenuOption option;

    private Integer additionalPrice;

    @Builder
    @JsonCreator
    public CartItemOption(
            @JsonProperty("id") Long id,
            @JsonProperty("cartItem") CartItem cartItem,
            @JsonProperty("option") MenuOption option,
            @JsonProperty("additionalPrice") Integer additionalPrice) {
        this.id = id;
        this.cartItem = cartItem;
        this.option = option;
        this.additionalPrice = additionalPrice;
    }

    // 추가 메서드나 로직이 필요한 경우 여기에 작성
}
