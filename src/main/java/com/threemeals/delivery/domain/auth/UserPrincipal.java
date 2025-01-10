package com.threemeals.delivery.domain.auth;


import com.threemeals.delivery.domain.user.entity.Role;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class UserPrincipal {

    private final Long userId;
    private final Role role;

    public static UserPrincipal fromRequest(Long userId, Role role) {
        return new UserPrincipal(userId, role);
    }


}
