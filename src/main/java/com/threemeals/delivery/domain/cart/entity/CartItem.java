package com.threemeals.delivery.domain.cart.entity;

import com.threemeals.delivery.domain.common.entity.BaseEntity;
import com.threemeals.delivery.domain.menu.entity.Menu;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "cart_item")
public class CartItem extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cart_id", nullable = false)
    private Cart cart;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "menu_id", nullable = false)
    private Menu menu;

    @Column(name = "quantity", nullable = false)
    private Integer quantity;

    @OneToMany(mappedBy = "cartItem", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CartItemOption> cartItemOptions = new ArrayList<>();

    @Builder
    public CartItem(Cart cart, Menu menu, Integer quantity) {
        this.cart = cart;
        this.menu = menu;
        this.quantity = quantity;
    }

    public Long getMenuId() {
        return this.menu.getId(); // Menu의 ID 반환
    }

    public int getTotalPrice(int menuPrice) {
        int totalOptionPrice = cartItemOptions.stream()
                .mapToInt(CartItemOption::getAdditionalPrice)
                .sum();
        return (menuPrice + totalOptionPrice) * quantity;
    }

}
