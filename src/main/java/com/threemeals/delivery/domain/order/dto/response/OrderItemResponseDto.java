package com.threemeals.delivery.domain.order.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.util.List;

@Data
@AllArgsConstructor
public class OrderItemResponseDto {
    private Long menuId; // 메뉴 ID
    private String menuName; // 메뉴 이름
    private Integer quantity; // 수량
    private Integer price; // 메뉴 가격
    private List<OrderItemOptionResponseDto> options; // 옵션 리스트
}