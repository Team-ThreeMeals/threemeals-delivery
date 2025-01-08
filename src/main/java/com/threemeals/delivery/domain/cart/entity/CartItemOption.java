package com.threemeals.delivery.domain.cart.entity;

import com.threemeals.delivery.domain.common.entity.BaseEntity;
import com.threemeals.delivery.domain.menu.entity.MenuOption;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "cart_item_option")
public class CartItemOption extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cart_item_id", nullable = false)
    private CartItem cartItem;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "option_id", nullable = false)
    private MenuOption option;

    @Column(name = "additional_price", nullable = false)
    private Integer additionalPrice;

    @Builder
    public CartItemOption(CartItem cartItem, MenuOption option, Integer additionalPrice) {
        this.cartItem = cartItem;
        this.option = option;
        this.additionalPrice = additionalPrice;
    }
}
