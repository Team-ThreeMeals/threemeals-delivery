package com.threemeals.delivery.domain.cart.dto.request;

import lombok.Data;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

@Data
public class AddCartItemRequestDto {
    private Long menuId; // 메뉴 ID
    private Integer quantity; // 수량
    private List<AddCartItemOptionRequestDto> options; // 옵션 리스트

    @JsonCreator
    public AddCartItemRequestDto(
            @JsonProperty("menuId") Long menuId,
            @JsonProperty("quantity") Integer quantity,
            @JsonProperty("options") List<AddCartItemOptionRequestDto> options) {
        this.menuId = menuId;
        this.quantity = quantity;
        this.options = options;
    }

    // Getters and setters
    public Long getMenuId() {
        return menuId;
    }

    public void setMenuId(Long menuId) {
        this.menuId = menuId;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public List<AddCartItemOptionRequestDto> getOptions() {
        return options;
    }

    public void setOptions(List<AddCartItemOptionRequestDto> options) {
        this.options = options;
    }
}
