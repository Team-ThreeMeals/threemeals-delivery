package com.threemeals.delivery.domain.oauth.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class NaverUserInfoDto {
    private String email;
    private String name;
    private String profileImage;
    public static NaverUserInfoDto empty() {
        return NaverUserInfoDto.builder().build();
    }

    public boolean isValid() {
        return email != null && !email.isEmpty();
    }
}

