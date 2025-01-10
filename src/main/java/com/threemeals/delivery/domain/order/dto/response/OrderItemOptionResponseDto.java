package com.threemeals.delivery.domain.order.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.util.List;

@Data
@AllArgsConstructor
public class OrderItemOptionResponseDto {
    private Long optionId; // 옵션 ID
    private String optionName; // 옵션 이름
    private Integer additionalPrice; // 추가 가격
}