package com.threemeals.delivery.domain.store.dto.request;

import com.threemeals.delivery.domain.store.entity.Store;
import com.threemeals.delivery.domain.user.entity.User;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.time.LocalTime;

public record SaveStoreRequestDto (
        @NotEmpty(message = "가게 이름을 입력하세요")
        String storeName,
        @NotEmpty(message = "가게 사진을 첨부하세요")
        String storeProfileImgUrl,
        @NotEmpty(message = "가게 주소를 입력하세요")
        String address,
        @NotNull(message = "가게 오픈시간을 설정해주세요")
        LocalTime openingTime,
        @NotNull(message = "가게 마감시간을 설정해주세요")
        LocalTime closingTime,
        @NotNull(message = "배달 팁을 설정해주세요")
        Integer deliveryTip,
        @NotNull(message = "최소 주문금액을 설정해주세요")
        Integer minOrderPrice

) {

    public Store toEntity(User user) {
        return new Store(
                user,
                storeName,
                storeProfileImgUrl,
                address,
                openingTime,
                closingTime,
                deliveryTip,
                minOrderPrice
        );
    }

}
