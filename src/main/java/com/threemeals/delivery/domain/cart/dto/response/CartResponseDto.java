package com.threemeals.delivery.domain.cart.dto.response;

import com.threemeals.delivery.domain.cart.dto.request.AddCartItemRequestDto;
import lombok.Data;
import lombok.AllArgsConstructor;
import java.util.List;

@Data
@AllArgsConstructor
public class CartResponseDto {
    private Long storeId; // 가게 ID
    private List<CartItemResponseDto> cartItems; // 장바구니 항목 리스트
}

