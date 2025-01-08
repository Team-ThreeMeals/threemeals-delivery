package com.threemeals.delivery.domain.order.repository;

import com.threemeals.delivery.domain.order.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {

    // JPQL: 사용자 주문과 연관된 데이터(가게, 주문 항목, 주문 옵션)를 Fetch Join으로 한 번에 로드
    @Query("SELECT DISTINCT o FROM Order o " +
            "JOIN FETCH o.store " +
            "JOIN FETCH o.orderItems oi " +
            "JOIN FETCH oi.orderItemOptions " +
            "WHERE o.user.id = :userId")
    List<Order> findByUserIdWithJoin(@Param("userId") Long userId);
}
