package com.threemeals.delivery.domain.cart.entity;

import com.threemeals.delivery.domain.common.entity.BaseEntity;
import com.threemeals.delivery.domain.store.entity.Store;
import com.threemeals.delivery.domain.user.entity.User;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "cart")
public class Cart extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", updatable = false)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id", nullable = false)
	private User user;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "store_id", nullable = false)
	private Store store;

	@Column(name = "total_price", nullable = false)
	private Integer totalPrice;

	@OneToMany(mappedBy = "cart", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<CartItem> cartItems = new ArrayList<>();

	@Builder
	public Cart(User user, Store store, Integer totalPrice) {
		this.user = user;
		this.store = store;
		this.totalPrice = totalPrice;
	}
	public void setStore(Store store) {
		this.store = store;
	}

	public void setTotalPrice(Integer totalPrice) {
		this.totalPrice = totalPrice;
	}
}
