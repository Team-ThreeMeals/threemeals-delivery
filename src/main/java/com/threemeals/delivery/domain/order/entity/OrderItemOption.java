package com.threemeals.delivery.domain.order.entity;

import com.threemeals.delivery.domain.common.entity.BaseEntity;
import com.threemeals.delivery.domain.user.entity.User;
import com.threemeals.delivery.domain.store.entity.Store;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "orders")
public class OrderItemOption extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_item_id", nullable = false)
    private OrderItem orderItem;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "store_id", nullable = false)
    private Store store;

    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    @Column(name = "total_price", nullable = false)
    private Integer totalPrice;

    @Column(name = "delivery_address", nullable = false)
    private String deliveryAddress;

    @Builder
    public OrderItemOption(User user, Store store, OrderStatus status, Integer totalPrice, String deliveryAddress) {
        this.user = user;
        this.store = store;
        this.status = status;
        this.totalPrice = totalPrice;
        this.deliveryAddress = deliveryAddress;
    }
}
