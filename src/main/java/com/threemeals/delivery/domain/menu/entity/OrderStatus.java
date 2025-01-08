package com.threemeals.delivery.domain.menu.entity;

public enum OrderStatus {
	PENDING, // 주문이 접수되었으나, 아직 처리되지는 않은 상태
	PREPARING, // 조리중
	DELIVERING, // 배달중,
	DELIVERED // 배달 완료

}
