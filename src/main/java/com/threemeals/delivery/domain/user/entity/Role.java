package com.threemeals.delivery.domain.user.entity;


import com.threemeals.delivery.domain.common.exception.InvalidRequestException;
import lombok.Getter;

import java.util.Arrays;

import static com.threemeals.delivery.config.error.ErrorCode.INVALID_ROLE;

@Getter
public enum Role {
    USER,
    STORE_OWNER,

    ;

    public static Role of(String roleType) {
        return Arrays.stream(Role.values())
                .filter(role -> role.name().equalsIgnoreCase(roleType))
                .findFirst()
                .orElseThrow(() -> new InvalidRequestException(INVALID_ROLE));
    }

    public boolean isStoreOwner() {
        return this.name().equalsIgnoreCase(STORE_OWNER.name());
    }
}
