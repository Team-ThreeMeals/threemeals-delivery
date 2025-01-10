package com.threemeals.delivery.domain.cart.entity;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.threemeals.delivery.domain.store.entity.Store;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor // 기본 생성자를 Lombok으로 추가
@JsonInclude(JsonInclude.Include.NON_NULL) // null 필드 제외
public class Cart implements Serializable {

	private Long id; // UUID 또는 수동 생성 ID
	private Long userId;
	private Store store;
	private Integer totalPrice;
	private List<CartItem> cartItems = new ArrayList<>();

	@Builder
	public Cart(Long id, Long userId, Store store, Integer totalPrice, List<CartItem> cartItems) {
		this.id = id;
		this.userId = userId;
		this.store = store;
		this.totalPrice = totalPrice;
		this.cartItems = cartItems != null ? cartItems : new ArrayList<>();
	}

	@JsonCreator // Jackson 역직렬화를 위한 생성자
	public static Cart create(
			@JsonProperty("id") Long id,
			@JsonProperty("userId") Long userId,
			@JsonProperty("store") Store store,
			@JsonProperty("totalPrice") Integer totalPrice,
			@JsonProperty("cartItems") List<CartItem> cartItems) {
		return Cart.builder()
				.id(id)
				.userId(userId)
				.store(store)
				.totalPrice(totalPrice)
				.cartItems(cartItems)
				.build();
	}

	public void setStore(Store store) {
		this.store = store;
	}

	public void updateTotalPrice() {
		this.totalPrice = cartItems.stream()
				.mapToInt(CartItem::getTotalPrice)
				.sum();
	}

	public void addCartItem(CartItem cartItem) {
		this.cartItems.add(cartItem);
		updateTotalPrice();
	}

	public void removeCartItem(CartItem cartItem) {
		this.cartItems.remove(cartItem);
		updateTotalPrice();
	}
}
