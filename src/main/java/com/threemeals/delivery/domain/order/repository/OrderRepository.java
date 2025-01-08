package com.threemeals.delivery.domain.order.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.threemeals.delivery.domain.order.entity.Order;
import com.threemeals.delivery.domain.order.exception.OrderNotFoundException;

public interface OrderRepository extends JpaRepository<Order, Long> {

	default Order findOrderByOrderIdAndUserId(Long orderId, Long userId) {
		return findByIdAndUser_id(orderId, userId)
			.orElseThrow(() -> new OrderNotFoundException());
	}

	Optional<Order> findByIdAndUser_id(Long orderId, Long userId);
}