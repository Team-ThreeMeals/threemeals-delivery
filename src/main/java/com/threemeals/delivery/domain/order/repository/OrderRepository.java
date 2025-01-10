package com.threemeals.delivery.domain.order.repository;

import com.threemeals.delivery.domain.order.entity.Order;
import com.threemeals.delivery.domain.order.exception.OrderNotFoundException;
import com.threemeals.delivery.domain.user.entity.User; // User 클래스 import
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {

    // 사용자 주문과 연관된 데이터(주문 항목, 주문 옵션)를 Fetch Join으로 로드
    @Query("SELECT DISTINCT o FROM Order o " +
            "JOIN FETCH o.orderItems oi " +
            "JOIN FETCH oi.orderItemOptions " +
            "WHERE o.user = :user")
    List<Order> findAllByUser(@Param("user") User user);

    default Order findOrderByOrderIdAndUserId(Long orderId, Long userId) {
        return findByIdAndUser_id(orderId, userId)
                .orElseThrow(() -> new OrderNotFoundException());
    }

    Optional<Order> findByIdAndUser_id(Long orderId, Long userId);
}
