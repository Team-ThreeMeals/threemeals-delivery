package com.threemeals.delivery.domain.order.entity;

import com.threemeals.delivery.domain.common.entity.BaseEntity;
import com.threemeals.delivery.domain.menu.entity.MenuOption;
import jakarta.persistence.*;
import lombok.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "order_item_option")
public class OrderItemOption extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_item_id", nullable = false)
    private OrderItem orderItem;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "menu_option_id", nullable = false)
    private MenuOption menuOption;

    @Column(name = "additional_price", nullable = false)
    private Integer additionalPrice;

    @Builder
    public OrderItemOption(OrderItem orderItem, MenuOption menuOption, Integer additionalPrice) {
        this.orderItem = orderItem;
        this.menuOption = menuOption;
        this.additionalPrice = additionalPrice;
    }

    public Long getMenuOptionId() {
        return this.menuOption.getId();
    }

    public String getMenuOptionName() {
        return this.menuOption.getMenuOptionName();
    }

    public int getAdditionalPrice() {
        return this.additionalPrice;
    }

    public void setOrderItem(OrderItem orderItem) {
        if (this.orderItem != null) {
            // 기존 연관 관계 제거
            this.orderItem.getOrderItemOptions().remove(this);
        }
        this.orderItem = orderItem;
        if (orderItem != null) {
            // 새로운 연관 관계 추가
            orderItem.getOrderItemOptions().add(this);
        }
    }

    public void setMenuOption(MenuOption menuOption) {
        this.menuOption = menuOption;
    }

}

