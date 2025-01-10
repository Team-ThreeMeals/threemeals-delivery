package com.threemeals.delivery.domain.order.entity;

import com.threemeals.delivery.domain.common.entity.BaseEntity;
import com.threemeals.delivery.domain.store.entity.Store;
import com.threemeals.delivery.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.HashSet;
import java.util.Set;
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "orders")
public class Order extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", updatable = false)
	private Long id;

	@Column(name = "user_id", nullable = false)
	private Long userId;

	@Column(name = "store_id", nullable = false)
	private Long storeId;

	@Column(name = "status", nullable = false)
	@Enumerated(EnumType.STRING)
	private OrderStatus status;

	@Column(name = "total_price", nullable = false)
	private Integer totalPrice;

	@Column(name = "delivery_address", nullable = false)
	private String deliveryAddress;

	@OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
	private Set<OrderItem> orderItems = new HashSet<>();

	@Builder
	public Order(Long userId, Long storeId, OrderStatus status, Integer totalPrice, String deliveryAddress) {
		this.userId = userId;
		this.storeId = storeId;
		this.status = status;
		this.totalPrice = totalPrice;
		this.deliveryAddress = deliveryAddress;
	}



	// 주문 상태 업데이트 메서드
	public void updateStatus(OrderStatus newStatus) {
		this.status = newStatus;
	}

	public void setTotalPrice(Integer totalPrice) {
		this.totalPrice = totalPrice;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public void setStatus(OrderStatus status) {
		this.status = status;
	}

	public void setDeliveryAddress(String deliveryAddress) {
		this.deliveryAddress = deliveryAddress;
	}

}
