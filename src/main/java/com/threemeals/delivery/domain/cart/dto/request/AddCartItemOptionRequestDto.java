package com.threemeals.delivery.domain.cart.dto.request;

import lombok.Data;

import java.util.List;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

@Data
public class AddCartItemOptionRequestDto {
    private Long optionId; // 옵션 ID
    private Integer additionalPrice; // 추가 금액

    @JsonCreator
    public AddCartItemOptionRequestDto(
            @JsonProperty("optionId") Long optionId,
            @JsonProperty("additionalPrice") Integer additionalPrice) {
        this.optionId = optionId;
        this.additionalPrice = additionalPrice;
    }

    // Getters and setters
    public Long getOptionId() {
        return optionId;
    }

    public void setOptionId(Long optionId) {
        this.optionId = optionId;
    }

    public Integer getAdditionalPrice() {
        return additionalPrice;
    }

    public void setAdditionalPrice(Integer additionalPrice) {
        this.additionalPrice = additionalPrice;
    }

}
