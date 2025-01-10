package com.threemeals.delivery.domain.order.entity;

import com.threemeals.delivery.domain.common.entity.BaseEntity;
import com.threemeals.delivery.domain.menu.entity.Menu;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;
import java.util.HashSet;
import java.util.Set;
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "order_items")
public class OrderItem extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "menu_id", nullable = false)
    private Menu menu;

    @Column(name = "quantity", nullable = false)
    private Integer quantity;

    @OneToMany(mappedBy = "orderItem", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<OrderItemOption> orderItemOptions = new HashSet<>();

    @Builder
    public OrderItem(Order order, Menu menu, Integer quantity) {
        this.order = order;
        this.menu = menu;
        this.quantity = quantity;
    }

    public Set<OrderItemOption> getOrderItemOptions() {
        return this.orderItemOptions;
    }

    public Long getMenuId() {
        return this.menu.getId();
    }

    public String getMenuName() {
        return this.menu.getMenuName();
    }

    public int getMenuPrice() {
        return this.menu.getPrice();
    }
    public void setOrder(Order order) {
        if (this.order != null) {
            // 기존 연관 관계 제거
            this.order.getOrderItems().remove(this);
        }
        this.order = order;
        if (order != null) {
            // 새로운 연관 관계 추가
            order.getOrderItems().add(this);
        }
    }

    public void setMenu(Menu menu) {
        this.menu = menu;
    }

}
