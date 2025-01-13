package com.threemeals.delivery.domain.oauth.dto;

import lombok.Getter;

@Getter
public class OAuthResponseDto {
    private final boolean success;
    private final String message;
    private final String token;

    public OAuthResponseDto(boolean success, String message, String token) {
        this.success = success;
        this.message = message;
        this.token = token;
    }

}
