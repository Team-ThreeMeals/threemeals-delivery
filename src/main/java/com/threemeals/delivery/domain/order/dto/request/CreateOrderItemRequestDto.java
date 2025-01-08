package com.threemeals.delivery.domain.order.dto.request;

import lombok.Data;
import java.util.List;

@Data
public class CreateOrderItemRequestDto {
    private Long menuId;
    private Integer quantity;
    private List<CreateOrderItemOptionRequestDto> options;
}
