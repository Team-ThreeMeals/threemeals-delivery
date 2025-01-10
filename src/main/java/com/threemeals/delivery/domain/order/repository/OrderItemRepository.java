package com.threemeals.delivery.domain.order.repository;

import com.threemeals.delivery.domain.order.entity.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
    // 필요 시 사용자 정의 쿼리 추가 가능
}
