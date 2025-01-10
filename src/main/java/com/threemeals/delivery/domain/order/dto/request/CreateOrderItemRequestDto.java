package com.threemeals.delivery.domain.order.dto.request;

import lombok.Data;
import lombok.Getter;

import java.util.List;

@Data
@Getter
public class CreateOrderItemRequestDto {
    private Long menuId;
    private Integer quantity;
    private List<CreateOrderItemOptionRequestDto> options;
}
