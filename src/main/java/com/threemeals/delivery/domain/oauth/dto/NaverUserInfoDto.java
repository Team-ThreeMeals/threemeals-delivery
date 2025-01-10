package com.threemeals.delivery.domain.oauth.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NaverUserInfoDto {
    private String email;
    private String name;
    private String profileImage;
}

