package com.threemeals.delivery.domain.order.entity;

public enum OrderStatus {
	CONFIRMING,   // 확인 중
	PREPARING,    // 조리 중
	DELIVERING,   // 배달 중
	COMPLETED,    // 완료됨
	CANCELLED     // 취소됨
}
