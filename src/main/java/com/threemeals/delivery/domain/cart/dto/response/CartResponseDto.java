package com.threemeals.delivery.domain.cart.dto.response;

import com.threemeals.delivery.domain.cart.entity.Cart;
import com.threemeals.delivery.domain.cart.entity.CartItem;
import com.threemeals.delivery.domain.cart.entity.CartItemOption;
import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.stream.Collectors;

@Data
@Builder
public class CartResponseDto {

    private Long userId;
    private Long storeId;
    private Integer totalPrice;
    private List<CartItemResponseDto> items;

    public static CartResponseDto from(Cart cart) {
        return CartResponseDto.builder()
                .userId(cart.getUser().getId())
                .storeId(cart.getStore().getId())
                .totalPrice(cart.getTotalPrice())
                .items(cart.getCartItems().stream()
                        .map(CartItemResponseDto::from)
                        .collect(Collectors.toList()))
                .build();
    }

    @Data
    @Builder
    public static class CartItemResponseDto {
        private Long menuId;
        private Integer quantity;
        private Integer totalPrice;
        private List<OptionResponseDto> options;

        public static CartItemResponseDto from(CartItem cartItem) {
            return CartItemResponseDto.builder()
                    .menuId(cartItem.getMenu().getId())
                    .quantity(cartItem.getQuantity())
                    .totalPrice(cartItem.getMenu().getPrice() * cartItem.getQuantity())
                    .options(cartItem.getCartItemOptions().stream()
                            .map(OptionResponseDto::from)
                            .collect(Collectors.toList()))
                    .build();
        }
    }

    @Data
    @Builder
    public static class OptionResponseDto {
        private Long optionId;
        private Integer additionalPrice;

        public static OptionResponseDto from(CartItemOption option) {
            return OptionResponseDto.builder()
                    .optionId(option.getOption().getId())
                    .additionalPrice(option.getAdditionalPrice())
                    .build();
        }
    }
}
