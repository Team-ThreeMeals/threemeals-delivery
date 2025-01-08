package com.threemeals.delivery.domain.order.entity;

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
    private List<OrderItemOption> orderItemOptions = new ArrayList<>();

    @Builder
    public OrderItem(Order order, Menu menu, Integer quantity) {
        this.order = order;
        this.menu = menu;
        this.quantity = quantity;
    }

    public List<OrderItemOption> getOrderItemOptions() {
        return this.orderItemOptions;
    }
}
