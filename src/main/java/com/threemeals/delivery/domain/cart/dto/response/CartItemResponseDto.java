package com.threemeals.delivery.domain.cart.dto.response;

import com.threemeals.delivery.domain.cart.dto.request.AddCartItemOptionRequestDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import java.util.List;

@Data
@AllArgsConstructor
public class CartItemResponseDto {
        private Long menuId; // 메뉴 ID
        private Integer quantity; // 수량
        private List<CartItemOptionResponseDto> options; // 옵션 리스트
}